package ru.vzotov.fx.utils;

import javafx.scene.control.skin.TextFieldSkin;

public class ValueTextFieldSkin<S, T extends ValueTextField<S>> extends TextFieldSkin {
    public ValueTextFieldSkin(T control) {
        super(control);
        registerChangeListener(control.textProperty(), it -> {
            String text = control.getText();
            if (text == null || text.isBlank()) {
                control.setValue(null);
            } else {
                S value = control.getConverter().fromString(control.getText());
                if (value != null) {
                    control.setValue(value);
                }
            }
        });

        registerChangeListener(control.valueProperty(), it -> {
            updateText();
        });
        updateText();
    }

    private void updateText() {
        ValueTextField<S> control = (ValueTextField<S>) getSkinnable();
        S value = control.getValue();
        if (value == null) {
            control.setText("");
        } else {
            control.setText(control.getConverter().toString(value));
        }
    }
}
