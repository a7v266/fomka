package fomka.receiver;

import java.io.File;

public class PartFolderException extends ReceiverException {

    public PartFolderException(File partFolder) {
        super("Can't create folder " + partFolder.getAbsolutePath());
    }
}
