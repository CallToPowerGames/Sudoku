/**
 * Sudoku
 * 
 * Copyright (c) 2014-2020 Denis Meyer
 */
package de.calltopower.sudoku.util;

public class Grid {

    private final Integer[][] field;

    public Grid() {
        field = new Integer[Constants.GRID_SIZE][Constants.GRID_SIZE];
        initGrid();
    }

    private void initGrid() {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                set(i, j, 0);
            }
        }
    }

    public int at(int i, int j) {
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            return field[i][j];
        } else {
            return -1;
        }
    }

    public boolean set(int i, int j, int val) {
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            field[i][j] = val;
            return true;
        } else {
            return false;
        }
    }

    public void copy(Grid gridToCopy) {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                set(i, j, gridToCopy.at(i, j));
            }
        }
    }

    public boolean isCompletelyFilled() {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                if (at(i, j) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean fromString(String gridStr) {
        Integer[][] newGrid = new Integer[Constants.GRID_SIZE][Constants.GRID_SIZE];
        String[] gridSpl = gridStr.split(" ");
        if (gridSpl.length != (Constants.GRID_SIZE * Constants.GRID_SIZE)) {
            return false;
        }
        int i = 0;
        int j = 0;
        for (String s : gridSpl) {
            try {
                int n = Integer.parseInt(s);
                newGrid[i][j] = n;
                ++j;
                if ((j % Constants.GRID_SIZE) == 0) {
                    ++i;
                    j = 0;
                }
            } catch (NumberFormatException ex) {
                return false;
            }
        }

        for (i = 0; i < Constants.GRID_SIZE; ++i) {
            for (j = 0; j < Constants.GRID_SIZE; ++j) {
                set(i, j, newGrid[i][j]);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String cont = "";
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                cont += at(i, j) + " ";
            }
            cont += "\n";
        }
        return cont;
    }

}
