package view;

import Controller.MoveResult;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Cell;
import model.Checker;
import model.CheckerType;
import model.CheckerColor;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    private Group cells = new Group();
    private Group checkers = new Group();

    private static final int width = 8;
    private static final int length = 8;

    private boolean canEatChecker = false;

    private int turn = -1;

    public static double cellSize = 100;

    public static Cell[][] board = new Cell[width][length];

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(playField());
        primaryStage.setResizable(false);
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean canEat(CheckerColor color, int x, int y, CheckerType type) {
        boolean condition1 = (x - 1 >= 0 && y - 1 >= 0 && board[x - 1][y - 1].hasChecker() && board[x - 1][y - 1].getChecker().getColor() != color
                && x - 2 >= 0 && y - 2 >= 0 && !board[x - 2][y - 2].hasChecker()
                || x + 1 < 8 && y - 1 >= 0 && board[x + 1][y - 1].hasChecker() && board[x + 1][y - 1].getChecker().getColor() != color
                && x + 2 < 8 && y - 2 >= 0 && !board[x + 2][y - 2].hasChecker());
        boolean condition2 = (x - 1 >= 0 && y + 1 < 8 && board[x - 1][y + 1].hasChecker() && board[x - 1][y + 1].getChecker().getColor() != color
                && x - 2 >= 0 && y + 2 < 8 && !board[x - 2][y + 2].hasChecker()
                || x + 1 < 8 && y + 1 < 8 && board[x + 1][y + 1].hasChecker() && board[x + 1][y + 1].getChecker().getColor() != color
                && x + 2 < 8 && y + 2 < 8 && !board[x + 2][y + 2].hasChecker());
        return type == CheckerType.USUAL && color == CheckerColor.WHITE
                && condition1
                || type == CheckerType.USUAL && color == CheckerColor.BLACK
                && condition2
                || type == CheckerType.KING && (condition1 || condition2);
    }

    private Group playField() { // создание игрового поля
        Group root = new Group();
        root.getChildren().addAll(cells, checkers);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                Cell cell = new Cell((x + y) % 2 == 0, x, y);
                board[x][y] = cell;
                cells.getChildren().add(cell);

                Checker checker = null;
                if (y < 3 && (x + y) % 2 == 1) {
                    checker = putChecker(CheckerColor.BLACK, x, y, CheckerType.USUAL);
                } else if (y > 4 && (x + y) % 2 == 1) {
                    checker = putChecker(CheckerColor.WHITE, x, y, CheckerType.USUAL);
                }
                if (checker != null) {
                    cell.setChecker(checker);
                    checkers.getChildren().add(checker);
                }
            }
        }
        return root;
    }

    public MoveResult moveResult(Checker checker, int currentX, int currentY) { // результат движения
        // для избежания IndexOutOfBoundsException при передвижении шашки за границы игрового поля
        if (currentX > 7 || currentY > 7 || currentX < 0 || currentY < 0) return MoveResult.NONE;
        int otherPieceX = Math.abs((currentX + checker.getPreviousX()) / 2);
        int otherPieceY = Math.abs((currentY + checker.getPreviousY()) / 2);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (board[x][y].hasChecker() && board[x][y].getChecker().getColor().direction == turn) {
                    Checker currentChecker = board[x][y].getChecker();
                    if (canEat(currentChecker.getColor(), x, y, currentChecker.getType())) {
                        canEatChecker = true;
                    }
                }
            }
        }
        Cell between = board[otherPieceX][otherPieceY];
        if (!board[currentX][currentY].hasChecker() && (currentY - checker.getPreviousY() == checker.getColor().direction
                && Math.abs(currentX - checker.getPreviousX()) == 1 || checker.getType() == CheckerType.KING
                && Math.abs(currentX - checker.getPreviousX()) == 1 && Math.abs(currentY - checker.getPreviousY()) == 1)
                && !canEat(checker.getColor(), checker.getPreviousX(), checker.getPreviousY(), checker.getType())
                && turn == checker.getColor().direction && !canEatChecker) {
            System.out.println("ыыы");
            return MoveResult.USUAL;
        } else if (!board[currentX][currentY].hasChecker()
                && Math.abs(currentX - checker.getPreviousX()) == 2 && between.hasChecker()
                && (checker.getType() == CheckerType.KING && Math.abs(currentY - checker.getPreviousY()) == 2
                || currentY - checker.getPreviousY() == checker.getColor().direction * 2
                && between.getChecker().getColor().direction != checker.getColor().direction)
                && canEat(checker.getColor(), checker.getPreviousX(), checker.getPreviousY(), checker.getType())
                && turn == checker.getColor().direction && canEatChecker) {
            return MoveResult.KILL;
        } else {
            System.out.println("Deny");
            return MoveResult.NONE;
        }
    }

    public Checker putChecker(CheckerColor color, int x, int y, CheckerType type) {
        Checker checker = new Checker(color, x, y, type);
        checker.setOnMouseReleased(e -> {
            Cell between = null;
            int currentX = (int) (e.getSceneX() / Main.cellSize);
            int currentY = (int) (e.getSceneY() / Main.cellSize);
            if (currentX < 8 && currentY < 8) {
                int otherPieceX = Math.abs((currentX + checker.getPreviousX()) / 2);
                int otherPieceY = Math.abs((currentY + checker.getPreviousY()) / 2);
                between = board[otherPieceX][otherPieceY];
            }
            System.out.println(currentX);
            System.out.println(currentY);

            MoveResult result = moveResult(checker, currentX, currentY);
            switch (result) {
                case USUAL:
                    board[checker.getPreviousX()][checker.getPreviousY()].setChecker(null);
                    board[currentX][currentY].setChecker(checker);
                    checker.placeChecker(currentX, currentY);
                    turn = -1 * turn;
                    break;
                case KILL:
                    board[checker.getPreviousX()][checker.getPreviousY()].setChecker(null);
                    board[currentX][currentY].setChecker(checker);
                    checker.placeChecker(currentX, currentY);
                    checkers.getChildren().remove(between.getChecker());
                    between.setChecker(null);
                    if (!canEat(checker.getColor(), currentX, currentY, checker.getType())) {
                        turn = -1 * turn;
                    }
                    canEatChecker = false;
                    break;
                case NONE:
                    checker.returnChecker();
                    checker.placeChecker(checker.getPreviousX(), checker.getPreviousY());
                    System.out.println(checker.getPreviousX());
                    break;
            }
            if (result != MoveResult.NONE && (currentY == 0 || currentY == 7)) { // появление дамки
                checkers.getChildren().remove(checker);
                Checker king = putChecker(color, currentX, currentY, CheckerType.KING);
                board[currentX][currentY].setChecker(king);
                checkers.getChildren().add(king);
            }
        });
        return checker;
    }
}
