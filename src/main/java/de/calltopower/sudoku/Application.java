/**
 * Sudoku
 * 
 * Copyright (c) 2014-2020 Denis Meyer
 */
package de.calltopower.sudoku;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.calltopower.sudoku.gui.SplashScreen;
import de.calltopower.sudoku.gui.SudokuUI;
import de.calltopower.sudoku.util.Helper;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger(Application.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Helper.printSystemInformation();
        LOGGER.debug("Setting up the UI");
        SudokuUI sui = new SudokuUI();
        LOGGER.debug("Initializing platform specifications");
        Helper.setPlatformLookAndFeel();
        new SplashScreen().setVisible();
        sui.setVisible(true);
    }

}
