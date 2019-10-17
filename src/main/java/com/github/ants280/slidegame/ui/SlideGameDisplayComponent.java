package com.github.ants280.slidegame.ui;

import com.github.ants280.slidegame.logic.Grid;
import com.github.ants280.slidegame.logic.Tile;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

public class SlideGameDisplayComponent {
	private final Grid grid;
	private final JComponent component;
	private static final RenderingHints ANTIALIAS_ON_RENDERING_HINT = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	private static final double SPACER_PERCENTAGE = 0.10d;
	private int xOffset;
	private int yOffset;
	private int cellSize;
	private int tileSize;
	private int spacerSize;

	public SlideGameDisplayComponent(Grid grid) {
		super();

		this.grid = grid;
		this.component = new SlideGameDisplayComponentImpl();

		this.init();
	}

	public JComponent getComponent() {
		return component;
	}

	private void init() {
		component.setFont(new Font("times", Font.PLAIN, 12));
		component.addComponentListener(new SlideGameComponentListener(componentEvent -> this.componentResized()));
	}

	private void componentResized() {
		int width = component.getWidth();
		int height = component.getHeight();
		int minDimension = Math.min(width, height);
		xOffset = (width - minDimension) / 2;
		yOffset = (height - minDimension) / 2;

		int newCellSize = round(minDimension / (grid.getLength() + SPACER_PERCENTAGE));
		if (cellSize != newCellSize) {
			cellSize = newCellSize;
			spacerSize = round(cellSize * SPACER_PERCENTAGE);
			tileSize = cellSize - spacerSize;

			component.setFont(component.getFont().deriveFont((float) (cellSize / 3d)));
			component.repaint();
		}
	}

	private class SlideGameDisplayComponentImpl extends JComponent {
		private static final long serialVersionUID = 1L;

		// <editor-fold defaultstate="collapsed" desc="painting">
		@Override
		public void paintComponent(Graphics g) {
			((Graphics2D) g).setRenderingHints(ANTIALIAS_ON_RENDERING_HINT);

			g.translate(xOffset, yOffset);
			this.paintGrid(g);
			this.paintTiles(g);
		}

		private void paintGrid(Graphics g) {
			g.setColor(SlideGameColors.SPACER_COLOR);

			int gridLengthPx = grid.getLength() * cellSize + spacerSize;
			for (int i = 0; i <= grid.getLength(); i++) {
				int lineOffset = i * cellSize;

				// vertical lines:
				g.fillRect(lineOffset, 0, spacerSize, gridLengthPx);

				// horizontal lines:
				g.fillRect(0, lineOffset, gridLengthPx, spacerSize);
			}
		}

		private void paintTiles(Graphics g) {
			for (int c = 0; c < grid.getLength(); c++) {
				for (int r = 0; r < grid.getLength(); r++) {
					this.paintTile(g, c, r);
				}
			}
		}

		private void paintTile(Graphics g, int c, int r) {
			Tile tile = grid.getTile(c, r);

			this.paintTileBackground(g, c, r, tile);

			if (tile != null) {
				this.paintTileText(g, c, r, tile);
			}
		}

		private void paintTileBackground(Graphics g, int c, int r, Tile tile) {
			Color tileColor = tile == null ? SlideGameColors.EMPTY_TILE_COLOR : SlideGameColors.getColor(tile);
			g.setColor(tileColor);

			int x = c * cellSize + spacerSize;
			int y = r * cellSize + spacerSize;
			g.fillRect(x, y, tileSize, tileSize);
		}

		private void paintTileText(Graphics g, int c, int r, Tile tile) {
			g.setColor(SlideGameColors.TILE_TEXT_COLOR);

			int textWidth = g.getFontMetrics().stringWidth(tile.getDisplayValue());
			double fontHeight = g.getFont().getSize2D() * 0.75d;
			int x = round((c + 0.5d) * cellSize + (spacerSize - textWidth) / 2d);
			int y = round((r + 0.5d) * cellSize + (spacerSize + fontHeight) / 2d);
			g.drawString(tile.getDisplayValue(), x, y);
		}

		// </editor-fold>
	}

	private static int round(double value) {
		return (int) (value + 0.5d);
	}
}
