package com.github.ants280.slidegame.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SlideGame implements Runnable {
	public static void main(final String[] args) {
		// Set the menu of the Frame on the mac menu.
		if (System.getProperty("mrj.version") != null) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}

		SwingUtilities.invokeLater(new SlideGame());
	}

	private SlideGame() {
	}

	@Override
	public void run() {
		setLookAndFeel();

		JFrame frame = new SlideGameFrame().getWindow();

		Thread.setDefaultUncaughtExceptionHandler(new SlideGameUncaughtExceptionHandler(frame));
		frame.setSize(800, 600);
		// Center the Window on the screen.
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
			Logger.getLogger(SlideGame.class.getName()).log(Level.SEVERE, "Could not set system Look-And-Feel", ex);
		}
	}
}
