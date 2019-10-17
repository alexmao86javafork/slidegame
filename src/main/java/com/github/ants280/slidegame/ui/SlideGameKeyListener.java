package com.github.ants280.slidegame.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

public class SlideGameKeyListener extends KeyAdapter implements KeyListener {
	private final Consumer<KeyEvent> keyReleasedConsumer;

	public SlideGameKeyListener(Consumer<KeyEvent> keyReleasedConsumer) {
		this.keyReleasedConsumer = keyReleasedConsumer;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyReleasedConsumer.accept(e);
	}
}
