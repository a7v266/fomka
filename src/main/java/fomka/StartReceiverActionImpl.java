package fomka;

import fomka.receiver.Receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

public class StartReceiverActionImpl extends AbstractAction implements StartReceiverAction {

    private static final Logger LOG = LoggerFactory.getLogger(StartReceiverActionImpl.class);

    private Receiver receiver;

    public StartReceiverActionImpl(String name) {
        super(name);
    }

    @Override
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EventQueue.invokeLater(() -> {
            try {
                receiver.start();
                setEnabled(false);
            } catch (Exception exception) {
                LOG.error("Start receiver exception", exception);
            }
        });
    }
}
