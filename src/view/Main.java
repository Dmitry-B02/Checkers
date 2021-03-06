package view;

import Controller.MoveResult;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.*;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    Text name = new Text("Checkers");
    Text turnText = new Text("Turn:");
    Text white = new Text("White");
    Text black = new Text("Black");


    private Group cells = new Group();
    public Group checkers = new Group();

    Pane root = new Pane();
    Pane clear = new Pane(); // для избежания ошибкок при начале новой игры (т.к. у двух сцен не может быть один root)

    Scene scene = new Scene(clear);

    private static final int width = 8;
    private static final int length = 8;

    private boolean canEatChecker = false; // может ли хоть одна шашка съесть кого-то
    private boolean currentKillSequence = false; // служит сигналом, что идёт серия поеданий
    private boolean newGame = false; // служит сигналом, что была нажата New Game (для исключения нескольких запусков таймера)
    private boolean freezeCheckers = false; // нужна для "заморозки" шашек после конца игры

    private int turn = 1;
    private int currentTurn = turn; // нужен для обнуления таймера
    private int whiteAmount = 12;
    private int blackAmount = 12;
    private int time = 60;
    int otherPieceX = -1;
    int otherPieceY = -1;

    public static double cellSize = 100;

    public static Board board;

    Timer timer = new Timer();

    Text timeText = new Text("Time left:60");

    @Override
    public void start(Stage primaryStage) {
        timeText.setText("Time left:" + time);
        timeText.setFill(Color.BLACK);
        timeText.setStyle("-fx-opacity: 0.4;");
        if (!newGame) {
            startCountDown();
        }
        newGame = true;
        Button newGame = new Button("New game");
        newGame.setLayoutX(850);
        newGame.setLayoutY(200);
        newGame.setPrefSize(300, 50);
        newGame.setFont(Font.font("Verdana", 30));
        root.getChildren().add(newGame);
        Pane playField = playField();
        scene.setRoot(playField);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        newGame.setOnMouseClicked(e -> {
            newGame(primaryStage);
        });
        primaryStage.show();
    }

    public void startCountDown() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    time--;
                    timeText.setText("Time left:" + time);
                    if (time < 11) {
                        timeText.setStyle("-fx-opacity: 1.0;");
                        timeText.setFill(Color.RED);
                    }
                    if (time == 0) {
                        stopGame();
                    }
                    if (time <= 0 || whiteAmount == 0 || blackAmount == 0) {
                        timeText.setText("Game over!");
                    }
                });
            }
        }, 1000, 1000); // каждую секунду
    }

    void newGame(Stage primaryStage) {
        cleanup();
        start(primaryStage);
    }

    void cleanup() {
        root.getChildren().clear();
        cells.getChildren().clear();
        checkers.getChildren().clear();
        turn = 1;
        canEatChecker = false;
        currentKillSequence = false;
        whiteAmount = 12;
        blackAmount = 12;
        time = 60;
        freezeCheckers = false;
        timer.purge();
    }

    void stopGame() { // появление окна с результатами игры
        freezeCheckers = true;
        Text resultText = new Text();
        resultText.setFont(Font.font("Verdana", 22));
        resultText.setLayoutX(35);
        resultText.setLayoutY(50);
        if (whiteAmount == 0 || time == 0 && turn == -1) {
            resultText.setText("Black win");
        }
        if (blackAmount == 0 || time == 0 && turn == 1) {
            resultText.setText("White win");
        }

        Pane winnerPane = new Pane();
        winnerPane.getChildren().add(resultText);

        Scene gameOver = new Scene(winnerPane, 230, 100);
        Stage newWindow = new Stage();
        newWindow.setTitle("Result");
        newWindow.setScene(gameOver);
        newWindow.show();
    }

    /* Функция проверяет, может ли данная шашка съесть кого-либо в этот ход
    (с учётом краёв поля и того, является ли она дамкой)
     */
    private boolean canEat(CheckerColor color, int x, int y, CheckerType type) {
        boolean condition1 = (x - 1 >= 0 && y - 1 >= 0 && board.hasChecker(x - 1, y - 1) && board.getChecker(x - 1, y - 1).getColor() != color
                && x - 2 >= 0 && y - 2 >= 0 && !board.hasChecker(x - 2, y - 2)
                || x + 1 < 8 && y - 1 >= 0 && board.hasChecker(x + 1, y - 1) && board.getChecker(x + 1, y - 1).getColor() != color
                && x + 2 < 8 && y - 2 >= 0 && !board.hasChecker(x + 2, y - 2));
        boolean condition2 = (x - 1 >= 0 && y + 1 < 8 && board.hasChecker(x - 1, y + 1) && board.getChecker(x - 1, y + 1).getColor() != color
                && x - 2 >= 0 && y + 2 < 8 && !board.hasChecker(x - 2, y + 2)
                || x + 1 < 8 && y + 1 < 8 && board.hasChecker(x + 1, y + 1) && board.getChecker(x + 1, y + 1).getColor() != color
                && x + 2 < 8 && y + 2 < 8 && !board.hasChecker(x + 2, y + 2));
        return type == CheckerType.USUAL && color == CheckerColor.WHITE
                && condition1
                || type == CheckerType.USUAL && color == CheckerColor.BLACK
                && condition2
                || type == CheckerType.KING && (condition1 || condition2);
    }

    public Pane playField() { // создание игрового поля

        board = new Board(width, length);

        name.setFont(Font.font("Verdana", 60));
        name.setLayoutX(860);
        name.setLayoutY(80);
        turnText.setFont(Font.font("Verdana", 30));
        turnText.setLayoutX(840);
        turnText.setLayoutY(360);
        white.setStyle("-fx-opacity: 0.0;");
        white.setFont(Font.font("Verdana", 30));
        white.setLayoutX(940);
        white.setLayoutY(360);
        black.setFont(Font.font("Verdana", 30));
        black.setLayoutX(940);
        black.setLayoutY(360);
        black.setStyle("-fx-opacity: 1.0;");
        timeText.setLayoutX(840);
        timeText.setLayoutY(460);
        timeText.setFont(Font.font("Verdana", 30));
        root.setPrefSize(1200, 800);
        root.getChildren().addAll(cells, checkers, name, turnText, white, black, timeText);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                Cell cell = new Cell((x + y) % 2 == 0, x, y);
                cells.getChildren().add(cell);

                Checker checker = null;
                if (y < 3 && (x + y) % 2 == 1) {
                    checker = putChecker(CheckerColor.BLACK, x, y, CheckerType.USUAL);
                } else if (y > 4 && (x + y) % 2 == 1) {
                    checker = putChecker(CheckerColor.WHITE, x, y, CheckerType.USUAL);
                }
                if (checker != null) {
                    checkers.getChildren().add(checker);
                    board.setChecker(x, y, checker);
                }
            }
        }
        return root;
    }

    public MoveResult moveResult(Checker checker, int currentX, int currentY) { // результат движения
        // для избежания IndexOutOfBoundsException при передвижении шашки за границы игрового поля
        if (currentX > 7 || currentY > 7 || currentX < 0 || currentY < 0 || freezeCheckers) return MoveResult.NONE;

        otherPieceX = Math.abs((currentX + checker.getPreviousX()) / 2); // для case KILL
        otherPieceY = Math.abs((currentY + checker.getPreviousY()) / 2);

        for (int x = 0; x < 8; x++) { // проверка, может ли хоть одна шашка есть
            for (int y = 0; y < 8; y++) {
                if (board.hasChecker(x, y) && board.getChecker(x, y).getColor().direction == turn) {
                    Checker currentChecker = board.getChecker(x, y);
                    if (canEat(currentChecker.getColor(), x, y, currentChecker.getType())) {
                        canEatChecker = true;
                    }
                }
            }
        }

        if (!board.hasChecker(currentX, currentY) && (currentY - checker.getPreviousY() == checker.getColor().direction
                && Math.abs(currentX - checker.getPreviousX()) == 1 || checker.getType() == CheckerType.KING
                && Math.abs(currentX - checker.getPreviousX()) == 1 && Math.abs(currentY - checker.getPreviousY()) == 1)
                && !canEat(checker.getColor(), checker.getPreviousX(), checker.getPreviousY(), checker.getType())
                && turn == checker.getColor().direction && !canEatChecker) {
            return MoveResult.USUAL;
        } else if (!board.hasChecker(currentX, currentY)
                && Math.abs(currentX - checker.getPreviousX()) == 2 && board.hasChecker(otherPieceX, otherPieceY)
                && (checker.getType() == CheckerType.KING && Math.abs(currentY - checker.getPreviousY()) == 2
                || currentY - checker.getPreviousY() == checker.getColor().direction * 2
                && board.getChecker(otherPieceX, otherPieceY).getColor().direction != checker.getColor().direction)
                && canEat(checker.getColor(), checker.getPreviousX(), checker.getPreviousY(), checker.getType())
                && turn == checker.getColor().direction && canEatChecker && (currentKillSequence && checker.killSequence
                || !currentKillSequence)) {
            return MoveResult.KILL;
        } else {
            System.out.println("Deny");
            System.out.println(board.hasChecker(1, 0));
            return MoveResult.NONE;
        }
    }

    /* Функция, в зависимости от результата движения,
     определяет, что делать с данной шашкой
     */
    public Checker putChecker(CheckerColor color, int x, int y, CheckerType type) {
        Checker checker = new Checker(color, x, y, type);
        checker.setOnMouseReleased(e -> {
            int currentX = (int) (e.getSceneX() / Main.cellSize);
            int currentY = (int) (e.getSceneY() / Main.cellSize);
            if (currentX < 8 && currentX >= 0 && currentY < 8 && currentY >= 0) {
                otherPieceX = Math.abs((currentX + checker.getPreviousX()) / 2);
                otherPieceY = Math.abs((currentY + checker.getPreviousY()) / 2);
            }
            System.out.println("Current X" + currentX);
            System.out.println("Current Y" + currentY);

            MoveResult result = moveResult(checker, currentX, currentY);
            switch (result) {
                case USUAL:
                    board.setChecker(checker.getPreviousX(), checker.getPreviousY(), null);
                    board.setChecker(currentX, currentY, checker);
                    checker.placeChecker(currentX, currentY);
                    turn = -1 * turn;
                    break;
                case KILL:
                    board.setChecker(checker.getPreviousX(), checker.getPreviousY(), null);
                    board.setChecker(currentX, currentY, checker);
                    checker.placeChecker(currentX, currentY);
                    checkers.getChildren().remove(board.getChecker(otherPieceX, otherPieceY));
                    board.setChecker(otherPieceX, otherPieceY, null);
                    currentKillSequence = true;
                    checker.killSequence = true;

                    if (!canEat(checker.getColor(), currentX, currentY, checker.getType())) { // для отмены киллстрика
                        turn = -1 * turn;
                        currentKillSequence = false;
                        checker.killSequence = false;
                    }
                    canEatChecker = false;

                    if (checker.getColor() == CheckerColor.WHITE) { // счётчик кол-ва находящихся на доске шашек
                        blackAmount--;
                    } else {
                        whiteAmount--;
                    }
                    if (whiteAmount == 0 || blackAmount == 0) {
                        stopGame();
                    }
                    break;
                case NONE:
                    checker.returnChecker();
                    break;
            }
            if (result != MoveResult.NONE && (currentY == 0 || currentY == 7)) { // появление дамки
                checkers.getChildren().remove(checker);
                Checker king = putChecker(color, currentX, currentY, CheckerType.KING);
                board.setChecker(currentX, currentY, king);
                checkers.getChildren().add(king);
            }
            if (turn == -1) {
                black.setStyle("-fx-opacity: 0.0;");
                white.setStyle("-fx-opacity: 1.0;");
            } else {
                white.setStyle("-fx-opacity: 0.0;");
                black.setStyle("-fx-opacity: 1.0;");
            }
            if (currentTurn != turn) {
                time = 60;
                timer.purge();
                currentTurn = turn;
            }
        });
        return checker;
    }
}
