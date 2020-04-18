/**
 * Sudoku
 * 
 * Copyright (c) 2014-2020 Denis Meyer
 */
package de.calltopower.sudoku.util;

import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Helper {

    private static final Logger LOGGER = LogManager.getLogger(Helper.class);

    private Helper() {
        // Nothing to see here...
    }

    public static void setPlatformLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            LOGGER.error("ClassNotFoundException:" + e.getMessage());
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException:" + e.getMessage());
        } catch (InstantiationException e) {
            LOGGER.error("InstantiationException:" + e.getMessage());
        } catch (UnsupportedLookAndFeelException e) {
            LOGGER.error("UnsupportedLookAndFeelException:" + e.getMessage());
        }
    }

    public static void printSystemInformation() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.debug("");
            LOGGER.debug("==============================");
            LOGGER.debug("Sudoku version " + Constants.APP_VERSION);
            LOGGER.debug("==============================");
            LOGGER.debug("");
            LOGGER.debug("------------------------------");
            LOGGER.debug("Java information");
            LOGGER.debug("------------------------------");
            LOGGER.debug("\t" + "Version " + System.getProperty("java.version"));
            LOGGER.debug("\t" + "Vendor: " + System.getProperty("java.vendor"));
            LOGGER.debug("\t" + "Vendor URL: " + System.getProperty("java.vendor.url"));
            LOGGER.debug("\t" + "Class path: " + System.getProperty("java.class.path"));
            LOGGER.debug("\t" + "Home: " + System.getProperty("java.home"));
            LOGGER.debug("");
            LOGGER.debug("------------------------------");
            LOGGER.debug("Operating system information");
            LOGGER.debug("------------------------------");
            LOGGER.debug("\t" + "Name: " + System.getProperty("os.name"));
            LOGGER.debug("\t" + "Arch: " + System.getProperty("os.arch"));
            LOGGER.debug("\t" + "Version: " + System.getProperty("os.version"));
            // logger.debug("\t" + "File separator: " +
            // System.getProperty("file.separator"));
            // logger.debug("\t" + "Line separator: " +
            // System.getProperty("line.separator"));
            // logger.debug("\t" + "Path separator: " +
            // System.getProperty("path.separator"));
            LOGGER.debug("");
            LOGGER.debug("------------------------------");
            LOGGER.debug("User information");
            LOGGER.debug("------------------------------");
            LOGGER.debug("\t" + "Name: " + System.getProperty("user.name"));
            LOGGER.debug("\t" + "Language: " + System.getProperty("user.language") + " (" + Locale.getDefault() + ")");
            LOGGER.debug("\t" + "Directory: " + System.getProperty("user.dir"));
            LOGGER.debug("\t" + "Home: " + System.getProperty("user.home"));
            LOGGER.debug("");
            LOGGER.debug("------------------------------");
            LOGGER.debug("Application output");
            LOGGER.debug("------------------------------");
        }
    }

}
