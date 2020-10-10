package fomka.ui;

import javax.swing.Action;

public interface SwingAction {

    default Action unwrap() {
        return (Action) this;
    }
}
