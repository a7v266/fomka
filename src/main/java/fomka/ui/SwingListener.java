package fomka.ui;

import java.awt.event.WindowListener;

public interface SwingListener {

    default WindowListener unwrap() {
        return (WindowListener) this;
    }
}
