package com.codenjoy.dojo.bomberman.model;

import com.codenjoy.dojo.bomberman.services.BombermanEvents;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static com.codenjoy.dojo.bomberman.model.BoardTest.*;
import static com.codenjoy.dojo.bomberman.model.BoardTest.SIZE;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * User: sanja
 * Date: 20.04.13
 * Time: 13:40
 */
public class MultiplayerBoardTest {

    public static final int SIZE = 5;
    private SingleBoard game2;
    private Walls walls = emptyWalls();
    private Bomberman bomberman2;
    private Bomberman bomberman1;
    private GameSettings settings;
    private Level level;
    private SingleBoard game1;
    private Board board;
    private EventListener listener1;
    private EventListener listener2;

    public void setup() {
        settings = mock(GameSettings.class);

        level = mock(Level.class);
        when(level.bombsCount()).thenReturn(1);
        when(level.bombsPower()).thenReturn(1);

        bomberman1 = new MyBomberman(level);
        bomberman2 = new MyBomberman(level);
        when(settings.getBomberman(any(Level.class))).thenReturn(bomberman1, bomberman2);

        when(settings.getLevel()).thenReturn(level);
        when(settings.getBoardSize()).thenReturn(SIZE);
        when(settings.getWalls()).thenReturn(walls);

        board = new Board(settings);

        listener1 = mock(EventListener.class);
        listener2 = mock(EventListener.class);

        game1 = new SingleBoard(board, listener1);
        game2 = new SingleBoard(board, listener2);

        game1.newGame();
        game2.newGame();
    }

    private Walls emptyWalls() {
        Walls walls = mock(WallsImpl.class);
        when(walls.iterator()).thenReturn(new LinkedList<Wall>().iterator());
        return walls;
    }

    private void setPosition(int x, int y, Bomberman bomberman) {
        when(bomberman.getX()).thenReturn(x);
        when(bomberman.getY()).thenReturn(y);
    }

    @Test
    public void shouldGetTwoBombermansOnBoard() {
        setup();

        assertSame(bomberman1, game1.getJoystick());
        assertSame(bomberman2, game2.getJoystick());

        assertBoard(
                "☺♥   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);

        assertBoard(
                "♥☺   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game2);
    }

    @Test
    public void shouldOnlyOneListenerWorksWhenOneBombermanKillAnother() {
        setup();

        bomberman1.act();
        bomberman1.down();
        tick();
        bomberman1.down();
        tick();
        tick();
        tick();
        tick();

        assertBoard(
                "҉♣   \n" +
                "҉    \n" +
                "☺    \n" +
                "     \n" +
                "     \n", game1);

        verify(listener1, only()).event(BombermanEvents.KILL_MEAT_CHOPPER.name());
        verify(listener2, only()).event(BombermanEvents.KILL_BOMBERMAN.name());
    }

    private void tick() {
        board.tick();
        board.tick();
    }

    @Test
    public void shouldPrintOtherBombBomberman() {
        setup();

        bomberman1.act();
        bomberman1.down();

        assertBoard(
                "☻♥   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);

        assertBoard(
                "♠☺   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game2);
    }

    @Test
    public void shouldBombermanCantGoToAnotherBomberman() {
        setup();

        bomberman1.right();
        tick();

        assertBoard(
                "☺♥   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);
    }

    private void assertBoard(String board, SingleBoard game) {
        assertEquals(board, game.getBoardAsString());
    }

    // бомбермен может идти на митчопера, при этом он умирает
    @Test
    public void shouldKllOtherBombermanWhenBombermanGoToMeatChopper() {
        walls = new MeatChopperAt(2, 0, new WallsImpl());
        setup();

        assertBoard(
                "☺♥&  \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);

        bomberman2.right();
        tick();
        assertBoard(
                "☺ ♣  \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);

        assertBoard(
                "♥ Ѡ  \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game2);

        verifyNoMoreInteractions(listener1);
        verify(listener2, only()).event(BombermanEvents.KILL_BOMBERMAN.name());
    }

    // если митчопер убил другого бомбермена, как это на моей доске отобразится? Хочу видеть трупик
    @Test
    public void shouldKllOtherBombermanWhenMeatChopperGoToIt() {
        Dice dice = mock(Dice.class);
        meatChopperAt(dice, 2, 0);
        setup();

        assertBoard(
                "☺♥&  \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);

        when(dice.next(anyInt())).thenReturn(Direction.LEFT.value);
        tick();

        assertBoard(
                "☺♣   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);

        assertBoard(
                "♥Ѡ   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game2);

        verifyNoMoreInteractions(listener1);
        verify(listener2, only()).event(BombermanEvents.KILL_BOMBERMAN.name());
    }

    // А что если бомбермен идет на митчопера а тот идет на встречу к нему - бомбермен проскочит или умрет? должен умереть!
    @Test
    public void shouldKllOtherBombermanWhenMeatChopperAndBombermanMoves() {
        Dice dice = mock(Dice.class);
        meatChopperAt(dice, 2, 0);
        setup();

        assertBoard(
                "☺♥&  \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);

        when(dice.next(anyInt())).thenReturn(Direction.LEFT.value);
        bomberman2.right();
        tick();

        assertBoard(
                "☺&♣  \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);

        assertBoard(
                "♥&Ѡ  \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game2);

        verifyNoMoreInteractions(listener1);
        verify(listener2, only()).event(BombermanEvents.KILL_BOMBERMAN.name());
    }

    private void meatChopperAt(Dice dice, int x, int y) {
        when(dice.next(anyInt())).thenReturn(x, y);
        walls = new MeatChoppers(new WallsImpl(), SIZE, 1, dice);
    }

    //  бомбермены не могут ходить по бомбам ни по своим ни по чужим
    @Test
    public void shouldBombermanCantGoToBombFromAnotherBomberman() {
        setup();

        bomberman2.act();
        bomberman2.right();
        tick();
        bomberman2.right();
        bomberman1.right();
        tick();

        assertBoard(
                "☺3 ♥ \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);

        bomberman2.left();
        tick();
        bomberman2.left();
        tick();

        assertBoard(
                "☺1♥  \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);
    }

    @Test
    public void shouldBombKillAllBomberman() {
        shouldBombermanCantGoToBombFromAnotherBomberman();

        tick();
        assertBoard(
                "Ѡ҉♣  \n" +
                " ҉   \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);

        assertBoard(
                "♣҉Ѡ  \n" +
                " ҉   \n" +
                "     \n" +
                "     \n" +
                "     \n", game2);
    }

    @Test
    public void shouldNewGamesWhenKillAll() {
        shouldBombKillAllBomberman();
        when(settings.getBomberman(any(Level.class))).thenReturn(new MyBomberman(level), new MyBomberman(level));

        game1.newGame();
        game2.newGame();
        tick();
        assertBoard(
                "☺♥   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game1);

        assertBoard(
                "♥☺   \n" +
                "     \n" +
                "     \n" +
                "     \n" +
                "     \n", game2);
    }

    // на поле можно чтобы каждый поставил то количество бомб которое ему позволено
}
