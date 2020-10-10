package fomka.receiver.collector;

import fomka.receiver.ReceiverException;

public class CollectorSizeAlreadySetException extends ReceiverException {

    public CollectorSizeAlreadySetException() {
        super("Collector size already set");
    }
}
