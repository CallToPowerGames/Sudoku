/**
 * Sudoku
 * 
 * Copyright (c) 2014-2023 Denis Meyer
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

	private int currentProgress = 0;

	public SplashScreen() {
		LOGGER.debug("Initializing components");
		currentProgress = 0;
		URL splashIconURL = getClass().getClassLoader().getResource(Constants.IMAGE_SPLASHSCREEN);
		if (splashIconURL != null) {
			this.imageIcon = new ImageIcon(splashIconURL);
		} else {
			LOGGER.error("Could not load image file '" + Constants.IMAGE_SPLASHSCREEN + "'");
		}
		borderLayout = new BorderLayout();
		imageLabel = new JLabel();
		southPanel = new JPanel();
		flowLayout = new FlowLayout();
		progressBar = new JProgressBar();

		initLayout();

		setProgressAndWait(0);
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
		} else {
			imageLabel.setText("Loading Sudoku...");
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

	public void setProgressAndWait(int progress) {
		int _progress = progress;
		if (progress < 0) {
			_progress = 0;
		} else if (progress > 100) {
			_progress = 100;
		}

		int ms_wait = imageIcon != null ? Constants.MS_SPLASHSCREEN : Constants.MS_SPLASHSCREEN_NOIMG;

		if (_progress >= currentProgress) {
			int progressDiff = _progress - currentProgress;
			for (int i = 0; i <= progressDiff; ++i) {
				setProgress(currentProgress + i);
				try {
					Thread.sleep(ms_wait);
				} catch (InterruptedException ex) {
					LOGGER.error("Thread interrupted");
				}
			}
			currentProgress = _progress;
		}
	}

}
