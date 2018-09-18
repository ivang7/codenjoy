/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
using System;
using System.Collections.Generic;
using Loderunner.Api;

namespace Demo
{
    /// <summary>
    /// This is LoderunnerAI client demo.
    /// </summary>
    internal class MyCustomLoderunnerAI : LoderunnerBase
    {
        private const int areaFindEnemey = 3;
        private const int areaFindGold = 11;

        private List<BoardPoint> pathToGold;

        public MyCustomLoderunnerAI(string name)
            : base(name)
        {
            pathToGold = new List<BoardPoint>();
        }

        /// <summary>
        /// Calls each move to make decision what to do (next move)
        /// </summary>
        protected override string DoMove(GameBoard gameBoard)
        {
            //Just print current state (gameBoard) to console
            Console.SetCursorPosition(0, 0);
            gameBoard.PrintBoard();

            var me = gameBoard.GetMe;
            var enemies = gameBoard.GetEnemyPositions();
            var otherHero = gameBoard.GetOtherHeroPositions();
            var golds = gameBoard.GetGoldPositions().FindAll(cell => me.StepBetweePoint(cell) <= areaFindGold);
            var enemyNear = enemies.FindAll(cell => Math.Abs(cell.X - me.X) <= areaFindEnemey && Math.Abs(cell.Y - me.Y) <= areaFindEnemey);

            return LoderunnerActionToString(LoderunnerAction.GoLeft);
        }

        /// <summary>
        /// Starts loderunner's client shutdown.
        /// </summary>
        public void InitiateExit()
        {
            ShouldExit = true;
        }
    }

    public static class ExtensionsForBoard
    {
        /// <summary></summary>
        /// <returns>1 - if can go and stay, -1 - if can go and fall, 0 - not go, 2 - can be drill</returns>
        public static int CanGo(this BoardPoint position, GameBoard gb, LoderunnerAction direction)
        {
            if (direction == LoderunnerAction.GoUp)
            {
                if (position.ShiftTop().IsOutOfBoard(gb.Size))
                    return 0;

                return gb.HasElementAt(position, BoardElement.Ladder) ? 1 : 0;
            }

            BoardPoint pos;
            BoardElement el;
            switch (direction)
            {
                case LoderunnerAction.GoDown:
                    {
                        pos = position.ShiftBottom();
                        if (pos.IsOutOfBoard(gb.Size))
                            return 0;

                        el = gb.GetElementAt(pos);
                        if (el == BoardElement.Ladder || el == BoardElement.Pipe)
                            return 1;
                        else if (el == BoardElement.None)
                            return -1;
                        else if (CanBeDrill(el))
                            return 2;
                        else return 0;
                    }

                case LoderunnerAction.GoLeft:
                    {
                        pos = position.ShiftLeft();
                        if (pos.IsOutOfBoard(gb.Size))
                            return 0;

                        el = gb.GetElementAt(pos);
                        if (CanBeMovable(el))
                        {
                            if (gb.GetElementAt(pos.ShiftBottom()).CanBeFall())
                                return -1;
                            else return 1;
                        }
                        else return 0;
                    }

                case LoderunnerAction.GoRight:
                    {
                        pos = position.ShiftRight();
                        if (pos.IsOutOfBoard(gb.Size))
                            return 0;

                        el = gb.GetElementAt(pos);
                        if (CanBeMovable(el))
                        {
                            if (gb.GetElementAt(pos.ShiftBottom()).CanBeFall())
                                return -1;
                            else return 1;
                        }
                        else return 0;
                    }

                default: return 0;
            }
        }

        public static int AbsDefference(int i1, int i2)
        {
            return Math.Abs(i1 - i2);
        }

        public static int StepBetweePoint(this BoardPoint start, BoardPoint end)
        {
            return AbsDefference(start.X, end.X) + AbsDefference(start.Y, end.Y);
        }


        public static List<BoardPoint> FindPath(this GameBoard gb, BoardPoint start, BoardPoint end)
        {
            //step--;
            //if (step == 0)
            //    return null;

            var path = new List<BoardPoint>();
            bool verticalDirectionDown = start.Y - end.Y > 0;
            bool horizontalDirectionRigth = start.X - end.X < 0;
            List<BoardPoint> pathVertical;
            List<BoardPoint> pathHorizontal;


            if (verticalDirectionDown)
            {
                var canDown = start.CanGo(gb, LoderunnerAction.GoDown);

                if (canDown == 1 || canDown == -1)
                {
                    pathVertical = FindPath(gb, start.ShiftBottom(), end);
                    if (pathVertical != null)
                        return pathVertical;
                }


            }


            return null;
        }

        public static bool CanBeDrill(this BoardElement cell)
        {
            switch (cell)
            {
                case BoardElement.Brick:
                case BoardElement.DrillPit:
                case BoardElement.PitFill1:
                case BoardElement.PitFill2:
                case BoardElement.PitFill3:
                case BoardElement.PitFill4:
                    return true;

                default: return false;
            }
        }

        public static bool CanBeMovable(this BoardElement cell)
        {
            switch (cell)
            {
                //case BoardElement.PitFill1:
                case BoardElement.PitFill2:
                case BoardElement.PitFill3:
                case BoardElement.PitFill4:
                case BoardElement.None:
                case BoardElement.Gold:
                case BoardElement.Ladder:
                case BoardElement.Pipe:
                    return true;
                default: return false;
            }
        }

        public static bool CanBeFall(this BoardElement cell)
        {
            if (cell == BoardElement.Ladder || cell == BoardElement.Pipe)
                return false;

            return CanBeMovable(cell);
        }

    }

}
