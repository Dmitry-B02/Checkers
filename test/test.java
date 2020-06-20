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
        board.setChecker(5, 4, test1);

        board.setChecker(1, 0, null);
        Checker test2 = new Checker(CheckerColor.WHITE, 1, 0, CheckerType.USUAL);
        board.setChecker(1, 0, test2);

        Checker test3 = new Checker(CheckerColor.BLACK, 2, 3, CheckerType.USUAL);
        board.setChecker(3, 4, test3);

        Checker test4 = new Checker(CheckerColor.WHITE, 6, 3, CheckerType.USUAL);
        board.setChecker(6, 3, test4);

        assertEquals(testapp.moveResult(board.getChecker(0, 5), 1, 4), MoveResult.NONE); // т.к. первый ход за чёрными

        assertTrue(board.hasChecker(5, 4) && board.getChecker(5, 4).getType() == CheckerType.KING);
        assertEquals(board.getChecker(1, 0).getColor(), CheckerColor.WHITE);
        assertEquals(testapp.moveResult(board.getChecker(5, 4), 3, 2), MoveResult.NONE); // дамка не может ходить через клетку
        assertEquals(testapp.moveResult(board.getChecker(3, 4), 2, 3), MoveResult.NONE); // обычная шашка не может ходить назад
        assertEquals(testapp.moveResult(board.getChecker(5, 2), 7, 4), MoveResult.KILL); // съедение шашки
        assertEquals(testapp.moveResult(board.getChecker(7, 2), 8, 3), MoveResult.NONE); // нельзя выйти за пределы поля
    }
}
