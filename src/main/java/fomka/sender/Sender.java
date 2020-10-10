package fomka.sender;

import java.io.File;
import java.io.IOException;

public interface Sender {

    void send(File file) throws IOException;

    void shutdown();
}
