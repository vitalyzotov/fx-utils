package ru.vzotov.fx.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class ValueTextField<T> extends TextField {
    private static final String DEFAULT_STYLE_CLASS = "value-text-field";

    public ValueTextField() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ValueTextFieldSkin<>(this);
    }

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

}
