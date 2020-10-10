package fomka.ui;


import fomka.ApplicationCloseListener;
import fomka.FileChooserAction;
import fomka.StartReceiverAction;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ApplicationFormImpl extends JFrame implements ApplicationForm {

    private final JPanel controlPanel;
    private final JButton startReceiverButton;
    private final JButton fileChooserButton;
    private StartReceiverAction startReceiverAction;
    private FileChooserAction fileChooserAction;
    private ApplicationCloseListener applicationCloseListener;

    public ApplicationFormImpl() {
        controlPanel = new JPanel();
        startReceiverButton = new JButton();
        fileChooserButton = new JButton();
        controlPanel.add(startReceiverButton);
        controlPanel.add(fileChooserButton);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        add(controlPanel);
    }

    @Override
    public void init() {
        startReceiverButton.setAction(startReceiverAction.unwrap());
        fileChooserButton.setAction(fileChooserAction.unwrap());
        addWindowListener(applicationCloseListener.unwrap());
    }

    @Override
    public void setStartReceiverAction(StartReceiverAction startReceiverAction) {
        this.startReceiverAction = startReceiverAction;
    }

    @Override
    public void setFileChooserAction(FileChooserAction fileChooserAction) {
        this.fileChooserAction = fileChooserAction;
    }

    @Override
    public void setApplicationCloseListener(ApplicationCloseListener applicationCloseListener) {
        this.applicationCloseListener = applicationCloseListener;
    }
}
