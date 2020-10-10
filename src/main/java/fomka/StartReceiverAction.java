package fomka;

import fomka.receiver.Receiver;
import fomka.ui.SwingAction;

public interface StartReceiverAction extends SwingAction {

    void setReceiver(Receiver receiver);

}
