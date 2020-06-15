package model;

public enum CheckerColor {
    BLACK(1), WHITE(-1);

    public int direction;

    CheckerColor(int direction) {
        this.direction = direction;
    }
}
