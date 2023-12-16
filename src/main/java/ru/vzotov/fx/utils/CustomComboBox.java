package ru.vzotov.fx.utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public abstract class CustomComboBox<T> extends TextField {

    private static final String DEFAULT_STYLE_CLASS = "custom-combo";
    public static final String STYLE_BUTTON_ARROW = "arrow-button";
    public static final String STYLE_ICON_ARROW = "arrow";

    public static final EventType<Event> ON_SHOWING =
            new EventType<Event>(Event.ANY, "CUSTOM_COMBO_BOX_ON_SHOWING");
    public static final EventType<Event> ON_SHOWN =
            new EventType<Event>(Event.ANY, "CUSTOM_COMBO_BOX_ON_SHOWN");
    public static final EventType<Event> ON_HIDING =
            new EventType<Event>(Event.ANY, "CUSTOM_COMBO_BOX_ON_HIDING");
    public static final EventType<Event> ON_HIDDEN =
            new EventType<Event>(Event.ANY, "CUSTOM_COMBO_BOX_ON_HIDDEN");


    public CustomComboBox() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    public void show() {
        if (!isDisabled()) {
            setShowing(true);
        }
    }

    /**
     * Closes the popup / dialog that was shown when {@link #show()} was called.
     */
    public void hide() {
        if (isShowing()) {
            setShowing(false);
        }
    }

    public void arm() {
        if (!armedProperty().isBound()) {
            setArmed(true);
        }
    }

    public void disarm() {
        if (!armedProperty().isBound()) {
            setArmed(false);
        }
    }

    // showing

    private ReadOnlyBooleanWrapper showing;

    public ReadOnlyBooleanProperty showingProperty() {
        return showingPropertyImpl().getReadOnlyProperty();
    }

    public final boolean isShowing() {
        return showingPropertyImpl().get();
    }

    private void setShowing(boolean value) {
        // these events will not fire if the showing property is bound
        Event.fireEvent(this, value ? new Event(ComboBoxBase.ON_SHOWING) :
                new Event(ComboBoxBase.ON_HIDING));
        showingPropertyImpl().set(value);
        Event.fireEvent(this, value ? new Event(ComboBoxBase.ON_SHOWN) :
                new Event(ComboBoxBase.ON_HIDDEN));
    }

    private ReadOnlyBooleanWrapper showingPropertyImpl() {
        if (showing == null) {
            showing = new ReadOnlyBooleanWrapper(false) {
                @Override
                protected void invalidated() {
                    pseudoClassStateChanged(PSEUDO_CLASS_SHOWING, get());
                    notifyAccessibleAttributeChanged(AccessibleAttribute.EXPANDED);
                }

                @Override
                public Object getBean() {
                    return CustomComboBox.this;
                }

                @Override
                public String getName() {
                    return "showing";
                }
            };
        }
        return showing;
    }

    // --- armed

    /**
     * Indicates that the ComboBox has been "armed" such that a mouse release
     * will cause the ComboBox {@link #show()} method to be invoked. This is
     * subtly different from pressed. Pressed indicates that the mouse has been
     * pressed on a Node and has not yet been released. {@code arm} however
     * also takes into account whether the mouse is actually over the
     * ComboBox and pressed.
     *
     * @return the armed property
     */
    public BooleanProperty armedProperty() {
        return armed;
    }

    private final void setArmed(boolean value) {
        armedProperty().set(value);
    }

    public final boolean isArmed() {
        return armedProperty().get();
    }

    private BooleanProperty armed = new SimpleBooleanProperty(this, "armed", false) {
        @Override
        protected void invalidated() {
            pseudoClassStateChanged(PSEUDO_CLASS_ARMED, get());
        }
    };


    // value

    private final ObjectProperty<T> value = new SimpleObjectProperty<>(this, "value", null);

    public T getValue() {
        return value.get();
    }

    public ObjectProperty<T> valueProperty() {
        return value;
    }

    public void setValue(T value) {
        this.value.set(value);
    }

    // converter

    private final ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(this, "converter", createDefaultConverter());

    public StringConverter<T> getConverter() {
        return converter.get();
    }

    public ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    public void setConverter(StringConverter<T> converter) {
        this.converter.set(converter);
    }

    // arrow symbol

    private final StringProperty arrowSymbol = new SimpleStringProperty(this, "arrowSymbol", null /*"\u25BC"*/);

    public String getArrowSymbol() {
        return arrowSymbol.get();
    }

    public StringProperty arrowSymbolProperty() {
        return arrowSymbol;
    }

    public void setArrowSymbol(String arrowSymbol) {
        this.arrowSymbol.set(arrowSymbol);
    }

    private StringConverter<T> createDefaultConverter() {
        return new StringConverter<T>() {
            @Override
            public String toString(T object) {
                return object.toString();
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        };
    }

    private static final PseudoClass PSEUDO_CLASS_SHOWING =
            PseudoClass.getPseudoClass("showing");
    private static final PseudoClass PSEUDO_CLASS_ARMED =
            PseudoClass.getPseudoClass("armed");
}
