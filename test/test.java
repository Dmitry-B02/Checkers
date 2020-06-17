import Controller.MoveResult;
import model.CheckerColor;
import model.CheckerType;
import org.junit.Test;
import view.Main;
import model.Checker;

import static org.junit.Assert.*;
import static view.Main.board;

public class test {
    @Test
    public void testboard() {
        Main testapp = new Main();
        testapp.playField();
        Checker test1 = new Checker(CheckerColor.BLACK, 5, 4, CheckerType.KING);
        board[5][4].setChecker(test1);

        board[1][0].setChecker(null);
        Checker test2 = new Checker(CheckerColor.WHITE, 1, 0, CheckerType.USUAL);
        board[1][0].setChecker(test2);

        Checker test3 = new Checker(CheckerColor.BLACK, 2, 3, CheckerType.USUAL);
        board[3][4].setChecker(test3);

        Checker test4 = new Checker(CheckerColor.WHITE, 6, 3, CheckerType.USUAL);
        board[6][3].setChecker(test4);

        assertEquals(testapp.moveResult(board[0][5].getChecker(), 1, 4), MoveResult.NONE); // т.к. первый ход за чёрными

        assertEquals(8, board.length);
        assertTrue(board[5][4].hasChecker() && board[5][4].getChecker().getType() == CheckerType.KING);
        assertEquals(board[1][0].getChecker().getColor(), CheckerColor.WHITE);
        assertEquals(testapp.moveResult(board[5][4].getChecker(), 3, 2), MoveResult.NONE); // дамка не может ходиить через клетку
        assertEquals(testapp.moveResult(board[3][4].getChecker(), 2, 3), MoveResult.NONE); // обычная шашка не может ходить назад
        assertEquals(testapp.moveResult(board[5][2].getChecker(), 7, 4), MoveResult.KILL); // съедение шашки
        assertEquals(testapp.moveResult(board[7][2].getChecker(), 8, 3), MoveResult.NONE); // нельзя выйти за пределы поля
    }
}
