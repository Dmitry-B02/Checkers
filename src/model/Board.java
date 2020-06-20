package model;

public class Board {
    private Checker checker;
    private Cell cell;

    Board(int width, int length) {

    }

    boolean hasChecker(int x, int y) {
        return checker != null;
    }

    Checker getChecker(int x, int y) {
        return checker;
    }

    void setChecker(int x, int y, Checker checker) {
        this.checker = checker;
    }
}
