package fomka;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ApplicationConfigImpl implements ApplicationConfig {

    private static final String FOM_PROPERTIES = "fom.properties";
    private static final String PROPERTY_PROTOCOL = "protocol";
    private static final String PROPERTY_SOURCE_EMAIL = "email.source";
    private static final String PROPERTY_DESTINATION_EMAILS = "email.destination";
    private static final String PROPERTY_SMTP_HOST = "smtp.host";
    private static final String PROPERTY_SMTP_PORT = "smtp.port";
    private static final String PROPERTY_IMAP_HOST = "imap.host";
    private static final String PROPERTY_IMAP_PORT = "imap.port";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";
    private static final String PROPERTY_UPLOAD_PATH = "upload.path";
    private static final String PROPERTY_UPLOAD_SIZE = "upload.size";
    private static final String PROPERTY_DOWNLOAD_PATH = "download.path";
    private static final String PROPERTY_COVER_PATH = "cover.path";
    private static final String PROPERTY_LOOKUP_DEPTH = "lookup.depth";

    private final String sourceEmail;
    private final String destinationEmails;
    private final String smtpHost;
    private final String smtpPort;
    private final String imapHost;
    private final String imapPort;
    private final String username;
    private final String password;
    private final int uploadSize;
    private final Path uploadPath;
    private final Path downloadPath;
    private final Path coverPath;
    private final String protocol;
    private final int lookupDepth;

    public ApplicationConfigImpl() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(FOM_PROPERTIES));
        protocol = properties.getProperty(PROPERTY_PROTOCOL);
        sourceEmail = properties.getProperty(PROPERTY_SOURCE_EMAIL);
        destinationEmails = properties.getProperty(PROPERTY_DESTINATION_EMAILS);
        smtpHost = properties.getProperty(PROPERTY_SMTP_HOST);
        smtpPort = properties.getProperty(PROPERTY_SMTP_PORT);
        imapHost = properties.getProperty(PROPERTY_IMAP_HOST);
        imapPort = properties.getProperty(PROPERTY_IMAP_PORT);
        username = properties.getProperty(PROPERTY_USERNAME);
        password = properties.getProperty(PROPERTY_PASSWORD);
        uploadSize = 1024 * 1024 * Integer.parseInt(properties.getProperty(PROPERTY_UPLOAD_SIZE));
        uploadPath = Paths.get(properties.getProperty(PROPERTY_UPLOAD_PATH));
        downloadPath = Paths.get(properties.getProperty(PROPERTY_DOWNLOAD_PATH));
        coverPath = Paths.get(properties.getProperty(PROPERTY_COVER_PATH));
        lookupDepth = Integer.parseInt(properties.getProperty(PROPERTY_LOOKUP_DEPTH));
    }

    @Override
    public String getSourceEmail() {
        return sourceEmail;
    }

    @Override
    public String getDestinationEmails() {
        return destinationEmails;
    }

    @Override
    public String getSmtpHost() {
        return smtpHost;
    }

    @Override
    public String getSmtpPort() {
        return smtpPort;
    }

    @Override
    public String getImapHost() {
        return imapHost;
    }

    @Override
    public String getImapPort() {
        return imapPort;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public int getUploadSize() {
        return uploadSize;
    }

    @Override
    public Path getUploadPath() {
        return uploadPath;
    }

    @Override
    public Path getDownloadPath() {
        return downloadPath;
    }

    @Override
    public Path getCoverPath() {
        return coverPath;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public int getLookupDepth() {
        return lookupDepth;
    }
}
