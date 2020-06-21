package model;

public class Board {
    Checker[][] board;

    public Board(int width, int length) {
        board = new Checker[width][length];
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
