package ru.vzotov.fx.utils;

import javafx.beans.WeakListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is used to handle the conversion binding in the application.
 * It provides methods for managing the binding process between different data types or objects.
 * It is particularly useful when we need to convert data types during data transfer between different application layers.
 *
 * @param <U> the type of the target property in the conversion binding.
 * @param <V> the type of the source property in the conversion binding.
 */
public class ConversionBinding<U, V> implements ChangeListener<Object>, WeakListener {

    private static final Logger log = LoggerFactory.getLogger(ConversionBinding.class);

    private static void checkParameters(Object property1, Object property2) {
        if ((property1 == null) || (property2 == null)) {
            throw new NullPointerException("Both properties must be specified.");
        }
        if (property1 == property2) {
            throw new IllegalArgumentException("Cannot bind property to itself");
        }
    }

    /**
     * Binds a target property to a source property with conversion functions.
     * This method creates a new ConversionBinding instance and adds listeners to the target and source properties.
     * The target's value is set to the result of applying the toTarget function to the source's value.
     *
     * @param target   The target property to bind. Must not be null.
     * @param source   The source property to bind. Must not be null.
     * @param toSource The function to convert a value of type U to a value of type V. Must not be null.
     * @param toTarget The function to convert a value of type V to a value of type U. Must not be null.
     * @param <U>      The type of the target property.
     * @param <V>      The type of the source property.
     * @return A new ConversionBinding instance that binds the target property to the source property.
     * @throws NullPointerException     If any of the parameters are null.
     * @throws IllegalArgumentException If the target and source are the same property.
     */
    public static <U, V> ConversionBinding<U, V> bind(Property<U> target, Property<V> source, Function<U, V> toSource, Function<V, U> toTarget) {
        checkParameters(target, source);
        Objects.requireNonNull(toSource);
        Objects.requireNonNull(toTarget);
        final ConversionBinding<U, V> binding = new ConversionBinding<>(target, source, toSource, toTarget);
        target.setValue(toTarget.apply(source.getValue()));
        target.addListener(binding);
        source.addListener(binding);
        return binding;
    }

    /**
     * Binds a read-only target property to a source property with conversion functions.
     * This method creates a new ConversionBinding instance and adds listeners to the target and source properties.
     * The target's value is set to the result of applying the toTarget function to the source's value.
     * The setter consumer is used to set the value of the target property.
     *
     * @param target   The read-only target property to bind. Must not be null.
     * @param source   The source property to bind. Must not be null.
     * @param toSource The function to convert a value of type U to a value of type V. Must not be null.
     * @param toTarget The function to convert a value of type V to a value of type U. Must not be null.
     * @param setter   The consumer to set the value of the target property. Must not be null.
     * @param <U>      The type of the target property.
     * @param <V>      The type of the source property.
     * @return A new ConversionBinding instance that binds the target property to the source property.
     * @throws NullPointerException     If any of the parameters are null.
     * @throws IllegalArgumentException If the target and source are the same property.
     */
    public static <U, V> ConversionBinding<U, V> bind(ReadOnlyProperty<U> target, Property<V> source, Function<U, V> toSource, Function<V, U> toTarget, Consumer<U> setter) {
        checkParameters(target, source);
        Objects.requireNonNull(toSource);
        Objects.requireNonNull(toTarget);
        Objects.requireNonNull(setter);
        final ConversionBinding<U, V> binding = new ConversionBinding<>(target, source, toSource, toTarget, setter);
        target.addListener(binding);
        source.addListener(binding);
        return binding;
    }

    private final int cachedHashCode;
    private final WeakReference<ReadOnlyProperty<U>> targetPropertyRef;
    private final WeakReference<Property<V>> sourcePropertyRef;
    private boolean updating;
    private final Function<U, V> toSource;
    private final Function<V, U> toTarget;
    private Consumer<U> setter;

    protected Object getTarget() {
        return targetPropertyRef.get();
    }

    protected Object getSource() {
        return sourcePropertyRef.get();
    }

    private ConversionBinding(Property<U> target, Property<V> source, Function<U, V> toSource, Function<V, U> toTarget) {
        this(target, source, toSource, toTarget, null);
        this.setter = (u) -> {
            ((Property<U>) Objects.requireNonNull(this.targetPropertyRef.get())).setValue(u);
        };
    }

    private ConversionBinding(ReadOnlyProperty<U> target, Property<V> source, Function<U, V> toSource, Function<V, U> toTarget, Consumer<U> setter) {
        this.toSource = toSource;
        this.toTarget = toTarget;
        this.setter = setter;
        cachedHashCode = target.hashCode() * source.hashCode();
        targetPropertyRef = new WeakReference<>(target);
        sourcePropertyRef = new WeakReference<>(source);
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public boolean wasGarbageCollected() {
        return (getTarget() == null) || (getSource() == null);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        final Object propertyA1 = getTarget();
        final Object propertyA2 = getSource();
        if ((propertyA1 == null) || (propertyA2 == null)) {
            return false;
        }

        if (obj instanceof final ConversionBinding<?, ?> otherBinding) {
            final Object propertyB1 = otherBinding.getTarget();
            final Object propertyB2 = otherBinding.getSource();
            if ((propertyB1 == null) || (propertyB2 == null)) {
                return false;
            }

            if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
                return true;
            }
            if (propertyA1 == propertyB2 && propertyA2 == propertyB1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
        if (!updating) {
            final ReadOnlyProperty<U> target = targetPropertyRef.get();
            final Property<V> source = sourcePropertyRef.get();
            if ((target == null) || (source == null)) {
                if (target != null) {
                    target.removeListener(this);
                }
                if (source != null) {
                    source.removeListener(this);
                }
            } else {
                try {
                    updating = true;
                    if (target == observable) {
                        try {
                            source.setValue(toSource.apply(target.getValue()));
                        } catch (Exception e) {
                            log.debug("Exception while parsing String in bidirectional binding");
                            source.setValue(null);
                        }
                    } else {
                        try {
                            setter.accept(toTarget.apply(source.getValue()));
                        } catch (Exception e) {
                            log.debug("Exception while converting Object to String in bidirectional binding");
                            setter.accept(null);
                        }
                    }
                } finally {
                    updating = false;
                }
            }
        }
    }

}
