package fomka.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

public class Connection implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Connection.class);

    private final Store store;
    private final Folder folder;

    public Connection(ConnectionConfig config) throws MessagingException {
        Properties properties = new Properties();
        properties.setProperty("mail.store.protocol", "imaps");
        properties.setProperty("mail.imaps.host", config.getImapHost());
        properties.setProperty("mail.imaps.port", config.getImapPort());
        properties.setProperty("mail.imaps.starttls.enable", "true");
        Session session = Session.getInstance(properties, null);
        session.setDebug(false);
        store = session.getStore();
        store.connect(config.getUsername(), config.getPassword());
        folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
    }

    public int getMessageCount() throws MessagingException {
        return folder.getMessageCount();
    }

    public Message getMessage(int index) throws MessagingException {
        return folder.getMessage(index);
    }

    @Override
    public void close() {
        try {
            folder.close();
        } catch (MessagingException exception) {
            LOG.error("Close folder exception", exception);
        }
        try {
            store.close();
        } catch (Exception exception) {
            LOG.error("Close store exception", exception);
        }
    }
}
