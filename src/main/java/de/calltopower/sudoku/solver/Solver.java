/**
 * Sudoku
 * 
 * Copyright (c) 2014-2020 Denis Meyer
 */
package de.calltopower.sudoku.solver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.calltopower.sudoku.util.Constants;
import de.calltopower.sudoku.util.Grid;

public class Solver {

    private static final Logger LOGGER = LogManager.getLogger(Solver.class);

    private final Grid grid;
    private final int maxNrOfTries;
    private int currNrOfTries;

    public Solver(Grid grid, int maxNrOfTries) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Max. no. of tries: " + maxNrOfTries);
        }
        this.grid = new Grid();
        this.grid.copy(grid);
        this.maxNrOfTries = maxNrOfTries;
        currNrOfTries = 0;
    }

    public Grid getGrid() {
        return grid;
    }

    public boolean checkGrid(Grid grid) {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                for (int k = 0; k < Constants.GRID_SIZE; ++k) {
                    if ((j != k) && (grid.at(i, j) == grid.at(i, k))) {
                        return false;
                    }
                }

                for (int k = 0; k < Constants.GRID_SIZE; ++k) {
                    if ((i != k) && (grid.at(i, j) == grid.at(k, j))) {
                        return false;
                    }
                }

                int boxRowOffset = (i / 3) * 3;
                int boxColOffset = (j / 3) * 3;
                for (int k = 0; k < 3; ++k) {
                    for (int m = 0; m < 3; ++m) {
                        if ((((boxRowOffset + k) != i) || ((boxColOffset + m) != j))
                                && (grid.at(boxRowOffset + k, boxColOffset + m) == grid.at(i, j))) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean checkGridValidity(Grid grid) {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                for (int k = 0; k < Constants.GRID_SIZE; ++k) {
                    if ((j != k) && (grid.at(i, j) != 0) && (grid.at(i, k) != 0) && (grid.at(i, j) == grid.at(i, k))) {
                        return false;
                    }
                }

                for (int k = 0; k < Constants.GRID_SIZE; ++k) {
                    if ((i != k) && (grid.at(i, j) != 0) && (grid.at(i, k) != 0) && (grid.at(i, j) == grid.at(k, j))) {
                        return false;
                    }
                }

                int boxRowOffset = (i / 3) * 3;
                int boxColOffset = (j / 3) * 3;
                for (int k = 0; k < 3; ++k) {
                    for (int m = 0; m < 3; ++m) {
                        if ((((boxRowOffset + k) != i) || ((boxColOffset + m) != j))
                                && (grid.at(boxRowOffset + k, boxColOffset + m) != 0) && (grid.at(i, j) != 0)
                                && (grid.at(boxRowOffset + k, boxColOffset + m) == grid.at(i, j))) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public boolean solve(int i, int j) {
        ++currNrOfTries;
        if (currNrOfTries >= maxNrOfTries) {
            return false;
        }

        if (i == Constants.GRID_SIZE) {
            i = 0;
            if (++j == Constants.GRID_SIZE) {
                return true;
            }
        }
        if (grid.at(i, j) != 0) {
            return solve(i + 1, j);
        }

        for (int val = 1; val <= Constants.GRID_SIZE; ++val) {
            if (legal(i, j, val)) {
                grid.set(i, j, val);
                if (solve(i + 1, j)) {
                    return true;
                }
            }
        }
        grid.set(i, j, 0);

        return false;
    }

    private boolean legal(int i, int j, int val) {
        for (int k = 0; k < Constants.GRID_SIZE; ++k) {
            if (val == grid.at(i, k)) {
                return false;
            }
        }

        for (int k = 0; k < Constants.GRID_SIZE; ++k) {
            if (val == grid.at(k, j)) {
                return false;
            }
        }

        int boxRowOffset = (i / 3) * 3;
        int boxColOffset = (j / 3) * 3;
        for (int k = 0; k < 3; ++k) {
            for (int m = 0; m < 3; ++m) {
                if (grid.at(boxRowOffset + k, boxColOffset + m) == val) {
                    return false;
                }
            }
        }

        return true;
    }

}
