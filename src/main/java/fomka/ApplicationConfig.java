package fomka;

import fomka.receiver.ReceiverConfig;
import fomka.sender.SenderConfig;

import java.nio.file.Path;

public interface ApplicationConfig extends ReceiverConfig, SenderConfig {

    String getSourceEmail();

    String getDestinationEmails();

    String getSmtpHost();

    String getSmtpPort();

    String getImapHost();

    String getImapPort();

    String getUsername();

    String getPassword();

    int getUploadSize();

    Path getUploadPath();

    Path getDownloadPath();

    Path getCoverPath();

    String getProtocol();

    int getLookupDepth();
}
