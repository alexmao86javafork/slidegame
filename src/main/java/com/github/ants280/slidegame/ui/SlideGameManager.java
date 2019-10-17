package com.github.ants280.slidegame.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.github.ants280.slidegame.logic.Grid;
import com.github.ants280.slidegame.logic.MoveDirection;

public class SlideGameManager {
	private static final String COUNTDOWN2 = "countdown";
	private static final String HIGHESTSCORE = "highestscore";
	private final Grid grid;
	private final JFrame slideGameRootComponent;
	private final JComponent slideGameDisplayComponent;
	private final SlideGameLabelManager slideGameLabelManager;
	private final KeyListener keyListener;
	//private final MouseListener mouseListener;
	private int score;
	private int highScore;
	private boolean gameOver=true;
	private boolean gameWon;
	private boolean listenersAdded;
	private MouseEvent mousePressedLocation;

	private Timer timer;
	// 冻结屏幕
	private AtomicBoolean froozen = new AtomicBoolean(true);
	private AtomicInteger countdown=new AtomicInteger(0);

	private Properties store = new Properties();
	{
		// default values
		store.setProperty(HIGHESTSCORE, "0");
		store.setProperty(COUNTDOWN2, "120");
	}

	public SlideGameManager(Grid grid, JFrame slideGameRootComponent, JComponent slideGameDisplayComponent, SlideGameLabelManager slideGameLabelManager) {
		this.grid = grid;
		this.slideGameRootComponent = slideGameRootComponent;
		this.slideGameDisplayComponent = slideGameDisplayComponent;
		this.slideGameLabelManager = slideGameLabelManager;

		this.keyListener = new SlideGameKeyListener(this::keyReleased);
		//this.mouseListener = new SlideGameMouseListener(this::mousePressed, this::mouseReleased);
		this.score = 0;
		this.highScore = 0;
		this.gameOver = false;
		this.gameWon = false;
		this.listenersAdded = false;

		this.initGame();

		loadData();
	}

	private void loadData() {
		try {
			InputStream in = new FileInputStream(new File(System.getProperty("user.home") + File.separator + ".2048puzzle"));
			store.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveData() {
		try {
			OutputStream out = new FileOutputStream(new File(System.getProperty("user.home") + File.separator + ".2048puzzle"));
			store.store(out, new Date().toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getGridLength() {
		return grid.getLength();
	}

	public void setGridLength(int length) {
		grid.setLength(length);
		this.newGame();
	}

	public int getGoalTileValue() {
		return grid.getGoalTileValue();
	}

	public void setGoalTileValue(int goalTileValue) {
		grid.setGoalTileValue(goalTileValue);
		this.newGame();
		slideGameLabelManager.updateGoalLabel(grid.getGoalTileValue());
	}

	public void makeMove(MoveDirection moveDirection) {
		boolean validMove = moveDirection != null && grid.canSlideTiles(moveDirection);

		if (validMove) {

			int moveScore = grid.slideTiles(moveDirection);

			this.incrementScore(moveScore);

			if (!grid.canSlideInAnyDirection() || grid.goalTileCreated()) {
				gameWon = grid.goalTileCreated();
				this.endGame();
			} else {
				grid.addRandomTile();

				if (!grid.canSlideInAnyDirection()) {
					this.endGame();
				}
			}

			slideGameLabelManager.updateScoreLabels(gameOver, gameWon, score, highScore);
			if(gameOver&&timer!=null) {
				timer.cancel();
				saveData();
			}

			slideGameDisplayComponent.repaint();
		}

		slideGameLabelManager.updateMoveLabel(moveDirection, validMove);
	}

	private void initGame() {
		gameOver = false;
		gameWon = false;
		score = 0;
		grid.addRandomTile();
		grid.addRandomTile();
		this.addListeners();
		slideGameLabelManager.updateScoreLabels(gameOver, gameWon, score, highScore);
		slideGameLabelManager.updateGoalLabel(grid.getGoalTileValue());
		slideGameLabelManager.clearMoveLabel();
	}

	public void newGame() {
		grid.clear();
		this.initGame();
		slideGameDisplayComponent.repaint();
		this.saveData();
		this.resetTimer();
	}

	private void resetTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		countdown.set(Integer.parseInt(store.getProperty(COUNTDOWN2, "120")));
		froozen.set(true);
		slideGameLabelManager.updateTimerLabel("0", Color.BLACK);
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				for (int i = 1; i < 4; i++) {
					final int iref=i;
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							slideGameLabelManager.updateTimerLabel(""+iref, Color.BLUE);
						}
					});
					saveData();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				froozen.set(false);
			}
		}, 1000);
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				int rest=countdown.get();
				Color color=(rest<10?Color.RED:Color.BLUE);
				slideGameLabelManager.updateTimerLabel(""+rest+" Sec", color);
				
				if(rest==0) {
					froozen.set(true);
					slideGameLabelManager.updateScoreLabels(gameOver, gameWon, score, highScore);
					slideGameLabelManager.updateGameOverLabel("Time up", Color.RED);
					saveData();
					cancel();
				}
				
				countdown.decrementAndGet();
			}
		}, 3000, 1000);
	}

	private void endGame() {
		gameOver = true;
		this.froozen.set(true);
		//this.removeListeners();
	}

	private void addListeners() {
		if (!listenersAdded) {
			slideGameRootComponent.addKeyListener(keyListener);
			//slideGameDisplayComponent.addMouseListener(mouseListener);//TODO remove mouse listener
			mousePressedLocation = null;
			listenersAdded = true;
		}
	}

	/*private void removeListeners() {
		slideGameRootComponent.removeKeyListener(keyListener);
		//slideGameDisplayComponent.removeMouseListener(mouseListener);
		mousePressedLocation = null;
		listenersAdded = false;
	}*/

	private void incrementScore(int additionalScore) {
		if (additionalScore != 0) {
			this.score += additionalScore;
			if (score > highScore) {
				highScore = score;
				store.setProperty(HIGHESTSCORE, "" + highScore);
				saveData();
			}
		}
	}

	private void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_N) {
			this.newGame();
			return;
		}

		if(froozen.get()) {
			/*JOptionPane.showMessageDialog(slideGameRootComponent,
					  "Please press N to start new game",
					  "INFO",
					  JOptionPane.WARNING_MESSAGE);*/
			return ;
		}
		
		this.makeMove(MoveDirection.fromKeyEvent(e));
	}
/*
	private void mousePressed(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) {
			return;
		}

		mousePressedLocation = e;
	}

	private void mouseReleased(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1 || mousePressedLocation == null) {
			return;
		}

		this.makeMove(MoveDirection.fromMouseEvents(mousePressedLocation, e));
	}
	*/
}
