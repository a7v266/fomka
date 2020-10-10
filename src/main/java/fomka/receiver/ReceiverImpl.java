package fomka.receiver;

import fomka.receiver.collector.Collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ReceiverImpl extends Thread implements Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(ReceiverImpl.class);

    private static final int POOL_SIZE = 20;
    private static final int SHUTDOWN_TIMEOUT = 60;
    private static final String ATTACHMENT_PATTERN = "^(.+?)-(\\d+?)\\.jpeg$";
    private static final String FOLDER_FORMAT = "%s.jpeg";
    private static final String HEADER_DELIMITER = ";";
    private static final String PARAMETER_DELIMITER = "=";
    private static final String MIME_IMAGE = "image/jpeg";
    private static final String PARAMETER_FILE_NAME = "file";
    private static final String PARAMETER_SIZE = "size";

    private final ConnectionPool connectionPool;
    private final ExecutorService executorService;
    private final Pattern attachmentPattern;
    private final Map<String, Collector> collectors;
    private final Path downloadPath;
    private final String protocol;
    private final int lookupDepth;
    private final Map<String, Connection> connections;

    public ReceiverImpl(ReceiverConfig config) throws MessagingException, IOException, InterruptedException {
        protocol = config.getProtocol();
        connectionPool = new ConnectionPool(config);
        executorService = Executors.newFixedThreadPool(POOL_SIZE);
        attachmentPattern = Pattern.compile(ATTACHMENT_PATTERN);
        collectors = new ConcurrentHashMap<>();
        downloadPath = config.getDownloadPath();
        connections = new ConcurrentHashMap<>();
        lookupDepth = config.getLookupDepth();
    }

    @Override
    public void start() {
        super.start();
    }

    public Map<Integer, String> getMessageList() throws InterruptedException, MessagingException {
        Map<Integer, String> messages = new TreeMap<>();
        try (Connection connection = connectionPool.getConnection()) {
            for (int index = connection.getMessageCount(); index > 0; index--) {
                Message message = connection.getMessage(index);
                messages.put(index, message.getSubject());
            }
        }
        return messages;
    }

    @Override
    public void run() {
        LOG.info("Start receiver");
        try {
            for (; ; ) {
                while (receive()) ;
                Thread.sleep(1000);
            }
        } catch (Exception exception) {
            LOG.error(exception.getMessage(), exception);
        } finally {
            shutdown();
        }
    }

    private boolean receive() throws InterruptedException, MessagingException {
        Connection connection = connectionPool.getConnection();
        int messageCount = connection.getMessageCount();
        int indexLimit = messageCount > lookupDepth ? messageCount - lookupDepth : 0;
        for (int index = messageCount; index > indexLimit; index--) {
            Message message = connection.getMessage(index);
            String subject = message.getSubject();
            if (subject != null && protocol.equals(subject.split(";")[0])) {
                if (connection == connections.computeIfAbsent(subject, key -> {
                    executorService.submit(() -> downloadMessage(connection, message));
                    return connection;
                })) {
                    return true;
                }
            }
        }
        return false;
    }

    private void downloadMessage(Connection connection, Message message) {
        try {
            LOG.info("Download message {}", message.getSubject());
            Multipart multipart = (Multipart) message.getContent();
            for (int index = 0; index < multipart.getCount(); index++) {
                BodyPart bodyPart = multipart.getBodyPart(index);
                String contentType = bodyPart.getContentType();
                if (contentType.startsWith(MIME_IMAGE)) {
                    processImagePart(bodyPart);
                } else {
                    processTextPart(bodyPart);
                }
            }
        } catch (Exception exception) {
            LOG.error("Error download message", exception);
        } finally {
            connection.close();
        }
    }

    private void processImagePart(BodyPart bodyPart) throws MessagingException, IOException {
        DataHandler dataHandler = bodyPart.getDataHandler();
        String partName = bodyPart.getFileName();
        Matcher partNameMatcher = attachmentPattern.matcher(partName);
        if (partNameMatcher.find()) {
            String fileName = partNameMatcher.group(1);
            int partNumber = Integer.parseInt(partNameMatcher.group(2));
            Path partFolderPath = downloadPath.resolve(String.format(FOLDER_FORMAT, fileName));
            File partFolder = partFolderPath.toFile();
            if (partFolder.exists() || partFolder.mkdir()) {
                Path partFilePath = partFolderPath.resolve(partName);
                try (InputStream inputStream = dataHandler.getInputStream()) {
                    Files.copy(inputStream, partFilePath, StandardCopyOption.REPLACE_EXISTING);
                }
                Collector collector = collectors.computeIfAbsent(fileName, key -> new Collector(key, downloadPath));
                collector.addPart(partNumber, partFilePath);
            } else {
                throw new PartFolderException(partFolder);
            }
        } else {
            throw new PartParserException(partName);
        }
    }

    private void processTextPart(BodyPart bodyPart) throws IOException, MessagingException {
        String text = (String) bodyPart.getContent();
        String[] headers = text.split(HEADER_DELIMITER);
        Map<String, String> parameters = new HashMap<>();
        for (String header : headers) {
            String[] parameter = header.split(PARAMETER_DELIMITER);
            parameters.put(parameter[0], parameter[1]);
        }
        String size = parameters.get(PARAMETER_SIZE);
        if (size != null) {
            String fileName = parameters.get(PARAMETER_FILE_NAME);
            Collector collector = collectors.computeIfAbsent(fileName, key -> new Collector(key, downloadPath));
            collector.addSize(Integer.parseInt(size));
        }
    }

    public void shutdown() {
        LOG.info("Shutdown receiver");
        Stream.of(shutdownExecutorService(), shutdownConnectionPool())
                .filter(Objects::nonNull)
                .forEach(exception -> LOG.error(exception.getMessage(), exception));
    }

    private Exception shutdownExecutorService() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MINUTES);
        } catch (Exception exception) {
            return exception;
        }
        return null;
    }

    private Exception shutdownConnectionPool() {
        try {
            connectionPool.shutdown();
        } catch (Exception exception) {
            return exception;
        }
        return null;
    }
}
