package ru.vzotov.fx.utils;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GridPaneBuilder {
    /**
     * Colspan for all rows or all columns
     */
    public final static int ALL = -1;

    private final double hgap;
    private final double vgap;
    private double minWidth = USE_COMPUTED_SIZE;
    private final List<ColumnConstraints> columns = new ArrayList<>();
    private final List<RowConstraints> rows = new ArrayList<>();
    private final List<Node> children = new ArrayList<>();

    public GridPaneBuilder() {
        this.hgap = Double.NaN;
        this.vgap = Double.NaN;
    }

    public GridPaneBuilder(double hgap, double vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }

    public GridPaneBuilder minWidth(double minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public GridPaneColumnBuilder column() {
        ColumnConstraints result = new ColumnConstraints();
        return new GridPaneColumnBuilder() {
            @Override
            public GridPaneColumnBuilder minWidth(double width) {
                result.setMinWidth(width);
                return this;
            }

            @Override
            public GridPaneColumnBuilder prefWidth(double width) {
                result.setPrefWidth(width);
                return this;
            }

            @Override
            public GridPaneColumnBuilder maxWidth(double width) {
                result.setMaxWidth(width);
                return this;
            }

            @Override
            public GridPaneColumnBuilder hgrow(Priority hgrow) {
                result.setHgrow(hgrow);
                return this;
            }

            @Override
            public GridPaneColumnBuilder align(HPos align) {
                result.setHalignment(align);
                return this;
            }

            @Override
            public GridPaneColumnBuilder fillWidth(boolean fillWidth) {
                result.setFillWidth(fillWidth);
                return this;
            }

            @Override
            public GridPaneColumnBuilder configure(Consumer<ColumnConstraints> config) {
                config.accept(result);
                return this;
            }

            @Override
            public GridPaneBuilder build() {
                columns.add(result);
                return GridPaneBuilder.this;
            }
        };
    }

    public GridPaneRowBuilder row() {
        final RowConstraints result = new RowConstraints();
        return new GridPaneRowBuilder() {

            @Override
            public GridPaneRowBuilder minHeight(double height) {
                result.setMinHeight(height);
                return this;
            }

            @Override
            public GridPaneRowBuilder prefHeight(double height) {
                result.setPrefHeight(height);
                return this;
            }

            @Override
            public GridPaneRowBuilder maxHeight(double height) {
                result.setMaxHeight(height);
                return this;
            }

            @Override
            public GridPaneRowBuilder vgrow(Priority vgrow) {
                result.setVgrow(vgrow);
                return this;
            }

            @Override
            public GridPaneRowBuilder align(VPos align) {
                result.setValignment(align);
                return this;
            }

            @Override
            public GridPaneRowBuilder fillHeight(boolean fillHeight) {
                result.setFillHeight(fillHeight);
                return this;
            }

            @Override
            public GridPaneRowBuilder configure(Consumer<RowConstraints> config) {
                config.accept(result);
                return this;
            }

            @Override
            public GridPaneBuilder build() {
                rows.add(result);
                return GridPaneBuilder.this;
            }
        };
    }

    public int columnIndex() {
        return columns.size() - 1;
    }

    public int rowIndex() {
        return rows.size() - 1;
    }

    public int columnCount() {
        return columns.size();
    }

    public int rowCount() {
        return rows.size();
    }

    public GridPaneBuilder configure(UnaryOperator<GridPaneBuilder> config) {
        return config.apply(this);
    }

    public GridPaneBuilder add(int col, int row, int colspan, int rowspan, HPos halign, VPos valign, Node child) {
        GridPane.setConstraints(child, col, row,
                colspan == ALL ? columns.size() : colspan,
                rowspan == ALL ? rows.size() : rowspan,
                halign, valign);
        children.add(child);
        return this;
    }

    public GridPaneBuilder add(int col, int row, int colspan, int rowspan, Node child) {
        GridPane.setConstraints(child, col, row,
                colspan == ALL ? columns.size() : colspan,
                rowspan == ALL ? rows.size() : rowspan);
        children.add(child);
        return this;
    }

    public GridPaneBuilder add(int col, int row, Node child) {
        GridPane.setConstraints(child, col, row);
        children.add(child);
        return this;
    }

    public GridPane build() {
        final GridPane grid = new GridPane();

        if (!Double.isNaN(hgap)) grid.setHgap(hgap);
        if (!Double.isNaN(vgap)) grid.setVgap(vgap);
        if (minWidth != USE_COMPUTED_SIZE) grid.setMinWidth(minWidth);
        if (!columns.isEmpty()) grid.getColumnConstraints().setAll(columns);
        if (!rows.isEmpty()) grid.getRowConstraints().setAll(rows);
        if (!children.isEmpty()) grid.getChildren().setAll(children);

        return grid;
    }

    public interface GridPaneColumnBuilder {
        GridPaneColumnBuilder minWidth(double width);

        GridPaneColumnBuilder prefWidth(double width);

        GridPaneColumnBuilder maxWidth(double width);

        GridPaneColumnBuilder hgrow(Priority hgrow);

        GridPaneColumnBuilder align(HPos align);

        GridPaneColumnBuilder fillWidth(boolean fillWidth);

        GridPaneColumnBuilder configure(Consumer<ColumnConstraints> config);

        GridPaneBuilder build();
    }

    public interface GridPaneRowBuilder {
        GridPaneRowBuilder minHeight(double height);

        GridPaneRowBuilder prefHeight(double height);

        GridPaneRowBuilder maxHeight(double height);

        GridPaneRowBuilder vgrow(Priority vgrow);

        GridPaneRowBuilder align(VPos align);

        GridPaneRowBuilder fillHeight(boolean fillHeight);

        GridPaneRowBuilder configure(Consumer<RowConstraints> config);

        GridPaneBuilder build();
    }
}
