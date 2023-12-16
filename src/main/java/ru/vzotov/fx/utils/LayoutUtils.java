package ru.vzotov.fx.utils;

import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Collection;
import java.util.ListIterator;
import java.util.Map;

public class LayoutUtils {

    public static Label labelFor(Label label, Node node) {
        label.setLabelFor(node);
        return label;
    }

    public static <T extends Node> T vboxMargin(double top, double right, double bottom, double left, T node) {
        VBox.setMargin(node, new Insets(top, right, bottom, left));
        return node;
    }

    public static <T extends Node> T vgrow(Priority value, T node) {
        VBox.setVgrow(node, value);
        return node;
    }

    public static <T extends Node> T hgrow(Priority value, T node) {
        HBox.setHgrow(node, value);
        return node;
    }

    public static <T extends Node> T fillWidth(T node) {
        GridPane.setFillWidth(node, true);
        return node;
    }

    public static void anchorAll(Node node, Double value) {
        anchor(node, value, value, value, value);
    }

    public static <T extends Node> T anchor(T node, Double top, Double right, Double bottom, Double left) {
        AnchorPane.setLeftAnchor(node, left);
        AnchorPane.setRightAnchor(node, right);
        AnchorPane.setTopAnchor(node, top);
        AnchorPane.setBottomAnchor(node, bottom);
        return node;
    }

    public static <T extends Node> T free(T node) {
        node.setManaged(false);
        return node;
    }

    public static <T extends Node> T disableAndHide(T node) {
        node.setDisable(true);
        node.setVisible(false);
        return node;
    }

    public static <T extends Node> T enableAndShow(T node) {
        node.setDisable(false);
        node.setVisible(true);
        return node;
    }

    public static <T extends Styleable> T restyled(T node, String... styles) {
        if (node != null) node.getStyleClass().setAll(styles);
        return node;
    }

    public static <T extends Styleable> T restyled(T node, Collection<String> styles) {
        if (node != null) node.getStyleClass().setAll(styles);
        return node;
    }

    public static <T extends Styleable> T styled(String style, T node) {
        return styled(node, style);
    }

    public static <T extends Styleable> T styled(String[] styles, T node) {
        return styled(node, styles);
    }

    public static <T extends Styleable> T styled(T node, String... styles) {
        if (node != null) node.getStyleClass().addAll(styles);
        return node;
    }

    public static <T extends Styleable> T styled(T node, Collection<String> styles) {
        if (node != null) node.getStyleClass().addAll(styles);
        return node;
    }

    public static <T extends Styleable> T toggle(T styleable, String style, boolean value) {
        if (styleable != null) {
            final ObservableList<String> list = styleable.getStyleClass();
            if (value) {
                if (!list.contains(style)) {
                    list.add(style);
                }
            } else {
                list.removeAll(style);
            }
        }
        return styleable;
    }

    public static <T extends Styleable, V> T toggleWhen(T styleable, Map<String, V> styles, V value) {
        if (styleable != null) {
            final ObservableList<String> list = styleable.getStyleClass();
            if (value != null) {
                final ListIterator<String> it = list.listIterator();
                boolean append = true;
                while (it.hasNext()) {
                    final String style = it.next();
                    final V v = styles.get(style);
                    if (v != null) {
                        if (value.equals(v)) {
                            // already contains selected style
                            append = false;
                        } else {
                            it.remove();
                        }
                    }
                }
                if (append) {
                    styles.entrySet().stream().filter(e -> value.equals(e.getValue()))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .ifPresent(list::add);

                }
            } else {
                list.removeAll(styles.keySet());
            }
        }
        return styleable;
    }

    public static <N extends Toggle> N toggleGroup(N input, ToggleGroup group) {
        input.setToggleGroup(group);
        return input;
    }

    public static <T extends Region> T stretch(T node) {
        node.setMaxWidth(Double.MAX_VALUE);
        return node;
    }

    public static <T extends Region> T usePrefSize(T node) {
        node.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        return node;
    }


    public static ButtonBar buttons(ButtonBar bar, Button... buttons) {
        bar.getButtons().setAll(buttons);
        return bar;
    }

}
