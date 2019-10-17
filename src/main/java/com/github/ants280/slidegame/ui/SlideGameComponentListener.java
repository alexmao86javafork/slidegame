package com.github.ants280.slidegame.ui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.function.Consumer;

public class SlideGameComponentListener extends ComponentAdapter implements ComponentListener {
	private final Consumer<ComponentEvent> componentResizedConsumer;

	public SlideGameComponentListener(Consumer<ComponentEvent> componentResizedConsumer) {
		this.componentResizedConsumer = componentResizedConsumer;
	}

	@Override
	public void componentResized(ComponentEvent e) {
		componentResizedConsumer.accept(e);
	}
}
