package com.github.ants280.slidegame.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

public class SlideGameMouseListener extends MouseAdapter implements MouseListener {
	private final Consumer<MouseEvent> mousePressedConsumer;
	private final Consumer<MouseEvent> mouseReleasedConsumer;

	public SlideGameMouseListener(Consumer<MouseEvent> mousePressedConsumer, Consumer<MouseEvent> mouseReleasedConsumer) {
		this.mousePressedConsumer = mousePressedConsumer;
		this.mouseReleasedConsumer = mouseReleasedConsumer;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePressedConsumer.accept(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseReleasedConsumer.accept(e);
	}
}
