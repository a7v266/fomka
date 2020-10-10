package fomka.sender;

import fomka.receiver.PartFolderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SenderImpl implements Sender {

    private static final Logger LOG = LoggerFactory.getLogger(SenderImpl.class);

    private static final String PART_MESSAGE = "file=%s;part=%d";
    private static final String SIZE_MESSAGE = "file=%s;size=%d";
    private static final int POOL_SIZE = 10;
    private static final int SHUTDOWN_TIMEOUT = 60;
    private static final String PART_NAME = "%s-%d.jpeg";
    private static final String PART_FOLDER = "%s.jpeg";
    private static final String IGNORE_FILE = "^.*\\.jpeg$";
    private static final String SUBJECT_FORMAT = "%s;%s";

    private final Authenticator authenticator;
    private final InternetAddress sourceAddress;
    private final InternetAddress[] destinationAddresses;
    private final Properties properties;
    private final ExecutorService executorService;
    private final String protocol;
    private final byte[] cover;
    private final int uploadSize;
    private final Path uploadPath;

    public SenderImpl(SenderConfig config) throws MessagingException, IOException {
        this.authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(config.getUsername(), config.getPassword());
            }
        };
        sourceAddress = new InternetAddress(config.getSourceEmail());
        destinationAddresses = InternetAddress.parse(config.getDestinationEmails());
        properties = new Properties();
        properties.put("mail.smtp.host", config.getSmtpHost());
        properties.put("mail.smtp.port", config.getSmtpPort());
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", config.getSmtpPort());
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        executorService = Executors.newFixedThreadPool(POOL_SIZE);
        protocol = config.getProtocol();
        cover = Files.readAllBytes(config.getCoverPath());
        uploadSize = config.getUploadSize();
        uploadPath = config.getUploadPath();
    }

    @Override
    public void send(File file) throws IOException {
        File partFolder = new File(String.format(PART_FOLDER, file.getAbsolutePath()));
        if (partFolder.exists() || partFolder.mkdir()) {
            try (FileInputStream fileStream = new FileInputStream(file)) {
                int index = 1;
                String fileName = file.getName();
                byte[] fileData = new byte[uploadSize];
                for (int length; (length = fileStream.read(fileData)) != -1; index++) {
                    String partName = String.format(PART_NAME, fileName, index);
                    String partPath = uploadPath.resolve(partFolder.toPath()).resolve(partName).toString();
                    try (FileOutputStream partStream = new FileOutputStream(partPath)) {
                        partStream.write(cover);
                        partStream.write(encode(fileData), 0, length);
                    }
                    sendPart(partPath);
                }
                sendSize(fileName, index - 1);
            }
        } else {
            throw new PartFolderException(partFolder);
        }
    }

    private byte[] encode(byte[] data) {
        for (int item = 0; item < data.length; item++) {
            data[item] = (byte) ~data[item];
        }
        return data;
    }

    private void sendPart(String partPath) {
        executorService.submit(() -> {
            try {
                File partFile = new File(partPath);
                String partName = partFile.getName();
                MimeBodyPart filePart = new MimeBodyPart();
                filePart.setFileName(partName);
                filePart.setDataHandler(new DataHandler(new FileDataSource(partFile)));
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(filePart);
                Message message = createMessage(partName);
                message.setContent(multipart);
                Transport.send(message);
            } catch (Exception exception) {
                LOG.error("Email send error: {}", exception.getMessage(), exception);
            }
        });
    }

    private void sendSize(String fileName, int size) {
        executorService.submit(() -> {
            try {
                Message message = createMessage(fileName);
                Multipart multipart = new MimeMultipart();
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(String.format(SIZE_MESSAGE, fileName, size));
                multipart.addBodyPart(textPart);
                message.setContent(multipart);
                Transport.send(message);
            } catch (Exception exception) {
                LOG.error("Email send error: {}", exception.getMessage(), exception);
            }
        });
    }

    private Message createMessage(String fileName) throws MessagingException {
        Session session = Session.getInstance(properties, authenticator);
        Message message = new MimeMessage(session);
        message.setFrom(sourceAddress);
        message.setRecipients(Message.RecipientType.TO, destinationAddresses);
        message.setSubject(String.format(SUBJECT_FORMAT, protocol, fileName));
        return message;
    }

    @Override
    public void shutdown() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MINUTES);
        } catch (Exception exception) {
            LOG.error("Sender shutdown exeption", exception);
        }
    }
}