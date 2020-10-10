package fomka.receiver;

import java.nio.file.Path;

public interface ReceiverConfig extends ConnectionConfig {

    String getProtocol();

    Path getDownloadPath();

    int getLookupDepth();
}
