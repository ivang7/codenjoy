package com.codenjoy.bomberman.model;

import com.codenjoy.bomberman.console.BombermanPrinter;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: oleksandr.baglai
 * Date: 3/7/13
 * Time: 9:32 PM
 */
public class BoomEngineTest {

    private static final int SIZE = 21;

    @Test
    public void testOneBarrier() {
        List<Point> barriers = Arrays.asList(new Point(3, 3), new Point(3, 2), new Point(2, 3), new Point(2, 2));
        Point source = new Point(0, 0);
        int radius = 17;

        assertBoom(barriers, source, radius,
                "@*****************   \n" +
                "*****************    \n" +
                "**XX*************    \n" +
                "**XX  ***********    \n" +
                "***     *********    \n" +
                "***       *******    \n" +
                "****        ****     \n" +
                "****          **     \n" +
                "*****                \n" +
                "*****                \n" +
                "******               \n" +
                "******               \n" +
                "*******              \n" +
                "*******              \n" +
                "********             \n" +
                "********             \n" +
                "******               \n" +
                "                     \n" +
                "                     \n" +
                "                     \n" +
                "                     \n");
    }

    @Test
    public void testOneBarrierAtCenter() {
        List<Point> barriers = Arrays.asList(new Point(9, 9), new Point(9, 8), new Point(8, 9), new Point(8, 8),
                new Point(12, 12), new Point(13, 13), new Point(12, 13), new Point(13, 12));
        Point source = new Point(10, 10);
        int radius = 7;

        assertBoom(barriers, source, radius,
                "                     \n" +
                "                     \n" +
                "                     \n" +
                "         *****       \n" +
                "         ******      \n" +
                "         *******     \n" +
                "         ********    \n" +
                "          *******    \n" +
                "        XX*******    \n" +
                "   **** XX*******    \n" +
                "   *******@*******   \n" +
                "   **************    \n" +
                "   *********XX***    \n" +
                "   *********XX  *    \n" +
                "    *********        \n" +
                "     ********        \n" +
                "      ********       \n" +
                "                     \n" +
                "                     \n" +
                "                     \n" +
                "                     \n");
    }

    @Test
    public void testTwoBarriers() {
        List<Point> barriers = Arrays.asList(new Point(9, 9), new Point(9, 8), new Point(8, 9), new Point(8, 8),
                new Point(4, 4), new Point(5, 5), new Point(4, 5), new Point(5, 4));
        Point source = new Point(10, 10);
        int radius = 17;

        assertBoom(barriers, source, radius,
                "        *************\n" +
                "        *************\n" +
                "        *************\n" +
                "        *************\n" +
                "    XX   ************\n" +
                "    XX   ************\n" +
                "         ************\n" +
                "         ************\n" +
                "****    XX***********\n" +
                "********XX***********\n" +
                "**********@**********\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n");
    }

    @Test
    public void testTwoBarriersInOtherVay() {
        List<Point> barriers = Arrays.asList(new Point(12, 12), new Point(11, 11), new Point(12, 11), new Point(11, 12),
                new Point(16, 16), new Point(17, 17), new Point(16, 17), new Point(17, 16));
        Point source = new Point(10, 10);
        int radius = 17;

        assertBoom(barriers, source, radius,
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "*********************\n" +
                "**********@**********\n" +
                "***********XX********\n" +
                "***********XX    ****\n" +
                "************         \n" +
                "************         \n" +
                "************         \n" +
                "************    XX   \n" +
                "*************   XX   \n" +
                "*************        \n" +
                "*************        \n" +
                "*************        \n");
    }

    @Test
    public void testBigBoomAtClassicWalls() {
        List<Point> barriers = new LinkedList<Point>();
        CollectionUtils.addAll(barriers, new OriginalWalls(SIZE).iterator());
        Point source = new Point(11, 11);
        int radius = 7;

        assertBoom(barriers, source, radius,
                "XXXXXXXXXXXXXXXXXXXXX\n" +
                "X                   X\n" +
                "X X X X X X X X X X X\n" +
                "X                   X\n" +
                "X X X X X X*X X X X X\n" +
                "X          *        X\n" +
                "X X X X X X*X X X X X\n" +
                "X          *        X\n" +
                "X X X X X X*X X X X X\n" +
                "X         ***       X\n" +
                "X X X X X*X*X*X*X X X\n" +
                "X   *******@******* X\n" +
                "X X X X X*X*X*X*X X X\n" +
                "X         ***       X\n" +
                "X X X X X X*X X X X X\n" +
                "X         ***       X\n" +
                "X X X X X X*X X X X X\n" +
                "X          *        X\n" +
                "X X X X X X X X X X X\n" +
                "X                   X\n" +
                "XXXXXXXXXXXXXXXXXXXXX\n");
    }


    private void assertBoom(List<Point> barriers, Point source, int radius, String expected) {
        List<Point> container = BoomEngine.boom(barriers, SIZE, source, radius);

        String actual = new BombermanPrinter(SIZE)
                .printSmth(container, '*')
                .printSmth(barriers, 'X')
                .printSmth(Arrays.asList(source), '@').asString();

        assertEquals(expected, actual);
    }

}
