package edu.miracosta.cs112.globalcustoms.globalcustoms;//package edu.miracosta.cs112.globalcustoms.globalcustoms;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

public class HelloApplication extends Application {
    private class GlobalCustom {
        GlobalCustom(String name, double peopleCelebrating, double dollarsSpent) {
            this.name = name;
            this.peopleCelebrating = peopleCelebrating;
            this.dollarsSpent = dollarsSpent;
            this.weight = 0.0;
            this.color = Color.BLACK;
        }
        String name;
        double peopleCelebrating;
        double dollarsSpent;
        double weight;
        Color color;

        void setColor(double ratio) {
            Color color1 = Color.RED;
            Color color2 = Color.GREEN;

            double red = (color1.getRed() * (1.0 - ratio) + color2.getRed() * ratio);
            double green = (color1.getGreen() * (1.0 - ratio) + color2.getGreen() * ratio);
            double blue = (color1.getBlue() * (1.0 - ratio) + color2.getBlue() * ratio);

            this.color = new Color(red, green, blue, 1.0);
        }
    }

    class TreemapRect {
        Rect rect;
        String label;
        double value;
        GlobalCustom custom;

        public TreemapRect(String name, double celebrants, double avgSpend) {
            this.custom = new GlobalCustom(name, celebrants, avgSpend);
            this.label = custom.name;
            this.value = custom.peopleCelebrating;
            this.rect = new Rect(0,0,0,0);
        }

        public TreemapRect(GlobalCustom custom) {
            this.rect = new Rect(0,0,0,0);
            label = custom.name;
            value = custom.peopleCelebrating;
            this.custom = custom;
        }

        public TreemapRect(Rect rect, GlobalCustom custom) {
            this.rect = rect;
            label = custom.name;
            value = custom.peopleCelebrating;
            this.custom = custom;
        }
    }

    private static class TreemapPane extends Pane {
        private List<TreemapRect> rects;
        private static final Random random = new Random(42);

        public TreemapPane() {
            rects = new ArrayList<>();
        }

        public void setRects(List<TreemapRect> rects) {
            this.rects = rects;
            drawTreemap();
        }

        private void drawTreemap() {
            getChildren().clear();

            for (TreemapRect tmRect : rects) {
                Rectangle rect = new Rectangle(
                        tmRect.rect.getX(), tmRect.rect.getY(),
                        Math.max(1, tmRect.rect.getW()),
                        Math.max(1, tmRect.rect.getH())
                );

                Color color = tmRect.custom.color;

                rect.setFill(color);
                rect.setStroke(Color.WHITE);
                rect.setStrokeWidth(1);

                if (tmRect.rect.getW() > 60 && tmRect.rect.getH() > 20) {
                    Text label = new Text(
                            tmRect.rect.getX() + 5,
                            tmRect.rect.getY() + 15,
                            tmRect.label
                    );
                    label.setFill(Color.WHITE);
                    getChildren().add(label);
                }

                Tooltip tooltip = new Tooltip(String.format(
                        "%s\nValue: %.1f\nSize: %.0f x %.0f",
                        tmRect.label, tmRect.value, tmRect.rect.getW(), tmRect.rect.getH()
                ));
                Tooltip.install(rect, tooltip);

                getChildren().add(rect);
                Label label = new Label(tmRect.label);
                label.setLayoutX(tmRect.rect.getX());
                label.setLayoutY(tmRect.rect.getY());
                label.setFont(new Font("Arial", 30));
                getChildren().add(label);
            }
        }
    }

    private List<TreemapRect> computeLayout(List<TreemapRect> inputs, double x, double y, double width, double height) {
        if (inputs.isEmpty()) return new ArrayList<>();
        if (inputs.size() == 1) {
            TreemapRect input = inputs.get(0);
            return List.of(new TreemapRect(new Rect(x, y, width, height), input.custom));
        }

        List<TreemapRect> result = new ArrayList<>();
        double totalValue = inputs.stream().mapToDouble(r -> r.value).sum();

        // Sort inputs by value in descending order
        List<TreemapRect> sortedInputs = new ArrayList<>(inputs);
        sortedInputs.sort((a, b) -> Double.compare(b.value, a.value));

        layoutRectangles(sortedInputs, x, y, width, height, totalValue, result);
        return result;
    }

    private void layoutRectangles(List<TreemapRect> inputs, double x, double y,
                                  double width, double height, double totalValue,
                                  List<TreemapRect> result) {
        if (inputs.isEmpty()) return;
        if (inputs.size() == 1) {
            TreemapRect input = inputs.get(0);
            result.add(new TreemapRect(new Rect(x, y, width, height), input.custom));
            return;
        }

        boolean vertical = width < height;
        int splitIndex = findBestSplit(inputs, totalValue);

        List<TreemapRect> group1 = inputs.subList(0, splitIndex);
        List<TreemapRect> group2 = inputs.subList(splitIndex, inputs.size());

        double group1Value = group1.stream().mapToDouble(r -> r.value).sum();
        double group2Value = totalValue - group1Value;

        if (vertical) {
            double group1Height = (group1Value / totalValue) * height;
            double group2Height = height - group1Height;

            layoutRectangles(group1, x, y, width, group1Height, group1Value, result);
            layoutRectangles(group2, x, y + group1Height, width, group2Height, group2Value, result);
        } else {
            double group1Width = (group1Value / totalValue) * width;
            double group2Width = width - group1Width;

            layoutRectangles(group1, x, y, group1Width, height, group1Value, result);
            layoutRectangles(group2, x + group1Width, y, group2Width, height, group2Value, result);
        }
    }

    private int findBestSplit(List<TreemapRect> inputs, double totalValue) {
        double halfValue = totalValue / 2;
        double currentSum = 0;
        for (int i = 0; i < inputs.size(); i++) {
            currentSum += inputs.get(i).value;
            if (currentSum >= halfValue) {
                return i + 1;
            }
        }
        return inputs.size() / 2;
    }

    @Override
    public void start(Stage stage) {
        // Create sample data
        List<TreemapRect> inputs = List.of(
            new TreemapRect("Christmas", 2000.0, 875.0),
            new TreemapRect("Eid al-Fitr", 2000.0, 250.0),
            new TreemapRect("Diwali", 1000.0, 45.0),
            new TreemapRect("Easter", 2300.0, 175.0),
            new TreemapRect("Dia De Los Muertos", 230.0, 45.0)
        );

        double mostSpent = 1;
        for (TreemapRect event : inputs) {
            mostSpent = event.custom.dollarsSpent > mostSpent ? event.custom.dollarsSpent : mostSpent;
        }

        for (TreemapRect event : inputs) {
            event.custom.setColor(Math.log(event.custom.dollarsSpent) / Math.log(mostSpent));
        }

        TreemapPane treemapPane = new TreemapPane();
        Scene scene = new Scene(treemapPane, 800, 600);

        treemapPane.widthProperty().addListener((obs, oldVal, newVal) ->
                updateLayout(treemapPane, inputs));
        treemapPane.heightProperty().addListener((obs, oldVal, newVal) ->
                updateLayout(treemapPane, inputs));

        stage.setTitle("Programming Languages Popularity Treemap");
        stage.setScene(scene);
        stage.show();

        updateLayout(treemapPane, inputs);
    }

    private void updateLayout(TreemapPane pane, List<TreemapRect> inputs) {
        double width = pane.getWidth();
        double height = pane.getHeight();

        if (width > 0 && height > 0) {
            List<TreemapRect> layout = computeLayout(inputs, 0, 0, width, height);
            pane.setRects(layout);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}