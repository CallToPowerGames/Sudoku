/**
 * Sudoku
 * 
 * Copyright (c) 2014-2023 Denis Meyer
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("==============================");
            LOGGER.info(Constants.APP_NAME);
            LOGGER.info("==============================");
            LOGGER.info("(C) " + Constants.VENDOR_NAME);
            LOGGER.info("Version " + Constants.APP_VERSION);
            LOGGER.info("");
            LOGGER.debug("------------------------------");
            LOGGER.debug("Java information");
            LOGGER.debug("------------------------------");
            LOGGER.debug("Version " + System.getProperty("java.version"));
            LOGGER.debug("Vendor: " + System.getProperty("java.vendor"));
            LOGGER.debug("Vendor URL: " + System.getProperty("java.vendor.url"));
            LOGGER.debug("Class path: " + System.getProperty("java.class.path"));
            LOGGER.debug("Home: " + System.getProperty("java.home"));
            LOGGER.debug("");
            LOGGER.debug("------------------------------");
            LOGGER.debug("Operating system information");
            LOGGER.debug("------------------------------");
            LOGGER.debug("Name: " + System.getProperty("os.name"));
            LOGGER.debug("Arch: " + System.getProperty("os.arch"));
            LOGGER.debug("Version: " + System.getProperty("os.version"));
            LOGGER.debug("");
            LOGGER.debug("------------------------------");
            LOGGER.debug("User information");
            LOGGER.debug("------------------------------");
            LOGGER.debug("Name: " + System.getProperty("user.name"));
            LOGGER.debug("Language: " + System.getProperty("user.language") + " (" + Locale.getDefault() + ")");
            LOGGER.debug("Directory: " + System.getProperty("user.dir"));
            LOGGER.debug("Home: " + System.getProperty("user.home"));
            LOGGER.debug("");
        }
    }

}
