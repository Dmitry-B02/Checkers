package model;

public class Board {
    Checker[][] board;

    public Board(int width, int length) {
        board = new Checker[width][length];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                Checker checker = null;
                if (y < 3 && (x + y) % 2 == 1) {
                    checker = new Checker(CheckerColor.BLACK, x, y, CheckerType.USUAL);
                } else if (y > 4 && (x + y) % 2 == 1) {
                    checker = new Checker(CheckerColor.WHITE, x, y, CheckerType.USUAL);
                }
                board[x][y] = checker;
            }
        }
    }

    public boolean hasChecker(int x, int y) {
        return board[x][y] != null;
    }

    public Checker getChecker(int x, int y) {
        return board[x][y];
    }

    public void setChecker(int x, int y, Checker checker) {
        board[x][y] = checker;
    }
}
