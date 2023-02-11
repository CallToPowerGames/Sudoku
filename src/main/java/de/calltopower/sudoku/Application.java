/**
 * Sudoku
 * 
 * Copyright (c) 2014-2023 Denis Meyer
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

        LOGGER.debug("Setting up splashscreen");
        SplashScreen splashScreen = new SplashScreen();
        splashScreen.setVisible(true);

        splashScreen.setProgressAndWait(10);

        LOGGER.debug("Setting up UI");
        SudokuUI ui = new SudokuUI();

        splashScreen.setProgressAndWait(90);

        LOGGER.debug("Initializing platform specifications");
        Helper.setPlatformLookAndFeel();

        splashScreen.setProgressAndWait(100);

        LOGGER.debug("Disposing splashscreen");
        splashScreen.dispose();

        LOGGER.debug("Setting UI visible");
        ui.setVisible(true);
    }

}
