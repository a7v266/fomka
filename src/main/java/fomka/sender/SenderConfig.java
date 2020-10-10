package fomka.sender;

import java.nio.file.Path;

public interface SenderConfig {

    String getSmtpHost();

    String getSmtpPort();

    String getUsername();

    String getPassword();

    String getSourceEmail();

    String getDestinationEmails();

    String getProtocol();

    Path getCoverPath();

    Path getUploadPath();

    int getUploadSize();
}
