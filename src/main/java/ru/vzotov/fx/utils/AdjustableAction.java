package ru.vzotov.fx.utils;

import javafx.beans.property.BooleanProperty;

public interface AdjustableAction {

    BooleanProperty enabledProperty();

    boolean isEnabled();

    void setEnabled(boolean enabled);
}
