package fomka.ui;

import java.io.File;

public interface FileChooserDialog {

    void setApplicationForm(ApplicationForm applicationForm);

    File selectedFile();
}
