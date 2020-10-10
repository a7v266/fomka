package fomka.ui;

import java.awt.Component;

public interface SwingComponent {

    default Component unwrap() {
        return (Component) this;
    }
}
