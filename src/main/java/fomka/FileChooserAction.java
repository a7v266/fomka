package fomka;

import fomka.sender.Sender;
import fomka.ui.FileChooserDialog;
import fomka.ui.SwingAction;

public interface FileChooserAction extends SwingAction {

    void setFileChooserDialog(FileChooserDialog dialog);

    void setSender(Sender sender);
}
