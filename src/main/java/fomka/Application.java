package fomka;

import fomka.receiver.Receiver;
import fomka.receiver.ReceiverImpl;
import fomka.sender.Sender;
import fomka.sender.SenderImpl;
import fomka.ui.ApplicationForm;
import fomka.ui.ApplicationFormImpl;
import fomka.ui.FileChooserDialog;
import fomka.ui.FileChooserDialogImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.awt.EventQueue;
import java.io.IOException;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private final ApplicationForm applicationForm = new ApplicationFormImpl();

    public Application() throws IOException, MessagingException, InterruptedException {
        ApplicationConfig applicationConfig = new ApplicationConfigImpl();
        Receiver receiver = new ReceiverImpl(applicationConfig);
        Sender sender = new SenderImpl(applicationConfig);
        StartReceiverAction startReceiverAction = new StartReceiverActionImpl("Start receiver");
        FileChooserAction fileChooserAction = new FileChooserActionImpl("Choose file");
        FileChooserDialog fileChooserDialog = new FileChooserDialogImpl();
        fileChooserDialog.setApplicationForm(applicationForm);
        fileChooserAction.setFileChooserDialog(fileChooserDialog);
        fileChooserAction.setSender(sender);
        startReceiverAction.setReceiver(receiver);
        applicationForm.setStartReceiverAction(startReceiverAction);
        applicationForm.setFileChooserAction(fileChooserAction);
        ApplicationCloseListener applicationCloseListener = new ApplicationCloseListenerImpl();
        applicationCloseListener.setSender(sender);
        applicationCloseListener.setReceiver(receiver);
        applicationForm.setApplicationCloseListener(applicationCloseListener);
        applicationForm.init();
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            try {
                Application application = new Application();
                application.run();

            } catch (Exception exception) {
                LOG.error("Application exception", exception);
            }
        });
    }

    public void run() {
        applicationForm.setVisible(true);
    }
}
