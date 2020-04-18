/**
 * Sudoku
 * 
 * Copyright (c) 2014-2020 Denis Meyer
 */
package de.calltopower.sudoku.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.calltopower.sudoku.solver.Solver;
import de.calltopower.sudoku.util.Constants;
import de.calltopower.sudoku.util.Grid;

public class Generator {

    private static final Logger LOGGER = LogManager.getLogger(Generator.class);

    private final Random random = new Random();
    private final Grid grid;
    int maxNrOfTries;
    int currNrOfTries;
    int difficulty;

    public Generator(int difficulty, int maxNrOfTries) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Difficulty: " + difficulty + ", max. no. of tries: " + maxNrOfTries);
        }
        grid = new Grid();
        this.maxNrOfTries = maxNrOfTries;
        this.difficulty = difficulty;
        currNrOfTries = 0;
    }

    public Grid getGrid() {
        return grid;
    }

    public boolean generate() {
        LOGGER.debug("generate");
        generate_helper();
        Solver solver = null;
        boolean solved = false;
        while ((currNrOfTries <= maxNrOfTries) && !solved) {
            ++currNrOfTries;
            generate_helper();
            Grid g_tmp = new Grid();
            g_tmp.copy(grid);
            solver = new Solver(g_tmp, maxNrOfTries);
            solved = solver.solve(0, 0);
        }
        boolean generated = solved && (solver != null) && (currNrOfTries < maxNrOfTries);
        if (generated) {
            LOGGER.debug("Grid has been generated.");
        } else {
            LOGGER.error("Grid could not be generated.");
        }
        return generated;
    }

    private void generate_helper() {
        for (int i = 0; i < 5; ++i) {
            fillAndRemove();
        }

        while (countFilledFields() > difficulty) {
            if (random.nextBoolean()) {
                boolean makeBreak = false;
                for (int i = 0; (i < Constants.GRID_SIZE) && !makeBreak; ++i) {
                    for (int j = 0; (j < Constants.GRID_SIZE) && !makeBreak; j++) {
                        if ((grid.at(i, j) != 0) && (countFilledSmallGridAround(i, j) > 2) && random.nextBoolean()) {
                            grid.set(i, j, 0);
                            makeBreak = true;
                        }
                    }
                }
            } else {
                boolean makeBreak = false;
                for (int i = Constants.GRID_SIZE - 1; (i > 0) && !makeBreak; --i) {
                    for (int j = Constants.GRID_SIZE - 1; (j > 0) && !makeBreak; --j) {
                        if ((grid.at(i, j) != 0) && (countFilledSmallGridAround(i, j) > 2) && random.nextBoolean()) {
                            grid.set(i, j, 0);
                            makeBreak = true;
                        }
                    }
                }
            }
        }
    }

    private void fillAndRemove() {
        ArrayList<Integer> numbers = new ArrayList<>();
        int num;
        for (int i = 1; i <= (Constants.GRID_SIZE * Constants.GRID_SIZE); ++i) {
            num = random.nextInt(9) + 1;
            numbers.add(num);
        }
        Collections.shuffle(numbers);

        if (random.nextBoolean()) {
            for (int i = 0; i < Constants.GRID_SIZE; ++i) {
                for (int j = 0; j < Constants.GRID_SIZE; j++) {
                    if (grid.at(i, j) == 0) {
                        grid.set(i, j, numbers.get(i + j));
                    }
                }
            }
        } else {
            for (int i = Constants.GRID_SIZE - 1; i > 0; --i) {
                for (int j = Constants.GRID_SIZE - 1; j > 0; --j) {
                    if (grid.at(i, j) == 0) {
                        grid.set(i, j, numbers.get(i + j));
                    }
                }
            }
        }

        if (random.nextBoolean()) {
            for (int i = 0; i < Constants.GRID_SIZE; ++i) {
                for (int j = 0; j < Constants.GRID_SIZE; j++) {
                    if (!isLegal(i, j)) {
                        grid.set(i, j, 0);
                    }
                }
            }
        } else {
            for (int i = Constants.GRID_SIZE - 1; i > 0; --i) {
                for (int j = Constants.GRID_SIZE - 1; j > 0; --j) {
                    if (!isLegal(i, j)) {
                        grid.set(i, j, 0);
                    }
                }
            }
        }
    }

    private int countFilledSmallGridAround(int i, int j) {
        int count = 0;
        int boxRowOffset = (i / 3) * 3;
        int boxColOffset = (j / 3) * 3;
        for (int m = 0; m < 3; ++m) {
            for (int k = 0; k < 3; ++k) {
                if (grid.at(boxRowOffset + k, boxColOffset + m) != 0) {
                    ++count;
                }
            }
        }
        return count;
    }

    private int countFilledFields() {
        int count = 0;
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; j++) {
                if (grid.at(i, j) != 0) {
                    ++count;
                }
            }
        }
        return count;
    }

    private boolean isLegal(int i, int j) {
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

        return true;
    }

}
