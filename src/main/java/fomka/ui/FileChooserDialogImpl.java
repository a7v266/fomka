package fomka.ui;

import javax.swing.JFileChooser;
import java.io.File;

public class FileChooserDialogImpl extends JFileChooser implements FileChooserDialog {

    private ApplicationForm applicationForm;

    @Override
    public void setApplicationForm(ApplicationForm applicationForm) {
        this.applicationForm = applicationForm;
    }

    @Override
    public File selectedFile() {
        showDialog(applicationForm.unwrap(), "Select");
        return getSelectedFile();
    }
}
