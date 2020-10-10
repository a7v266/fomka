package fomka;

import fomka.sender.Sender;
import fomka.ui.FileChooserDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.File;

public class FileChooserActionImpl extends AbstractAction implements FileChooserAction {

    private static final Logger LOG = LoggerFactory.getLogger(FileChooserActionImpl.class);

    private FileChooserDialog dialog;
    private Sender sender;

    public FileChooserActionImpl(String name) {
        super(name);
    }

    @Override
    public void setFileChooserDialog(FileChooserDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void setSender(Sender sender) {
        this.sender = sender;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EventQueue.invokeLater(() -> {
            File file = dialog.selectedFile();
            if (file == null) {
                return;
            }
            try {
                sender.send(file);

            } catch (Exception exception) {
                LOG.error("Send file exception", exception);
            }
        });
    }
}
