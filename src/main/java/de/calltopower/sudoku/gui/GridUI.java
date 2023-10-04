/**
 * Sudoku
 * 
 * Copyright (c) 2014-2023 Denis Meyer
 */
package de.calltopower.sudoku.gui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.calltopower.sudoku.util.Constants;
import de.calltopower.sudoku.util.Grid;

public class GridUI {

    private static final Logger LOGGER = LogManager.getLogger(GridUI.class);

    private final JTextField[][] textfields;

    public GridUI() {
        LOGGER.debug("Initializing components");
        textfields = new JTextField[Constants.GRID_SIZE][Constants.GRID_SIZE];
    }

    public void init(int i, int j) {
        // if (LOGGER.isDebugEnabled()) {
        // LOGGER.debug("Initializing " + i + ", " + j);
        // }
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            textfields[i][j] = new JTextField("");
            textfields[i][j].setHorizontalAlignment(JTextField.CENTER);
            textfields[i][j].addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    LOGGER.debug("keyReleased");
                    if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
                        String curText = ((JTextComponent) e.getSource()).getText();
                        curText = curText.trim().substring(0, 1).replaceAll(Constants.FIELD_REGEX, "");
                        ((JTextComponent) e.getSource()).setText(curText);
                    }
                }
            });
        }
    }

    public int at(int i, int j) {
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            int nr = 0;
            try {
                nr = Integer.parseInt(textfields[i][j].getText());
            } catch (NumberFormatException ex) {
                return nr;
            }
            return nr;
        } else {
            return 0;
        }
    }

    public void limit(int i, int j) {
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            textfields[i][j].setText(textfields[i][j].getText().substring(0, 1));
        }
    }

    public void enable(int i, int j, boolean enabled) {
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            textfields[i][j].setEnabled(enabled);
        }
    }

    public void editable(int i, int j, boolean editable) {
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            textfields[i][j].setEditable(editable);
        }
    }

    public boolean isFilled(int i, int j) {
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            return !textfields[i][j].getText().trim().isEmpty() && (at(i, j) != 0);
        }
        return false;
    }

    public boolean isMarked(int i, int j) {
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            return textfields[i][j].getBackground() == Constants.COLOR_MARKED;
        }
        return false;
    }

    public boolean anyFieldFilled() {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                if (isFilled(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    public JTextField get(int i, int j) {
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            return textfields[i][j];
        } else {
            return new JTextField(); // not null...
        }
    }

    public boolean set(int i, int j, int val) {
        return set(i, j, val, null);
    }

    public boolean set(int i, int j, int val, Color c) {
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            textfields[i][j].setText(String.valueOf(val));
            if (c != null) {
                textfields[i][j].setBackground(c);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean set(int i, int j, String val) {
        if ((i >= 0) && (i < Constants.GRID_SIZE) && (j >= 0) && (j < Constants.GRID_SIZE)) {
            textfields[i][j].setText(val);

            return true;
        } else {
            return false;
        }
    }

    public void writeGrid(Grid grid) {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                set(i, j, String.valueOf(((grid.at(i, j) == 0) || (grid.at(i, j) == -1)) ? "" : grid.at(i, j)));
            }
        }
    }

    public void getGrid(Grid grid) {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                if (isFilled(i, j)) {
                    grid.set(i, j, at(i, j));
                } else {
                    grid.set(i, j, 0);
                }
            }
        }
    }

    public boolean checkGrid() {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                if (isFilled(i, j)) {
                    int parsed = at(i, j);
                    if (parsed >= Constants.GRID_SIZE) {
                        limit(i, j);
                    }
                }
            }
        }
        return true;
    }

    public void fillFilled(boolean filledEditable, boolean otherEditable) {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                if (isFilled(i, j) && !filledEditable) {
                    get(i, j).setBackground(Constants.COLOR_MARKED);
                    editable(i, j, filledEditable);
                } else {
                    get(i, j).setBackground(Constants.COLOR_UNMARKED);
                    editable(i, j, otherEditable);
                }
            }
        }
    }

    public void clearUnmarked(Grid grid) {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                if (!isMarked(i, j)) {
                    enable(i, j, true);
                    editable(i, j, true);
                    get(i, j).setBackground(Constants.COLOR_UNMARKED);
                    set(i, j, "");
                    grid.set(i, j, 0);
                }
            }
        }
    }

    public void clearGrid(Grid grid) {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                enable(i, j, true);
                editable(i, j, true);
                get(i, j).setBackground(Constants.COLOR_UNMARKED);
                set(i, j, "");
                grid.set(i, j, 0);
            }
        }
    }

    public void editGrid() {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                enable(i, j, true);
                editable(i, j, true);
                get(i, j).setBackground(Constants.COLOR_UNMARKED);
            }
        }
    }

}
