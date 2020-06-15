package model;

import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import view.Main;
import model.Checker;

public class Cell extends Rectangle {
    Checker checker;

    public Cell(boolean color, int x, int y) {
        setWidth(Main.cellSize);
        setHeight(Main.cellSize);

        if (color) setFill(Paint.valueOf("#D3D3D3"));
        else setFill(Paint.valueOf("#696969"));

        relocate(x * Main.cellSize, y * Main.cellSize);
    }

    public boolean hasChecker() {
        return checker != null;
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    public Checker getChecker() {
        return checker;
    }
}
