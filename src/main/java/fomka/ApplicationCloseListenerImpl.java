package fomka;

import fomka.receiver.Receiver;
import fomka.sender.Sender;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ApplicationCloseListenerImpl extends WindowAdapter implements ApplicationCloseListener {

    private Sender sender;
    private Receiver receiver;

    @Override
    public void setSender(Sender sender) {
        this.sender = sender;
    }

    @Override
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void windowClosing(WindowEvent event) {
        if (sender != null) {
            sender.shutdown();
        }
        if (receiver != null) {
            receiver.shutdown();
        }
    }
}
