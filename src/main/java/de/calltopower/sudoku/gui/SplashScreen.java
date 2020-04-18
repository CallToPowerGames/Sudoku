/**
 * Sudoku
 * 
 * Copyright (c) 2014-2020 Denis Meyer
 */
package de.calltopower.sudoku.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.calltopower.sudoku.util.Constants;

@SuppressWarnings("serial")
public class SplashScreen extends JWindow {

    private static final Logger LOGGER = LogManager.getLogger(SplashScreen.class);

    private JLabel imageLabel = null;
    private JPanel southPanel = null;
    private JProgressBar progressBar = null;
    private ImageIcon imageIcon = null;
    private BorderLayout borderLayout = null;
    private FlowLayout flowLayout = null;

    public SplashScreen() {
        LOGGER.debug("Initializing components");
        URL splashIconURL = getClass().getClassLoader().getResource(Constants.IMAGE_SPLASHSCREEN);
        if (splashIconURL != null) {
            this.imageIcon = new ImageIcon(splashIconURL);

            borderLayout = new BorderLayout();
            imageLabel = new JLabel();
            southPanel = new JPanel();
            flowLayout = new FlowLayout();
            progressBar = new JProgressBar();

            initLayout();
        }
    }

    public void setProgress(int progress) {
        final int theProgress = progress;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setValue(theProgress);
            }
        });
    }

    private void initLayout() {
        // Set the (upper) Image
        if (imageIcon != null) {
            imageLabel.setIcon(imageIcon);
        }

        southPanel.setLayout(flowLayout);
        southPanel.setBackground(Color.BLACK);
        southPanel.add(progressBar, null);

        setLayout(borderLayout);
        add(imageLabel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        pack();

        this.setLocationRelativeTo(null);
    }

    public void setVisible() {
        setVisible(true);
        for (int i = 0; i <= 100; ++i) {
            setProgress(i);
            try {
                Thread.sleep(Constants.MS_SPLASHSCREEN);
            } catch (InterruptedException ex) {
            }
        }
        dispose();
    }

}
