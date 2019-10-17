package com.github.ants280.slidegame.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import com.github.ants280.slidegame.logic.Grid;

public class SlideGameFrame {
	private final JFrame frame;
	private final SlideGameManager slideGameManager;
	private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(2, 10, 2, 10); // top, left, bottom,
																								// right
	private static final Border LINE_BORDER = BorderFactory.createCompoundBorder(EMPTY_BORDER, BorderFactory.createLineBorder(SlideGameColors.SPACER_COLOR, 1, // width
			true)); // rounded

	public SlideGameFrame() {
		this.frame = new JFrame("2048 Puzzle Game");

		Grid grid = new Grid();
		JComponent slideGameDisplayComponent = new SlideGameDisplayComponent(grid).getComponent();

		JLabel gameOverLabel = this.createBigFontJLabel(false);

		JLabel scoreLabel = this.createBigFontJLabel(true);

		JLabel highScoreLabel = this.createBigFontJLabel(true);

		JLabel goalLabel = this.createJLabel(false);
		JLabel moveLabel = this.createJLabel(false);
		
		JLabel timerLabel = this.createBigFontJLabel(true);

		this.slideGameManager = new SlideGameManager(grid, frame, slideGameDisplayComponent, new SlideGameLabelManager(scoreLabel, highScoreLabel, moveLabel, goalLabel, gameOverLabel, timerLabel));

		this.doLayout(slideGameDisplayComponent, gameOverLabel, scoreLabel, highScoreLabel, goalLabel, moveLabel, timerLabel);
	}

	private void doLayout(JComponent slideGameDisplayComponent, JLabel gameOverLabel, JLabel scoreLabel, JLabel highScoreLabel, JLabel goalLabel, JLabel moveLabel, JLabel timerLabel) {
		JPanel scorePanel = new JPanel();
		scorePanel.setBorder(EMPTY_BORDER);
		scorePanel.setLayout(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.gridx = 0;
		constraints.gridy = 0;

		scorePanel.add(gameOverLabel, constraints);
		constraints.gridy = constraints.gridy + 1;
		
		JLabel scoreTitleLabel=this.createBigFontJLabel(false);
		scoreTitleLabel.setText("Your Score");
		scorePanel.add(scoreTitleLabel, constraints);
		constraints.gridy = constraints.gridy + 1;

		scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		scorePanel.add(scoreLabel, constraints);
		constraints.gridy = constraints.gridy + 1;
		
		JLabel highScoreTitleLabel=this.createBigFontJLabel(false);
		highScoreTitleLabel.setText("Best");
		scorePanel.add(highScoreTitleLabel, constraints);
		constraints.gridy = constraints.gridy + 1;

		highScoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		scorePanel.add(highScoreLabel, constraints);
		constraints.gridy = constraints.gridy + 1;
		
		JLabel timerTitleLabel=this.createBigFontJLabel(false);
		timerTitleLabel.setText("Countdown");
		scorePanel.add(timerTitleLabel, constraints);
		constraints.gridy = constraints.gridy + 1;
		
		timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		timerLabel.setText("00:00");
		scorePanel.add(timerLabel, constraints);
		constraints.gridy = constraints.gridy + 1;

		JPanel momentPanel = new JPanel();
		momentPanel.setLayout(new BoxLayout(momentPanel, BoxLayout.Y_AXIS));
		momentPanel.add(new JLabel("2048 puzzle, press N to new game"));
		momentPanel.add(goalLabel);
		momentPanel.add(Box.createGlue());
		momentPanel.add(moveLabel);
		slideGameDisplayComponent.setBorder(EMPTY_BORDER);

		//frame.setJMenuBar(createJMenuBar());
		frame.add(scorePanel, BorderLayout.WEST);
		frame.add(momentPanel, BorderLayout.EAST);
		frame.add(slideGameDisplayComponent);

		frame.setMinimumSize(new Dimension(400, 447));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
	}

	public JFrame getWindow() {
		return frame;
	}

	private JLabel createJLabel(boolean addBorder) {
		JLabel label = new JLabel();
		label.setBorder(addBorder ? LINE_BORDER : EMPTY_BORDER);
		return label;
	}
	
	private JLabel createBigFontJLabel(boolean addBorder) {
		JLabel label = new JLabel();
		label.setFont( new Font("Arial", Font.BOLD, 48));
		label.setBorder(addBorder ? LINE_BORDER : EMPTY_BORDER);
		return label;
	}

	private JMenuBar createJMenuBar() {
		JMenuItem setGridLengthMenuItem = new JMenuItem("Set grid length...");
		setGridLengthMenuItem.addActionListener(actionEvent -> this.showSetGridLengthPopup());

		JMenuItem setGoalTileValueMenuItem = new JMenuItem("Set goal tile value...");
		setGoalTileValueMenuItem.addActionListener(actionEvent -> this.showSetGoalTileValuePopup());

		JMenuItem newGameMenuItem = new JMenuItem("New Game", KeyEvent.VK_N);
		newGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		newGameMenuItem.addActionListener(actionEvent -> slideGameManager.newGame());

		JMenuItem exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
		exitMenuItem.addActionListener(actionEvent -> Runtime.getRuntime().exit(0));

		JMenuItem helpMenuItem = new JMenuItem("Help...", KeyEvent.VK_H);
		helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK));
		helpMenuItem.addActionListener(actionEvent -> this.showHelpPopup());

		JMenuItem aboutMenuItem = new JMenuItem("About...", KeyEvent.VK_F1);
		aboutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_DOWN_MASK));
		aboutMenuItem.addActionListener(actionEvent -> this.showAboutPopup());

		JMenu actionMenu = new JMenu("Action");
		actionMenu.add(setGridLengthMenuItem);
		actionMenu.add(setGoalTileValueMenuItem);
		actionMenu.addSeparator();
		actionMenu.add(newGameMenuItem);
		actionMenu.add(exitMenuItem);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(helpMenuItem);
		helpMenu.add(aboutMenuItem);

		JMenuBar mainMenu = new JMenuBar();
		mainMenu.add(actionMenu);
		mainMenu.add(helpMenu);

		return mainMenu;
	}

	private void showAboutPopup() {
		String message = "By: Jacob Patterson" + "\n" + "\nCopyright(©) 2018" + "\n" + "\nBased on the game by Gabriele Cirulli.";
		String title = "About " + frame.getTitle();
		this.showPopup(message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	private void showHelpPopup() {
		String message = "Slide the tiles all to the top, left, bottom, and right of " + "the grid to\ncombine tiles of equal values. When tiles " + "combine, the combined tile's value\nis their sum of values." + " Combine tiles by typing the appropriate arrow key,\nusing " + "WASD controls, or clicking and dragging in the desired " + "direction on the\ngrid. After each turn a new tile is " + "added. The game is over if the grid is\nfull and no moves " + "can be made. It is won when a tile of the goal value is\n" + "created.";
		String title = "Help for " + frame.getTitle();
		this.showPopup(message, title, JOptionPane.QUESTION_MESSAGE);
	}

	private void showPopup(String message, String title, int messageType) {
		JOptionPane.showMessageDialog(frame, message, title, messageType);
	}

	private void showSetGridLengthPopup() {
		String message = "Set grid length";
		int goalTileValue = slideGameManager.getGoalTileValue();
		int minimumGridLength = (int) Math.ceil(Math.sqrt(Math.log(goalTileValue) / Math.log(2d) - 1));
		Object[] selectionValues = IntStream.range(minimumGridLength, minimumGridLength + 10).boxed().toArray();
		int initialSelectionValue = slideGameManager.getGridLength();
		this.showOptionDialog(message, selectionValues, initialSelectionValue, slideGameManager::setGridLength);
	}

	private void showSetGoalTileValuePopup() {
		String message = "Set goal tile value";
		int gridLength = slideGameManager.getGridLength();
		int maximumGoalTileValue = (int) Math.pow(2, Math.pow(gridLength, 2));
		Object[] selectionValues = IntStream.range(0, 10).map(i -> (int) Math.pow(2d, i + 3d)).filter(possibleGoalTileValue -> possibleGoalTileValue <= maximumGoalTileValue).boxed().toArray();
		int initialSelectionValue = slideGameManager.getGoalTileValue();
		this.showOptionDialog(message, selectionValues, initialSelectionValue, slideGameManager::setGoalTileValue);
	}

	private void showOptionDialog(String message, Object[] selectionValues, Object initialSelectionValue, IntConsumer setValueFunction) {
		Object optionChoice = JOptionPane.showInputDialog(frame, message, "Change field for " + frame.getTitle(), JOptionPane.QUESTION_MESSAGE, null, selectionValues, initialSelectionValue);
		if (optionChoice != null) {
			setValueFunction.accept(Integer.parseInt(optionChoice.toString()));
		}
	}
}
