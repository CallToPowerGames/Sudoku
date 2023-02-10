/**
 * Sudoku
 * 
 * Copyright (c) 2014-2023 Denis Meyer
 */
package de.calltopower.sudoku.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class FileUtils {

    private static final Logger LOGGER = LogManager.getLogger(FileUtils.class);

    private FileUtils() {
        // Nothing to see here...
    }

    public static boolean fileExists(String fileName) {
        File f = new File(fileName);
        return f.exists();
    }

    public static boolean writeToFile(String fileName, Grid grid) {
        return writeToFile(fileName, grid.toString());
    }

    public static String getName(String fileName) {
        File f = new File(fileName);
        return f.getName();
    }

    public static boolean writeToFile(String fileName, String content) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Writing to file '" + fileName + "': " + content);
        }
        BufferedWriter writer = null;
        try {
            File f = new File(fileName);

            if (!f.exists()) {
                LOGGER.debug("File does not exist");
                if (!f.createNewFile()) {
                    LOGGER.error("Could not create new file '" + fileName + "'");
                    return false;
                }
            }
            if (f.canWrite()) {
                writer = new BufferedWriter(new FileWriter(f));
                writer.write(content);
                return true;
            } else {
                LOGGER.error("Could not write to file '" + fileName + "'");
            }
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage());
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                LOGGER.error("Exception: " + e.getMessage());
            }
        }
        return false;
    }

    public static String readFromFile(String fileName) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Trying to read from file '" + fileName);
        }
        BufferedReader reader = null;
        try {
            File f = new File(fileName);

            if (!f.exists()) {
                LOGGER.error("File '" + fileName + "' does not exist");
                return "";
            }
            if (!f.canRead()) {
                LOGGER.error("Cannot read file '" + fileName + "'");
                return "";
            }
            String cont = "";
            reader = new BufferedReader(new FileReader(f));
            String c_line = reader.readLine();
            while (c_line != null) {
                cont += " " + c_line.trim();
                c_line = reader.readLine();
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Successfully read from file: " + cont.trim());
            }
            return cont.trim();
        } catch (Exception e) {
            LOGGER.error("Exception: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                LOGGER.error("Exception: " + e.getMessage());
            }
        }

        return "";
    }

}
