/**
 * Sudoku
 * 
 * Copyright (c) 2014-2023 Denis Meyer
 */
package de.calltopower.sudoku.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.calltopower.sudoku.generator.Generator;
import de.calltopower.sudoku.solver.Solver;
import de.calltopower.sudoku.util.Constants;
import de.calltopower.sudoku.util.FileUtils;
import de.calltopower.sudoku.util.Grid;

public class SudokuUI extends javax.swing.JFrame {

    private static final long serialVersionUID = 8582947873987149171L;

    private static final Logger LOGGER = LogManager.getLogger(SudokuUI.class);

    private final Random random = new Random();

    private final Color colorBorder = new Color(0, 0, 0);
    private final Color colorStep = new Color(198, 226, 255);
    private int maxNrOfTries = Constants.DEFAULT_MAX_NR_OF_TRIES;
    private boolean actionRunning = false;
    private Grid grid = null;
    private Grid grid_hidden = null;
    private GridUI gridUI = null;
    private int difficulty = Constants.MAX_NR_OF_GENERATED_NUMBERS_NORMAL;
    private boolean clickedQuit = false;
    private ImageIcon icon = null;

    public SudokuUI() {
        LOGGER.debug("Initializing grid and grid UI");
        grid = new Grid();
        gridUI = new GridUI();

        LOGGER.debug("Initializing components");
        initComponents();
        initMoreComponents();
        if (System.getProperty("os.name").startsWith("Mac")) {
            menubar.remove(menu_jsudoku);
        }
        URL imgURL = getClass().getClassLoader().getResource(Constants.IMAGE_ICON);
        if (imgURL != null) {
            icon = new ImageIcon(imgURL);
            this.setIconImage(icon.getImage());
        } else {
            LOGGER.error("Could not load image file '" + Constants.IMAGE_ICON + "'");
        }

        setDifficulty(Constants.MAX_NR_OF_GENERATED_NUMBERS_NORMAL);
        menu_settings_maxDepth.setText("Max. depth (currently " + maxNrOfTries + ")");

        this.setLocationRelativeTo(null);

        final JFrame jf = this;
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!clickedQuit) {
                    quit();
                } else {
                    jf.dispose();
                }
            }
        });

        requestMainFocus();
    }

    private void quit() {
        LOGGER.info("Asking to quit");
        if (JOptionPane.showConfirmDialog(this, "Cancel current Sudoku and quit?", "Quit Sudoku",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, icon) == JOptionPane.YES_OPTION) {
            LOGGER.info("Clicked 'Yes'. Quitting...");
            this.dispose();
            System.exit(0);
        } else {
            LOGGER.info("Clicked 'No'");
        }
        clickedQuit = false;
    }

    private void initMoreComponents() {
        for (int i = 0; i < Constants.GRID_SIZE; ++i) {
            for (int j = 0; j < Constants.GRID_SIZE; ++j) {
                gridUI.init(i, j);
                if (i < 3) {
                    if (j < 3) {
                        panel_1_1.add(gridUI.get(i, j));
                    } else if ((j >= 3) && (j < 6)) {
                        panel_1_2.add(gridUI.get(i, j));
                    } else {
                        panel_1_3.add(gridUI.get(i, j));
                    }
                } else if ((i >= 3) && (i < 6)) {
                    if (j < 3) {
                        panel_2_1.add(gridUI.get(i, j));
                    } else if ((j >= 3) && (j < 6)) {
                        panel_2_2.add(gridUI.get(i, j));
                    } else {
                        panel_2_3.add(gridUI.get(i, j));
                    }
                } else {
                    if (j < 3) {
                        panel_3_1.add(gridUI.get(i, j));
                    } else if ((j >= 3) && (j < 6)) {
                        panel_3_2.add(gridUI.get(i, j));
                    } else {
                        panel_3_3.add(gridUI.get(i, j));
                    }
                }
                gridUI.get(i, j).addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        checkWOErrors();
                    }
                });
            }
        }
        panel_1_1.setBorder(javax.swing.BorderFactory.createLineBorder(colorBorder));
        panel_1_2.setBorder(javax.swing.BorderFactory.createLineBorder(colorBorder));
        panel_1_3.setBorder(javax.swing.BorderFactory.createLineBorder(colorBorder));
        panel_2_1.setBorder(javax.swing.BorderFactory.createLineBorder(colorBorder));
        panel_2_2.setBorder(javax.swing.BorderFactory.createLineBorder(colorBorder));
        panel_2_3.setBorder(javax.swing.BorderFactory.createLineBorder(colorBorder));
        panel_3_1.setBorder(javax.swing.BorderFactory.createLineBorder(colorBorder));
        panel_3_2.setBorder(javax.swing.BorderFactory.createLineBorder(colorBorder));
        panel_3_3.setBorder(javax.swing.BorderFactory.createLineBorder(colorBorder));
    }

    private boolean checkAndWarnIfFilled() {
        boolean anyFieldFilled = gridUI.anyFieldFilled();
        boolean ret = !anyFieldFilled || (anyFieldFilled
                && JOptionPane.showConfirmDialog(this, "Cancel current Sudoku?", "Cancel current Sudoku",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, icon) == JOptionPane.YES_OPTION);
        if (ret && LOGGER.isDebugEnabled()) {
            LOGGER.debug("No field is filled (" + (!anyFieldFilled) + ") or clicked 'Yes'.");
        }

        return ret;
    }

    private void requestMainFocus() {
        menubar.requestFocus();
    }

    private void generateSudoku() {
        LOGGER.info("Generating new Sudoku");

        if (!checkAndWarnIfFilled()) {
            LOGGER.debug("Gaming on...");
            return;
        }

        if (actionRunning) {
            LOGGER.warn("Another action is currently running...");
            return;
        }

        try {
            LOGGER.debug("Trying to start Thread...");
            final Component mainComponent = this;
            new Thread() {
                @Override
                public void run() {
                    LOGGER.debug("Disabling all actions");
                    enableAllActions(false);
                    LOGGER.debug("Clearing grid");
                    gridUI.clearGrid(grid);

                    try {
                        Generator generator = new Generator(difficulty, maxNrOfTries);
                        if (generator.generate()) {
                            grid.copy(generator.getGrid());
                            gridUI.writeGrid(grid);
                            gridUI.fillFilled(false, true);
                        } else {
                            LOGGER.warn(
                                    "No Sudoku could be generated.\\nPlease try again with an increased max. depth.");
                            JOptionPane.showMessageDialog(mainComponent,
                                    "No Sudoku could be generated.\nPlease try again with an increased max. depth.",
                                    "Sudoku not generated", JOptionPane.ERROR_MESSAGE, icon);
                        }
                    } catch (Exception ex) {
                        LOGGER.error("Exception while generating Sudoku", ex);
                    } finally {
                        enableAllActions(true);
                        requestMainFocus();
                        LOGGER.info("Done generating a new Sudoku");
                    }
                }
            }.start();
        } catch (Exception e) {
            LOGGER.error("Exception while threading", e);
        }
    }

    private void step() {
        LOGGER.info("Stepping");

        if (actionRunning) {
            LOGGER.warn("Another action is currently running...");
            return;
        }

        try {
            final Component mainComponent = this;
            new Thread() {
                @Override
                public void run() {
                    enableAllActions(false);
                    try {
                        if (grid_hidden == null) {
                            grid_hidden = new Grid();
                        }
                        gridUI.getGrid(grid_hidden);
                        Solver solver = new Solver(grid_hidden, maxNrOfTries);
                        if (grid_hidden.isCompletelyFilled()) {
                            LOGGER.info("Grid is completely filled");
                            if (!solver.checkGrid(grid_hidden)) {
                                LOGGER.info("The solution to this Sudoku is not correct.");
                                JOptionPane.showMessageDialog(mainComponent,
                                        "The solution to this Sudoku is not correct.", "Solution not correct",
                                        JOptionPane.ERROR_MESSAGE, icon);
                            } else {
                                LOGGER.info("This Sudoku has been correctly solved.");
                                JOptionPane.showMessageDialog(mainComponent, "This Sudoku has been correctly solved.",
                                        "Sudoku solved", JOptionPane.INFORMATION_MESSAGE, icon);
                                gridUI.fillFilled(false, false);
                            }
                        } else {
                            LOGGER.info("Grid is not completely filled");
                            if (solver.solve(0, 0)) {
                                grid_hidden.copy(solver.getGrid());
                                boolean makeBreak = false;
                                int curr_depth = 0;
                                int rand_i;
                                int rand_j;
                                while ((curr_depth < maxNrOfTries) && !makeBreak) {
                                    rand_i = random.nextInt(9);
                                    rand_j = random.nextInt(9);
                                    ++curr_depth;
                                    if (grid.at(rand_i, rand_j) == 0) {
                                        grid.set(rand_i, rand_j, grid_hidden.at(rand_i, rand_j));
                                        gridUI.set(rand_i, rand_j, grid.at(rand_i, rand_j), colorStep);
                                        makeBreak = true;
                                    }
                                }
                                if (!makeBreak) {
                                    for (int i = 0; (i < 9) && !makeBreak; ++i) {
                                        for (int j = 0; (j < 9) && !makeBreak; ++j) {
                                            if (grid.at(i, j) == 0) {
                                                grid.set(i, j, grid_hidden.at(i, j));
                                                gridUI.set(i, j, grid.at(i, j));
                                                makeBreak = true;
                                            }
                                        }
                                    }
                                }
                            } else {
                                LOGGER.warn(
                                        "This Sudoku could not be stepped. You could try again with an increased max. depth.");
                                JOptionPane.showMessageDialog(mainComponent,
                                        "This Sudoku could not be stepped.\nYou could try again with an increased max. depth.",
                                        "Sudoku not stepped", JOptionPane.ERROR_MESSAGE, icon);
                            }
                        }
                    } catch (Exception ex) {
                        LOGGER.error("Exception while stepping", ex);
                    } finally {
                        grid_hidden = null;
                        enableAllActions(true);
                        requestMainFocus();
                        LOGGER.info("Done stepping");
                    }
                }
            }.start();
        } catch (Exception e) {
            LOGGER.error("Exception while threading", e);
        }
    }

    private void enableAllActions(boolean enable) {
        actionRunning = !enable;
        menu_jsudoku.setEnabled(enable);
        menu_options.setEnabled(enable);
        menu_settings.setEnabled(enable);
    }

    private void checkWOErrors() {
        LOGGER.info("Checking (w/o errors)");

        if (actionRunning) {
            LOGGER.warn("Another action is currently running...");
            return;
        }

        try {
            final Component mainComponent = this;
            new Thread() {
                @Override
                public void run() {
                    enableAllActions(false);
                    try {
                        if (grid_hidden == null) {
                            grid_hidden = new Grid();
                        }
                        gridUI.getGrid(grid_hidden);
                        Solver solver = new Solver(grid_hidden, maxNrOfTries);

                        if (solver.checkGridValidity(grid_hidden) && solver.solve(0, 0)
                                && grid_hidden.isCompletelyFilled()) {
                            LOGGER.info("This Sudoku has been correctly solved.");
                            JOptionPane.showMessageDialog(mainComponent, "This Sudoku has been correctly solved.",
                                    "Sudoku solved", JOptionPane.INFORMATION_MESSAGE, icon);
                        }
                    } catch (Exception ex) {
                        LOGGER.error("Exception while checking (w/o errors)", ex);
                    } finally {
                        grid_hidden = null;
                        enableAllActions(true);
                        requestMainFocus();
                        LOGGER.info("Done checking (w/o errors)");
                    }
                }
            }.start();
        } catch (Exception e) {
            LOGGER.error("Exception while threading", e);
        }
    }

    private void check() {
        LOGGER.info("Checking");

        if (actionRunning) {
            LOGGER.warn("Another action is currently running...");
            return;
        }

        try {
            final Component mainComponent = this;
            new Thread() {
                @Override
                public void run() {
                    enableAllActions(false);
                    try {
                        if (grid_hidden == null) {
                            grid_hidden = new Grid();
                        }
                        gridUI.getGrid(grid_hidden);
                        Solver solver = new Solver(grid_hidden, maxNrOfTries);

                        if (!solver.checkGridValidity(grid_hidden)) {
                            JOptionPane.showMessageDialog(mainComponent, "This Sudoku contains one or more errors.",
                                    "Solution not correct", JOptionPane.ERROR_MESSAGE, icon);
                        } else {
                            if (solver.solve(0, 0)) {
                                if (grid_hidden.isCompletelyFilled()) {
                                    LOGGER.info("This Sudoku has been correctly solved.");
                                    JOptionPane.showMessageDialog(mainComponent,
                                            "This Sudoku has been correctly solved.", "Sudoku solved",
                                            JOptionPane.INFORMATION_MESSAGE, icon);
                                } else {
                                    LOGGER.info("This Sudoku is valid.");
                                    JOptionPane.showMessageDialog(mainComponent, "This Sudoku is valid.",
                                            "Sudoku valid", JOptionPane.INFORMATION_MESSAGE, icon);
                                }
                            } else {
                                LOGGER.info(
                                        "This Sudoku is not valid. You could try again with an increased max. depth.");
                                JOptionPane.showMessageDialog(mainComponent,
                                        "This Sudoku is not valid.\n\nYou could try again with an increased max. depth.",
                                        "Sudoku not valid", JOptionPane.ERROR_MESSAGE, icon);
                            }
                        }
                    } catch (Exception ex) {
                        LOGGER.error("Exception while checking", ex);
                    } finally {
                        grid_hidden = null;
                        enableAllActions(true);
                        requestMainFocus();
                        LOGGER.info("Done checking");
                    }
                }
            }.start();
        } catch (Exception e) {
            LOGGER.error("Exception while threading", e);
        }
    }

    private void solve() {
        LOGGER.info("Solving");

        if (actionRunning) {
            LOGGER.warn("Another action is currently running...");
            return;
        }

        if (!gridUI.checkGrid()) {
            LOGGER.info("The grid is not valid...");
        }

        try {
            final Component mainComponent = this;
            new Thread() {
                @Override
                public void run() {
                    enableAllActions(false);
                    gridUI.fillFilled(false, false);

                    try {
                        gridUI.getGrid(grid);
                        Solver solver = new Solver(grid, maxNrOfTries);

                        if (!solver.checkGridValidity(grid)) {
                            LOGGER.info("This Sudoku contains one or more errors.");
                            JOptionPane.showMessageDialog(mainComponent, "This Sudoku contains one or more errors.\n",
                                    "Solution not correct", JOptionPane.ERROR_MESSAGE, icon);
                            gridUI.fillFilled(true, true);
                        } else {
                            if (grid.isCompletelyFilled()) {
                                LOGGER.info("This Sudoku has already been solved correctly.");
                                JOptionPane.showMessageDialog(mainComponent,
                                        "This Sudoku has already been solved correctly.\n", "Sudoku already solved",
                                        JOptionPane.INFORMATION_MESSAGE, icon);
                            } else {
                                if (solver.solve(0, 0)) {
                                    grid.copy(solver.getGrid());
                                    gridUI.writeGrid(grid);
                                } else {
                                    LOGGER.info(
                                            "This Sudoku could not be solved. You could edit it or try again with an increased max. depth.");
                                    gridUI.getGrid(grid);
                                    JOptionPane.showMessageDialog(mainComponent,
                                            "This Sudoku could not be solved.\nYou could edit it or try again with an increased max. depth.",
                                            "Sudoku not solved", JOptionPane.ERROR_MESSAGE, icon);
                                    gridUI.fillFilled(true, true);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        LOGGER.error("Exception while solving", ex);
                    } finally {
                        enableAllActions(true);
                        requestMainFocus();
                        LOGGER.info("Done solving");
                    }
                }
            }.start();
        } catch (Exception e) {
            LOGGER.error("Exception while threading", e);
        }
    }

    private void setDifficulty(int _difficulty) {
        URL selectedIconUrl = getClass().getClassLoader().getResource(Constants.IMAGE_SELECTED);
        ImageIcon selectedImageIcon = null;
        if (selectedIconUrl != null) {
            selectedImageIcon = new ImageIcon(new ImageIcon(selectedIconUrl).getImage().getScaledInstance(15, 15, 0));
        } else {
            LOGGER.error("Could not load image file '" + Constants.IMAGE_SELECTED + "'");
        }
        if (_difficulty == Constants.MAX_NR_OF_GENERATED_NUMBERS_EASY) {
            LOGGER.info("Setting difficulty to easy");
            difficulty = _difficulty;
            menu_settings_menu_difficulty_item_easy.setSelected(true);
            menu_settings_menu_difficulty_item_normal.setSelected(false);
            menu_settings_menu_difficulty_item_hard.setSelected(false);
            menu_settings_menu_difficulty_item_veryhard.setSelected(false);
            menu_settings_menu_difficulty_item_easy.setIcon(selectedImageIcon);
            menu_settings_menu_difficulty_item_normal.setIcon(null);
            menu_settings_menu_difficulty_item_hard.setIcon(null);
            menu_settings_menu_difficulty_item_veryhard.setIcon(null);
        } else if (_difficulty == Constants.MAX_NR_OF_GENERATED_NUMBERS_HARD) {
            LOGGER.info("Setting difficulty to hard");
            difficulty = _difficulty;
            menu_settings_menu_difficulty_item_easy.setSelected(false);
            menu_settings_menu_difficulty_item_normal.setSelected(false);
            menu_settings_menu_difficulty_item_hard.setSelected(true);
            menu_settings_menu_difficulty_item_veryhard.setSelected(false);
            menu_settings_menu_difficulty_item_easy.setIcon(null);
            menu_settings_menu_difficulty_item_normal.setIcon(null);
            menu_settings_menu_difficulty_item_hard.setIcon(selectedImageIcon);
            menu_settings_menu_difficulty_item_veryhard.setIcon(null);
        } else if (_difficulty == Constants.MAX_NR_OF_GENERATED_NUMBERS_VERYHARD) {
            LOGGER.info("Setting difficulty to very hard");
            difficulty = _difficulty;
            menu_settings_menu_difficulty_item_easy.setSelected(false);
            menu_settings_menu_difficulty_item_normal.setSelected(false);
            menu_settings_menu_difficulty_item_hard.setSelected(false);
            menu_settings_menu_difficulty_item_veryhard.setSelected(true);
            menu_settings_menu_difficulty_item_easy.setIcon(null);
            menu_settings_menu_difficulty_item_normal.setIcon(null);
            menu_settings_menu_difficulty_item_hard.setIcon(null);
            menu_settings_menu_difficulty_item_veryhard.setIcon(selectedImageIcon);
        } else {
            LOGGER.info("Setting difficulty to normal");
            difficulty = Constants.MAX_NR_OF_GENERATED_NUMBERS_NORMAL;
            menu_settings_menu_difficulty_item_easy.setSelected(false);
            menu_settings_menu_difficulty_item_normal.setSelected(true);
            menu_settings_menu_difficulty_item_hard.setSelected(false);
            menu_settings_menu_difficulty_item_veryhard.setSelected(false);
            menu_settings_menu_difficulty_item_easy.setIcon(null);
            menu_settings_menu_difficulty_item_normal.setIcon(selectedImageIcon);
            menu_settings_menu_difficulty_item_hard.setIcon(null);
            menu_settings_menu_difficulty_item_veryhard.setIcon(null);
        }

        menu_options_generateActionPerformed(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        panel_all = new javax.swing.JPanel();
        panel_board = new javax.swing.JPanel();
        panel_1_1 = new javax.swing.JPanel();
        panel_1_2 = new javax.swing.JPanel();
        panel_1_3 = new javax.swing.JPanel();
        panel_2_1 = new javax.swing.JPanel();
        panel_2_2 = new javax.swing.JPanel();
        panel_2_3 = new javax.swing.JPanel();
        panel_3_1 = new javax.swing.JPanel();
        panel_3_2 = new javax.swing.JPanel();
        panel_3_3 = new javax.swing.JPanel();
        menubar = new javax.swing.JMenuBar();
        menu_jsudoku = new javax.swing.JMenu();
        menu_jsudoku_about = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        menu_jsudoku_quit = new javax.swing.JMenuItem();
        menu_file = new javax.swing.JMenu();
        menu_jsudoku_load = new javax.swing.JMenuItem();
        menu_jsudoku_save = new javax.swing.JMenuItem();
        menu_options = new javax.swing.JMenu();
        menu_options_generate = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        menu_options_check = new javax.swing.JMenuItem();
        menu_options_step = new javax.swing.JMenuItem();
        menu_options_solve = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menu_options_edit = new javax.swing.JMenuItem();
        menu_options_markFilledFields = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menu_options_clear_unmarked = new javax.swing.JMenuItem();
        menu_options_clear = new javax.swing.JMenuItem();
        menu_settings = new javax.swing.JMenu();
        menu_settings_menu_difficulty = new javax.swing.JMenu();
        menu_settings_menu_difficulty_item_easy = new javax.swing.JMenuItem();
        menu_settings_menu_difficulty_item_normal = new javax.swing.JMenuItem();
        menu_settings_menu_difficulty_item_hard = new javax.swing.JMenuItem();
        menu_settings_menu_difficulty_item_veryhard = new javax.swing.JMenuItem();
        menu_settings_maxDepth = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(549, 528));

        panel_all.setLayout(new java.awt.BorderLayout());

        panel_board.setLayout(new java.awt.GridLayout(3, 3));

        panel_1_1.setLayout(new java.awt.GridLayout(3, 3));
        panel_board.add(panel_1_1);

        panel_1_2.setLayout(new java.awt.GridLayout(3, 3));
        panel_board.add(panel_1_2);

        panel_1_3.setLayout(new java.awt.GridLayout(3, 3));
        panel_board.add(panel_1_3);

        panel_2_1.setLayout(new java.awt.GridLayout(3, 3));
        panel_board.add(panel_2_1);

        panel_2_2.setLayout(new java.awt.GridLayout(3, 3));
        panel_board.add(panel_2_2);

        panel_2_3.setLayout(new java.awt.GridLayout(3, 3));
        panel_board.add(panel_2_3);

        panel_3_1.setLayout(new java.awt.GridLayout(3, 3));
        panel_board.add(panel_3_1);

        panel_3_2.setLayout(new java.awt.GridLayout(3, 3));
        panel_board.add(panel_3_2);

        panel_3_3.setLayout(new java.awt.GridLayout(3, 3));
        panel_board.add(panel_3_3);

        panel_all.add(panel_board, java.awt.BorderLayout.CENTER);

        getContentPane().add(panel_all, java.awt.BorderLayout.CENTER);

        menu_jsudoku.setText("Sudoku");

        menu_jsudoku_about.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_jsudoku_about.setText("About");
        menu_jsudoku_about.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_jsudoku_aboutActionPerformed(evt);
            }
        });
        menu_jsudoku.add(menu_jsudoku_about);
        menu_jsudoku.add(jSeparator3);

        menu_jsudoku_quit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_jsudoku_quit.setText("Quit Sudoku");
        menu_jsudoku_quit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_jsudoku_quitActionPerformed(evt);
            }
        });
        menu_jsudoku.add(menu_jsudoku_quit);

        menubar.add(menu_jsudoku);

        menu_file.setText("File");

        menu_jsudoku_load.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_jsudoku_load.setText("Load Sudoku");
        menu_jsudoku_load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_jsudoku_loadActionPerformed(evt);
            }
        });
        menu_file.add(menu_jsudoku_load);

        menu_jsudoku_save.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_jsudoku_save.setText("Save Sudoku");
        menu_jsudoku_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_jsudoku_saveActionPerformed(evt);
            }
        });
        menu_file.add(menu_jsudoku_save);

        menubar.add(menu_file);

        menu_options.setText("Game");

        menu_options_generate.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_options_generate.setText("Generate");
        menu_options_generate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_options_generateActionPerformed(evt);
            }
        });
        menu_options.add(menu_options_generate);
        menu_options.add(jSeparator5);

        menu_options_check.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_options_check.setText("Check");
        menu_options_check.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_options_checkActionPerformed(evt);
            }
        });
        menu_options.add(menu_options_check);

        menu_options_step.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_options_step.setText("Step");
        menu_options_step.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_options_stepActionPerformed(evt);
            }
        });
        menu_options.add(menu_options_step);

        menu_options_solve.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_options_solve.setText("Solve");
        menu_options_solve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_options_solveActionPerformed(evt);
            }
        });
        menu_options.add(menu_options_solve);
        menu_options.add(jSeparator1);

        menu_options_edit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_options_edit.setText("Edit");
        menu_options_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_options_editActionPerformed(evt);
            }
        });
        menu_options.add(menu_options_edit);

        menu_options_markFilledFields.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_options_markFilledFields.setText("Mark filled fields");
        menu_options_markFilledFields.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_options_markFilledFieldsActionPerformed(evt);
            }
        });
        menu_options.add(menu_options_markFilledFields);
        menu_options.add(jSeparator2);

        menu_options_clear_unmarked.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_options_clear_unmarked.setText("Clear unmarked fields");
        menu_options_clear_unmarked.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_options_clear_unmarkedActionPerformed(evt);
            }
        });
        menu_options.add(menu_options_clear_unmarked);

        menu_options_clear.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_options_clear.setText("Clear all");
        menu_options_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_options_clearActionPerformed(evt);
            }
        });
        menu_options.add(menu_options_clear);

        menubar.add(menu_options);

        menu_settings.setText("Settings");

        menu_settings_menu_difficulty.setText("Difficulty");

        menu_settings_menu_difficulty_item_easy.setAccelerator(javax.swing.KeyStroke
                .getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_settings_menu_difficulty_item_easy.setText("Easy");
        menu_settings_menu_difficulty_item_easy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_settings_menu_difficulty_item_easyActionPerformed(evt);
            }
        });
        menu_settings_menu_difficulty.add(menu_settings_menu_difficulty_item_easy);

        menu_settings_menu_difficulty_item_normal.setAccelerator(javax.swing.KeyStroke
                .getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_settings_menu_difficulty_item_normal.setText("Normal");
        menu_settings_menu_difficulty_item_normal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_settings_menu_difficulty_item_normalActionPerformed(evt);
            }
        });
        menu_settings_menu_difficulty.add(menu_settings_menu_difficulty_item_normal);

        menu_settings_menu_difficulty_item_hard.setAccelerator(javax.swing.KeyStroke
                .getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_settings_menu_difficulty_item_hard.setText("Hard");
        menu_settings_menu_difficulty_item_hard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_settings_menu_difficulty_item_hardActionPerformed(evt);
            }
        });
        menu_settings_menu_difficulty.add(menu_settings_menu_difficulty_item_hard);

        menu_settings_menu_difficulty_item_veryhard.setAccelerator(javax.swing.KeyStroke
                .getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_settings_menu_difficulty_item_veryhard.setText("Very Hard");
        menu_settings_menu_difficulty_item_veryhard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_settings_menu_difficulty_item_veryHardActionPerformed(evt);
            }
        });
        menu_settings_menu_difficulty.add(menu_settings_menu_difficulty_item_veryhard);

        menu_settings.add(menu_settings_menu_difficulty);

        menu_settings_maxDepth.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_0,
                java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menu_settings_maxDepth.setText("Max. depth");
        menu_settings_maxDepth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_settings_maxDepthActionPerformed(evt);
            }
        });
        menu_settings.add(menu_settings_maxDepth);

        menubar.add(menu_settings);

        setJMenuBar(menubar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menu_settings_menu_difficulty_item_easyActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_settings_menu_difficulty_item_easyActionPerformed
        LOGGER.debug("Setting difficulty to easy");
        setDifficulty(Constants.MAX_NR_OF_GENERATED_NUMBERS_EASY);
    }// GEN-LAST:event_menu_settings_menu_difficulty_item_easyActionPerformed

    private void menu_settings_menu_difficulty_item_normalActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_settings_menu_difficulty_item_normalActionPerformed
        LOGGER.debug("Setting difficulty to normal");
        setDifficulty(Constants.MAX_NR_OF_GENERATED_NUMBERS_NORMAL);
    }// GEN-LAST:event_menu_settings_menu_difficulty_item_normalActionPerformed

    private void menu_settings_menu_difficulty_item_hardActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_settings_menu_difficulty_item_hardActionPerformed
        LOGGER.debug("Setting difficulty to hard");
        setDifficulty(Constants.MAX_NR_OF_GENERATED_NUMBERS_HARD);
    }// GEN-LAST:event_menu_settings_menu_difficulty_item_hardActionPerformed

    private void menu_settings_menu_difficulty_item_veryHardActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_settings_menu_difficulty_item_veryHardActionPerformed
        LOGGER.debug("Setting difficulty to hard");
        setDifficulty(Constants.MAX_NR_OF_GENERATED_NUMBERS_VERYHARD);
    }// GEN-LAST:event_menu_settings_menu_difficulty_item_veryHardActionPerformed

    private void menu_options_clearActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_options_clearActionPerformed
        LOGGER.debug("Clear grid");
        if (checkAndWarnIfFilled()) {
            gridUI.clearGrid(grid);
        }
    }// GEN-LAST:event_menu_options_clearActionPerformed

    private void menu_options_editActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_options_editActionPerformed
        LOGGER.debug("Edit grid");
        gridUI.editGrid();
    }// GEN-LAST:event_menu_options_editActionPerformed

    private void menu_options_generateActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_options_generateActionPerformed
        LOGGER.debug("Generate Sudoku");
        generateSudoku();
    }// GEN-LAST:event_menu_options_generateActionPerformed

    private void menu_options_checkActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_options_checkActionPerformed
        LOGGER.debug("Check");
        check();
    }// GEN-LAST:event_menu_options_checkActionPerformed

    private void menu_options_stepActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_options_stepActionPerformed
        LOGGER.debug("Step");
        step();
    }// GEN-LAST:event_menu_options_stepActionPerformed

    private void menu_options_solveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_options_solveActionPerformed
        LOGGER.debug("Solve");
        solve();
    }// GEN-LAST:event_menu_options_solveActionPerformed

    private void menu_settings_maxDepthActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_settings_maxDepthActionPerformed
        Object[] arr = { 10000, 25000, 50000, 100000, 250000, 500000 };
        int currSelected;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Current max. no. of tries: " + maxNrOfTries);
        }
        switch (maxNrOfTries) {
        default:
        case 10000:
            currSelected = 0;
            break;
        case 25000:
            currSelected = 1;
            break;
        case 50000:
            currSelected = 2;
            break;
        case 100000:
            currSelected = 3;
            break;
        case 250000:
            currSelected = 4;
            break;
        case 500000:
            currSelected = 5;
            break;
        }
        Object input = JOptionPane.showInputDialog(this,
                "Current max. depth is " + maxNrOfTries + ".\n" + "New max. depth:", "Set new max. depth",
                JOptionPane.INFORMATION_MESSAGE, icon, arr, arr[currSelected]);
        if (input != null) {
            maxNrOfTries = (int) input;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Set max. no. of tries to: " + maxNrOfTries);
            }
            menu_settings_maxDepth.setText("Max. depth (currently " + maxNrOfTries + ")");
            JOptionPane.showMessageDialog(this, "Successfully set a new max. depth of " + maxNrOfTries + ".",
                    "Set new max. depth", JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }// GEN-LAST:event_menu_settings_maxDepthActionPerformed

    private void menu_jsudoku_quitActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_jsudoku_quitActionPerformed
        LOGGER.debug("Quit");
        clickedQuit = true;
        quit();
    }// GEN-LAST:event_menu_jsudoku_quitActionPerformed

    private void menu_jsudoku_saveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_jsudoku_saveActionPerformed
        LOGGER.debug("Save");
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
        fc.setDialogTitle("Choose where to save the Sudoku to and give it a name:");
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileFilter ff = new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory() || (f.isFile() && f.getName().endsWith(Constants.APP_SAVENAME_SUFFIX))) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Folders and Sudoku files";
            }

        };
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(ff);
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            LOGGER.info("Approved saving");
            String saveName = fc.getSelectedFile().getAbsolutePath();
            if (FileUtils.getName(saveName).length() <= 0) {
                DateFormat df = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");
                Date today = Calendar.getInstance().getTime();
                String date = df.format(today);
                saveName = saveName + File.separator + Constants.APP_SAVENAME + date;
            }
            saveName = saveName.endsWith(Constants.APP_SAVENAME_SUFFIX) ? saveName
                    : (saveName + Constants.APP_SAVENAME_SUFFIX);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Trying to save to file '" + saveName + "'");
            }
            boolean goOn = FileUtils.fileExists(saveName);
            if (goOn) {
                LOGGER.debug(
                        "Another Sudoku with the chosen name already exists at that location. Asking to overwrite");
                if (JOptionPane.showConfirmDialog(this,
                        "Another Sudoku with the chosen name already exists at that location. Overwrite?",
                        "Overwrite Sudoku", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                        icon) == JOptionPane.NO_OPTION) {
                    goOn = false;
                    LOGGER.info("Overriding");
                } else {
                    LOGGER.info("Not overriding");
                }
            } else {
                goOn = true;
            }
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Saving Sudoku to " + "'" + saveName + "' as " + FileUtils.getName(saveName) + "'.");
            }
            if (goOn) {
                if (FileUtils.writeToFile(saveName, grid)) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Successfully saved current Sudoku as " + "'" + FileUtils.getName(saveName) + "'.");
                    }
                    JOptionPane.showMessageDialog(this,
                            "Successfully saved current Sudoku as\n" + "'" + FileUtils.getName(saveName) + "'.",
                            "Save successful", JOptionPane.INFORMATION_MESSAGE, icon);
                } else {
                    LOGGER.error("Could not save current Sudoku as " + "'" + FileUtils.getName(saveName) + "'.");
                    JOptionPane.showMessageDialog(this,
                            "Could not save current Sudoku as\n" + "'" + FileUtils.getName(saveName) + "'.",
                            "Save failed", JOptionPane.ERROR_MESSAGE, icon);
                }
            }
        } else {
            LOGGER.info("Did not approve saving");
        }
    }// GEN-LAST:event_menu_jsudoku_saveActionPerformed

    private void menu_jsudoku_loadActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_jsudoku_loadActionPerformed
        LOGGER.debug("Load");
        if (checkAndWarnIfFilled()) {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
            fc.setDialogTitle("Choose a file to load a Sudoku from:");
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            FileFilter ff = new FileFilter() {

                @Override
                public boolean accept(File f) {
                    if (f.isFile() && f.getName().endsWith(Constants.APP_SAVENAME_SUFFIX)) {
                        return true;
                    }
                    return false;
                }

                @Override
                public String getDescription() {
                    return "Sudoku files";
                }

            };
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(ff);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                LOGGER.info("Approved loading");
                String loadFile = fc.getSelectedFile().getAbsolutePath();
                String fromFile = FileUtils.readFromFile(loadFile);
                if (!fromFile.isEmpty() && !(fromFile.equals(""))) {
                    if (grid.fromString(fromFile)) {
                        gridUI.writeGrid(grid);
                        gridUI.editGrid();
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info("Successfully loaded Sudoku from " + "'" + FileUtils.getName(loadFile) + "'.");
                        }
                        JOptionPane.showMessageDialog(this,
                                "Successfully loaded Sudoku.\n\n"
                                        + "Tip: If the filled fields are final mark them via\n"
                                        + "'[Menu] Game - Mark filled fields'",
                                "Load successful", JOptionPane.INFORMATION_MESSAGE, icon);
                    } else {
                        LOGGER.error("Could not load Sudoku from " + "'" + FileUtils.getName(loadFile) + "'.");
                        JOptionPane.showMessageDialog(this,
                                "Could not load Sudoku from\n" + "'" + FileUtils.getName(loadFile) + "'.",
                                "Load failed", JOptionPane.ERROR_MESSAGE, icon);
                    }
                } else {
                    LOGGER.error("Could not load Sudoku from " + "'" + FileUtils.getName(loadFile)
                            + "', Sudoku file seems to be empty.");
                    JOptionPane.showMessageDialog(this,
                            "Could not load Sudoku from\n" + "'" + FileUtils.getName(loadFile)
                                    + "', Sudoku file seems to be empty.",
                            "Load failed", JOptionPane.ERROR_MESSAGE, icon);
                }
            } else {
                LOGGER.info("Did not approve loading");
            }
        }
    }// GEN-LAST:event_menu_jsudoku_loadActionPerformed

    private void menu_options_markFilledFieldsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_options_markFilledFieldsActionPerformed
        LOGGER.debug("Mark filled fields");
        gridUI.fillFilled(false, true);
    }// GEN-LAST:event_menu_options_markFilledFieldsActionPerformed

    private void menu_jsudoku_aboutActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_jsudoku_aboutActionPerformed
        LOGGER.debug("About");
        About about = new About(this);
        about.setVisible(true);
    }// GEN-LAST:event_menu_jsudoku_aboutActionPerformed

    private void menu_options_clear_unmarkedActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_menu_options_clear_unmarkedActionPerformed
        LOGGER.debug("Clear unmarked fields");
        if (checkAndWarnIfFilled()) {
            gridUI.clearUnmarked(grid);
        }
    }// GEN-LAST:event_menu_options_clear_unmarkedActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JMenu menu_file;
    private javax.swing.JMenu menu_jsudoku;
    private javax.swing.JMenuItem menu_jsudoku_about;
    private javax.swing.JMenuItem menu_jsudoku_load;
    private javax.swing.JMenuItem menu_jsudoku_quit;
    private javax.swing.JMenuItem menu_jsudoku_save;
    private javax.swing.JMenu menu_options;
    private javax.swing.JMenuItem menu_options_check;
    private javax.swing.JMenuItem menu_options_clear;
    private javax.swing.JMenuItem menu_options_clear_unmarked;
    private javax.swing.JMenuItem menu_options_edit;
    private javax.swing.JMenuItem menu_options_generate;
    private javax.swing.JMenuItem menu_options_markFilledFields;
    private javax.swing.JMenuItem menu_options_solve;
    private javax.swing.JMenuItem menu_options_step;
    private javax.swing.JMenu menu_settings;
    private javax.swing.JMenuItem menu_settings_maxDepth;
    private javax.swing.JMenu menu_settings_menu_difficulty;
    private javax.swing.JMenuItem menu_settings_menu_difficulty_item_easy;
    private javax.swing.JMenuItem menu_settings_menu_difficulty_item_hard;
    private javax.swing.JMenuItem menu_settings_menu_difficulty_item_veryhard;
    private javax.swing.JMenuItem menu_settings_menu_difficulty_item_normal;
    private javax.swing.JMenuBar menubar;
    private javax.swing.JPanel panel_1_1;
    private javax.swing.JPanel panel_1_2;
    private javax.swing.JPanel panel_1_3;
    private javax.swing.JPanel panel_2_1;
    private javax.swing.JPanel panel_2_2;
    private javax.swing.JPanel panel_2_3;
    private javax.swing.JPanel panel_3_1;
    private javax.swing.JPanel panel_3_2;
    private javax.swing.JPanel panel_3_3;
    private javax.swing.JPanel panel_all;
    private javax.swing.JPanel panel_board;
    // End of variables declaration//GEN-END:variables

}
