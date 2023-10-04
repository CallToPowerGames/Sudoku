/**
 * Sudoku
 * 
 * Copyright (c) 2014-2023 Denis Meyer
 */
package de.calltopower.sudoku.util;

import java.awt.Color;

public class Constants {

    public final static String APP_NAME = "Sudoku";
    public final static String VENDOR_NAME = "CallToPower Games";
    public final static String APP_VERSION = "v1.2.1";

    public final static String APP_SAVENAME = "Sudoku_";
    public final static String APP_SAVENAME_SUFFIX = ".sudoku";

    public final static int MS_SPLASHSCREEN = 10;
    public final static int MS_SPLASHSCREEN_NOIMG = 2;

    public final static int GRID_SIZE = 9;

    public final static int DEFAULT_MAX_NR_OF_TRIES = 50000;

    // Minimum value must be >= 18
    public final static int MAX_NR_OF_GENERATED_NUMBERS_EASY = 35;
    public final static int MAX_NR_OF_GENERATED_NUMBERS_NORMAL = 28;
    public final static int MAX_NR_OF_GENERATED_NUMBERS_HARD = 22;
    public final static int MAX_NR_OF_GENERATED_NUMBERS_VERYHARD = 18;

    public final static Color COLOR_MARKED = new Color(238, 233, 233);
    public final static Color COLOR_UNMARKED = Color.WHITE;

    public final static String FIELD_REGEX = "[^1-" + Constants.GRID_SIZE + "]";
    public final static String TEXTFIELD_MAX_NR_REGEX = "[^0-" + Constants.GRID_SIZE + "]";

    public final static String IMAGE_SPLASHSCREEN = "img/splashscreen.png";
    public final static String IMAGE_SELECTED = "img/selected.png";
    public final static String IMAGE_ICON = "img/icon.png";

}
