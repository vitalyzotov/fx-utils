package ru.vzotov.fx.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Data<X, N> {

    public Data(X x, double value) {
        setX(x);
        setValue(value);
    }

    private final ObjectProperty<N> node = new SimpleObjectProperty<>();

    public N getNode() {
        return node.get();
    }

    public ObjectProperty<N> nodeProperty() {
        return node;
    }

    public void setNode(N node) {
        this.node.set(node);
    }

    private final ObjectProperty<X> x = new SimpleObjectProperty<>();

    public X getX() {
        return x.get();
    }

    public ObjectProperty<X> xProperty() {
        return x;
    }

    public void setX(X x) {
        this.x.set(x);
    }

    private final DoubleProperty value = new SimpleDoubleProperty();

    public double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    @Override
    public String toString() {
        return "Data{" +
                "x=" + x +
                ", value=" + value +
                '}';
    }
}
