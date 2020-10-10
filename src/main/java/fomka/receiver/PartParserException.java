package fomka.receiver;

public class PartParserException extends ReceiverException {

    public PartParserException(String attachmentName) {
        super("Can't parse attachment name " + attachmentName);
    }
}
