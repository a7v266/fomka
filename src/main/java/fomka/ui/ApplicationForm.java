package fomka.ui;

import fomka.ApplicationCloseListener;
import fomka.FileChooserAction;
import fomka.StartReceiverAction;

public interface ApplicationForm extends SwingComponent, Initializing {

    void setStartReceiverAction(StartReceiverAction startReceiverAction);

    void setFileChooserAction(FileChooserAction fileChooserAction);

    void setApplicationCloseListener(ApplicationCloseListener closeListener);

    void setVisible(boolean visible);
}
