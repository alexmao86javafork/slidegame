package com.github.ants280.slidegame.ui;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.Timer;

import com.github.ants280.slidegame.logic.MoveDirection;

public class SlideGameLabelManager {
	private final JLabel scoreLabel;
	private final JLabel highScoreLabel;
	private final JLabel moveLabel;
	private final JLabel goalLabel;
	private final JLabel gameOverLabel;
	private final Timer moveLabelClearingTimer;
	private final JLabel timerLabel;

	public SlideGameLabelManager(JLabel scoreLabel, JLabel highScoreLabel, JLabel moveLabel, JLabel goalLabel, JLabel gameOverLabel, JLabel timerLabel) {
		this.scoreLabel = scoreLabel;
		this.highScoreLabel = highScoreLabel;
		this.moveLabel = moveLabel;
		this.goalLabel = goalLabel;
		this.gameOverLabel = gameOverLabel;
		this.timerLabel=timerLabel;

		this.moveLabelClearingTimer = new Timer((int) TimeUnit.MILLISECONDS.convert(1, TimeUnit.SECONDS), actiovEvent -> this.clearMoveLabel());
		moveLabelClearingTimer.setRepeats(false);
	}

	public void updateScoreLabels(boolean gameOver, boolean gameWon, int score, int highScore) {
		String gameOverLabelText = "";
		if (gameOver) {
			gameOverLabelText = gameWon ? "You Win:)" : "You Lose:(";
		}
		if(gameWon) {
			gameOverLabel.setForeground(Color.GREEN);
		}
		else {
			gameOverLabel.setForeground(Color.RED);
		}
		
		gameOverLabel.setText(gameOverLabelText);
		scoreLabel.setText("" + score);
		highScoreLabel.setText("" + highScore);
	}

	public void updateGoalLabel(int goalTileValue) {
		goalLabel.setText(String.format("Goal: Create %d tile", goalTileValue));
	}

	public void updateMoveLabel(MoveDirection moveDirection, boolean validMove) {
		if (moveDirection != null) {
			moveLabel.setForeground(validMove ? Color.BLACK : Color.RED);
			moveLabel.setText(String.format(validMove ? "Moved %s" : "Cannot move %s", moveDirection.getDisplayValue()));
			moveLabelClearingTimer.stop();
			moveLabelClearingTimer.start();
		}
	}

	public void clearMoveLabel() {
		moveLabelClearingTimer.stop();
		moveLabel.setText("");
	}

	public void updateTimerLabel(String txt, Color c) {
		timerLabel.setText(txt);
		timerLabel.setForeground(c);
	}
	
	public void updateGameOverLabel(String txt, Color c) {
		gameOverLabel.setText(txt);
		gameOverLabel.setForeground(c);
	}
}
