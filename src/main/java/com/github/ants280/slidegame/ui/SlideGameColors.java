package com.github.ants280.slidegame.ui;

import com.github.ants280.slidegame.logic.Tile;
import java.awt.Color;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Tile colors copied from .tile {.tile.tile-[//d+] .tile-inner background }
 *
 * See https://github.com/gabrielecirulli/2048/blob/master/style/main.css
 */
public class SlideGameColors {
	public static final Color EMPTY_TILE_COLOR = new Color(0xCDC1B4);
	public static final Color TILE_TEXT_COLOR = new Color(0x776E65);
	public static final Color SPACER_COLOR = new Color(0xBBADA0);
	private static final NavigableMap<Tile, Color> COLOR_CACHE = new TreeMap<>();

	static {
		int[] tileColors = new int[] { 0xeee4da, // 2
				0xede0c8, // 4
				0xf2b179, // 8
				0xf59563, // 16
				0xf67c5f, // 32
				0xf65e3b, // 64
				0xedcf72, // 128
				0xedcc61, // 256
				0xedc850, // 512
				0xedc53f, // 1024
				0xedc22e, // 2048
		};

		Tile tile = Tile.TWO;

		for (int tileColor : tileColors) {
			COLOR_CACHE.put(tile, new Color(tileColor));
			tile = tile.getNext();
		}
	}

	private SlideGameColors() {
	}

	public static Color getColor(Tile tile) {
		if (!COLOR_CACHE.containsKey(tile)) {
			return COLOR_CACHE.lastEntry().getValue();
		}

		return COLOR_CACHE.get(tile);
	}
}
