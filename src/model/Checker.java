package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import view.Main;

import static model.CheckerType.KING;

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

    public boolean hasKillSequence() {
        return killSequence;
    }

    public Checker(CheckerColor color, int x, int y, CheckerType type) {
        this.color = color;
        this.type = type;

        if (type == KING) {
            Image checkerImg = color == CheckerColor.WHITE ? // создание модельки шашки
                    new Image("/assets/white_queen.png") :
                    new Image("/assets/black_queen.png");
            ImageView imageView = new ImageView(checkerImg);
            getChildren().add(imageView);
        }
        else {
            Image checkerImg = color == CheckerColor.WHITE ? // создание модельки шашки
                    new Image("/assets/white.png") :
                    new Image("/assets/black.png");
            ImageView imageView = new ImageView(checkerImg);
            getChildren().add(imageView);
        }

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
        firstX = x * Main.cellSize;
        firstY = y * Main.cellSize;
        relocate(firstX, firstY);
    }

    public void returnChecker() {
        checkerX = (int) ((int) (firstX / Main.cellSize) * Main.cellSize);
        checkerY = (int) ((int) (firstY / Main.cellSize) * Main.cellSize);
        relocate(checkerX, checkerY);
    }

    public int getPreviousX() {
        return (int) (firstX / 100);
    }

    public int getPreviousY() {
        return (int) (firstY / 100);
    }

}


