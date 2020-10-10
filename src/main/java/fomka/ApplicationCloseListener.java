package fomka;

import fomka.receiver.Receiver;
import fomka.sender.Sender;
import fomka.ui.SwingListener;

public interface ApplicationCloseListener extends SwingListener {

    void setSender(Sender sender);

    void setReceiver(Receiver receiver);
}
