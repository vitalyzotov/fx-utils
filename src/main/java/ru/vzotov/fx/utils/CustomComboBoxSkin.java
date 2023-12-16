package ru.vzotov.fx.utils;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;

import static ru.vzotov.fx.utils.CustomComboBox.STYLE_BUTTON_ARROW;
import static ru.vzotov.fx.utils.CustomComboBox.STYLE_ICON_ARROW;
import static ru.vzotov.fx.utils.LayoutUtils.restyled;

public abstract class CustomComboBoxSkin<T> extends TextFieldSkin {

    private final CustomComboBox<T> control;
    //private final HBox root;

    private final StackPane arrowButton;
    private final Region arrow;

    private Popup popup;

    public CustomComboBoxSkin(CustomComboBox<T> control) {
        super(control);
        this.control = control;

        // open button / arrow
        arrow = restyled(new Region(), STYLE_ICON_ARROW);
        arrow.setFocusTraversable(false);
        arrow.setId(STYLE_ICON_ARROW);
        arrow.setMaxWidth(Region.USE_PREF_SIZE);
        arrow.setMaxHeight(Region.USE_PREF_SIZE);
        arrow.setMouseTransparent(true);

        arrowButton = restyled(new StackPane(), STYLE_BUTTON_ARROW);
        arrowButton.setManaged(false);
        arrowButton.setFocusTraversable(false);
        arrowButton.setId(STYLE_BUTTON_ARROW);
        arrowButton.getChildren().add(arrow);

        getChildren().add(arrowButton);

        // When focus shifts to another node, it should hide.
        control.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                focusLost();
            }
        });

        // Register listeners
        updateArrowButtonListeners();
        registerChangeListener(control.showingProperty(), e -> {
            if (control.isShowing()) {
                showPopup();
            } else {
                hidePopup();
            }
        });


        control.requestLayout();
    }

    protected void hidePopup() {
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }

    protected void showPopup() {
        control.requestFocus();

        if (popup != null) {
            hidePopup();
        }

        popup = new Popup();
        popup.setAutoHide(true);
        popup.setOnAutoHide(e -> control.hide());
        popup.getContent().add(getPopupContent());
        popup.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_TOP_RIGHT);

        final Bounds localBounds = getSkinnable().getBoundsInLocal();
        final Point2D pt = getSkinnable().localToScreen(localBounds.getMaxX(), localBounds.getMaxY());
        popup.show(getSkinnable(), pt.getX(), pt.getY());

        popup.requestFocus();
        popup.focusedProperty().addListener((o, wasFocused, isFocused) -> {
            if (wasFocused != isFocused) {
                popup.hide();
                popup = null;
            }
        });
    }

    protected void toggle() {
        if (popup != null) {
            control.hide();
        } else {
            control.show();
        }
    }

    protected abstract Node getPopupContent();

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        super.layoutChildren(x, y, w, h);
        final double arrowWidth = snapSizeX(arrow.prefWidth(-1));
        final double arrowButtonWidth = arrowButton.snappedLeftInset() + arrowWidth + arrowButton.snappedRightInset();

        final double l = control.snappedLeftInset();
        final double r = control.snappedRightInset();
        final double t = control.snappedTopInset();
        final double b = control.snappedBottomInset();
        arrowButton.resize(arrowButtonWidth, h + t + b);
        arrowButton.relocate(l + w + r - arrowButtonWidth, 0);
    }

    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        final double arrowWidth = snapSizeX(arrow.prefWidth(-1));
        final double arrowButtonWidth = arrowButton.snappedLeftInset() +
                arrowWidth +
                arrowButton.snappedRightInset();
        return leftInset + arrowButtonWidth + rightInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        double ph;
        final int DEFAULT_HEIGHT = 21;
        double arrowHeight =
                (arrowButton.snappedTopInset() + arrow.prefHeight(-1) + arrowButton.snappedBottomInset());
        ph = Math.max(DEFAULT_HEIGHT, arrowHeight);

        return topInset + ph + bottomInset;
    }


    @Override
    protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefWidth(height);
    }

    @Override
    protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        return getSkinnable().prefHeight(width);
    }

    void focusLost() {
        control.hide();
    }

    private void updateArrowButtonListeners() {
        if (getSkinnable().isEditable()) {
            //
            // arrowButton behaves like a button.
            // This is strongly tied to the implementation in ComboBoxBaseBehavior.
            //
            arrowButton.addEventHandler(MouseEvent.MOUSE_ENTERED, mouseEnteredEventHandler);
            arrowButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedEventHandler);
            arrowButton.addEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedEventHandler);
            arrowButton.addEventHandler(MouseEvent.MOUSE_EXITED, mouseExitedEventHandler);
        }
    }

    private final EventHandler<MouseEvent> mouseEnteredEventHandler = e -> {
        arm();
    };

    private final EventHandler<MouseEvent> mousePressedEventHandler = e -> {
        arm();
        e.consume();
    };
    private final EventHandler<MouseEvent> mouseReleasedEventHandler = e -> {
        disarm();

        toggle();

        e.consume();
    };
    private final EventHandler<MouseEvent> mouseExitedEventHandler = e -> {
        disarm();
    };

    public void arm() {
        if (control.isPressed()) {
            control.arm();
        }
    }

    public void disarm() {
        if (control.isArmed()) {
            control.disarm();
        }
    }

}
