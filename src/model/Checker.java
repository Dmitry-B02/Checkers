package model;

import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Ellipse;

import static model.CheckerType.KING;
import static view.Main.cellSize;

public class Checker extends Pane {


    double firstX, firstY;
    int checkerX, checkerY;
    private CheckerColor color;
    private CheckerType type;
    public boolean killSequence = false; // нужен для проверки, была ли уже съедена данной шашкой какая-либо шашка

    public CheckerColor getColor() {
        return color;
    }

    public CheckerType getType() {
        return type;
    }

    public Checker(CheckerColor color, int x, int y, CheckerType type) {
        this.color = color;
        this.type = type;

        Ellipse ellipse = new Ellipse(cellSize * 0.42, cellSize * 0.42);
        ellipse.setFill(color == CheckerColor.WHITE ? Color.valueOf("#FFFAFA") : Color.valueOf("#000000"));
        if (type == KING) {
            ellipse.setStroke(Color.valueOf("#FF00FF"));
            ellipse.setStrokeWidth(cellSize * 0.08);
        }
        ellipse.setTranslateX(cellSize - 50);
        ellipse.setTranslateY(cellSize - 50);
        getChildren().add(ellipse);

        placeChecker(x, y);

        setOnMousePressed(e -> {
            firstX = e.getSceneX();
            firstY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            relocate(e.getSceneX() - 50, e.getSceneY() - 50);
        });
    }

    public void placeChecker(int x, int y) {
        firstX = x * cellSize;
        firstY = y * cellSize;
        relocate(firstX, firstY);
    }

    public void returnChecker() {
        checkerX = (int) ((int) (firstX / cellSize) * cellSize);
        checkerY = (int) ((int) (firstY / cellSize) * cellSize);
        relocate(checkerX, checkerY);
    }

    public int getPreviousX() {
        return (int) (firstX / 100);
    }

    public int getPreviousY() {
        return (int) (firstY / 100);
    }

}


