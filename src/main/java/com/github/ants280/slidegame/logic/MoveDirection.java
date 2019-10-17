package com.github.ants280.slidegame.logic;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public enum MoveDirection {
	LEFT("Left", Integer.signum(-1), 0, KeyEvent.VK_A, KeyEvent.VK_LEFT), RIGHT("Right", Integer.signum(1), 0, KeyEvent.VK_D, KeyEvent.VK_RIGHT), UP("Up", 0, Integer.signum(-1), KeyEvent.VK_W, KeyEvent.VK_UP), DOWN("Down", 0, Integer.signum(1), KeyEvent.VK_S, KeyEvent.VK_DOWN);

	private final String displayValue;
	private final int dx;
	private final int dy;
	private final int[] keyCodes;
	private static final Map<Integer, MoveDirection> KEY_CODES = Arrays.stream(MoveDirection.values()).flatMap(moveDirection -> Arrays.stream(moveDirection.keyCodes).mapToObj(keyCode -> new SimpleEntry<>(moveDirection, keyCode)))
			// throws exception if duplicate keys (Entry::Value) exist:
			.collect(Collectors.toMap(Entry::getValue, Entry::getKey));

	MoveDirection(String displayValue, int dx, int dy, int... keyCodes) {
		this.displayValue = displayValue;
		this.dx = dx;
		this.dy = dy;
		this.keyCodes = keyCodes;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public int getDx() {
		return dx;
	}

	public int getDy() {
		return dy;
	}

	// package-private for testing
	int[] getKeyCodes() {
		return Arrays.copyOf(keyCodes, keyCodes.length);
	}

	public static MoveDirection fromKeyEvent(KeyEvent keyEvent) {
		return KEY_CODES.get(keyEvent.getKeyCode());
	}

	public static MoveDirection fromMouseEvents(MouseEvent startMouseEvent, MouseEvent endMouseEvent) {
		int deltaX = endMouseEvent.getX() - startMouseEvent.getX();
		int deltaY = endMouseEvent.getY() - startMouseEvent.getY();
		int absDeltaX = Math.abs(deltaX);
		int absDeltaY = Math.abs(deltaY);
		int signumX = Integer.signum(deltaX);
		int signumY = Integer.signum(deltaY);

		// Only care about the sign of the axis with a larger value:
		if (absDeltaX > absDeltaY) {
			signumY = 0;
		} else if (absDeltaX < absDeltaY) {
			signumX = 0;
		}

		for (MoveDirection moveDirection : MoveDirection.values()) {
			if (signumX == moveDirection.getDx() && signumY == moveDirection.getDy()) {
				return moveDirection;
			}
		}

		return null;
	}
}
