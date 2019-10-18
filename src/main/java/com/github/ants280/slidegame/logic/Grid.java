package com.github.ants280.slidegame.logic;

import java.util.Random;

public class Grid {
	private final Random random;
	private int length;
	private Tile[][] cols;
	private Tile[][] rows;
	private int goalTileValue;
	private boolean goalTileCreated;

	public Grid() {
		this(4);
	}

	public Grid(int length) {
		this(length, 1024);
	}

	/**
	 * Creates an empty, square grid of tiles.
	 *
	 * @param length
	 *            The width and height of columns and rows in the grid.
	 * @param goalTileValue
	 *            The value of the Tile used to determine when the game is over.
	 */
	public Grid(int length, int goalTileValue) {
		this.random = new Random();
		this.length = length;
		this.cols = createTiles(length);
		this.rows = createTiles(length);
		this.goalTileValue = goalTileValue;
		this.goalTileCreated = false;

		this.validateLength(length);
		this.validateGoalTileValue(goalTileValue);
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.validateLength(length);
		this.length = length;
		this.cols = createTiles(length);
		this.rows = createTiles(length);
		this.clear();
	}

	public int getGoalTileValue() {
		return goalTileValue;
	}

	public void setGoalTileValue(int goalTileValue) {
		this.validateGoalTileValue(goalTileValue);
		this.goalTileValue = goalTileValue;
		this.clear();
	}

	private void validateLength(int length) {
		if (length < 2) {
			throw new IllegalArgumentException("Length must be large enough " + "to slide tiles.  Found: " + length);
		}

		int minimumLength = (int) Math.ceil(Math.sqrt(Math.log(goalTileValue) / Math.log(2) - 1));

		if (length < minimumLength) {
			throw new IllegalArgumentException(String.format("Length of %d is too small to win with " + "a goalTileValue of %d.  Must be at least %d", length, goalTileValue, minimumLength));
		}
	}

	private void validateGoalTileValue(int goalTileValue) {
		if (goalTileValue < 8 || (goalTileValue & (goalTileValue - 1)) != 0) {
			throw new IllegalArgumentException("Goal tile value must be a value of 2");
		}

		int maximumGoalTileValue = (int) Math.pow(2, Math.pow(length, 2));

		if (goalTileValue > maximumGoalTileValue) {
			throw new IllegalArgumentException(String.format("Goal tile of value of %d is too large to win with " + "a grid length of %d.  Must be at least %d", goalTileValue, length, maximumGoalTileValue));
		}
	}

	public Tile getTile(int c, int r) {
		return rows[r][c];
	}

	public void clear() {
		for (int r = 0; r < length; r++) {
			for (int c = 0; c < length; c++) {
				this.setTile(c, r, null);
			}
		}

		goalTileCreated = false;
	}

	/**
	 * Add a random tile to an empty spot on the grid.
	 *
	 * This may be slow with fairly-filled game boards due to random adding.
	 */
	public void addRandomTile() {
		if (this.isFilled()) {
			throw new IllegalArgumentException("Cannot add random tile");
		}

		int c;
		int r;

		Tile tile = random.nextInt(10) == 0 ? Tile.TWO.getNext() : Tile.TWO;
		do {
			c = random.nextInt(length);
			r = random.nextInt(length);
		} while (this.getTile(c, r) != null);

		this.setTile(c, r, tile);
	}

	public int slideTiles(MoveDirection moveDirection) {
		return this.slideTiles(moveDirection == MoveDirection.UP || moveDirection == MoveDirection.DOWN, moveDirection == MoveDirection.LEFT || moveDirection == MoveDirection.UP);
	}

	public boolean canSlideTiles(MoveDirection moveDirection) {
		return this.canSlideTiles(moveDirection == MoveDirection.UP || moveDirection == MoveDirection.DOWN, moveDirection == MoveDirection.LEFT || moveDirection == MoveDirection.UP);
	}

	public boolean canSlideInAnyDirection() {
		for (MoveDirection moveDirection : MoveDirection.values()) {
			if (this.canSlideTiles(moveDirection)) {
				return true;
			}
		}

		return false;
	}

	public boolean isFilled() {
		for (int r = 0; r < length; r++) {
			for (int c = 0; c < length; c++) {
				if (this.getTile(c, r) == null) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean goalTileCreated() {
		return goalTileCreated;
	}

	private boolean canSlideTiles(boolean slideColumns, boolean towardZero) {
		for (int i = 0; i < length; i++) {
			if (this.canSlideTiles(slideColumns, i, towardZero)) {
				return true;
			}
		}

		return false;
	}

	private int slideTiles(boolean slideColumns, boolean towardZero) {
		int sum = 0;

		for (int i = 0; i < length; i++) {
			sum += this.slideTiles(slideColumns, i, towardZero);
		}

		return sum;
	}

	/**
	 * Determines if the row or column can be slid in the specified direction. That
	 * is, if sliding the row/column moves/changes the tiles at all.
	 *
	 * @param slideColumns
	 *            Whether or not a column or row is being slid (consolidated).
	 * @param index
	 *            The column or row index.
	 * @param towardZero
	 *            Whether or not to slide the row/column up/left (toward zero) or
	 *            down/right (toward the end of the array).
	 * @return The if the row/column can be slid in the specified direction.
	 */
	private boolean canSlideTiles(boolean slideColumns, int index, boolean towardZero) {
		Tile lastSlidTile = null;

		Tile[] tilesToSlide = this.getTilesToSlide(slideColumns, index);
		int slideIndex = this.getSlideIndex(towardZero);
		int slideDirectionDelta = this.getSlideDirectionDelta(towardZero);
		for (int i = slideIndex; towardZero ? i < length : i >= 0; i += slideDirectionDelta) {
			if (tilesToSlide[i] != null) {
				if (lastSlidTile == tilesToSlide[i] || i != slideIndex) {
					return true;
				} else {
					lastSlidTile = tilesToSlide[i];
					slideIndex += slideDirectionDelta;
				}
			}
		}

		return false;
	}

	/**
	 * Slide the tiles at the specified index in the specified direction.
	 *
	 * @param slideColumns
	 *            Whether or not a column or row is being slid (consolidated).
	 * @param index
	 *            The row or column index.
	 * @param towardZero
	 *            Whether or not to slide the row/column up/left (toward zero) or
	 *            down/right (toward the end of the array).
	 * @return The sum of the newly created, combined tiles.
	 */
	private int slideTiles(boolean slideColumns, int index, boolean towardZero) {
		int sum = 0;
		Tile[] slidTiles = new Tile[length];
		boolean canCombineWithPreviousSlide = false;

		Tile[] tilesToSlide = this.getTilesToSlide(slideColumns, index);
		int slideIndex = this.getSlideIndex(towardZero);
		int slideDirectionDelta = this.getSlideDirectionDelta(towardZero);
		for (int i = slideIndex; towardZero ? i < length : i >= 0; i += slideDirectionDelta) {
			if (tilesToSlide[i] != null) {
				if (canCombineWithPreviousSlide && slidTiles[slideIndex - slideDirectionDelta] == tilesToSlide[i]) {
					Tile nextTile = tilesToSlide[i].getNext();
					slidTiles[slideIndex - slideDirectionDelta] = nextTile;
					sum += nextTile.getValue();
					canCombineWithPreviousSlide = false;
					if (nextTile.getValue() == goalTileValue) {
						goalTileCreated = true;
					}
				} else {
					slidTiles[slideIndex] = tilesToSlide[i];
					slideIndex += slideDirectionDelta;
					canCombineWithPreviousSlide = true;
				}
			}
		}

		this.setTiles(slideColumns, index, slidTiles);

		return sum;
	}

	private Tile[] getTilesToSlide(boolean slideColumns, int index) {
		return slideColumns ? cols[index] : rows[index];
	}

	private int getSlideIndex(boolean towardZero) {
		return towardZero ? 0 : length - 1;
	}

	private int getSlideDirectionDelta(boolean towardZero) {
		return towardZero ? 1 : -1;
	}

	private void setTiles(boolean slideColumns, int index, Tile[] tempArray) {
		for (int i = 0; i < length; i++) {
			this.setTile(slideColumns ? index : i, slideColumns ? i : index, tempArray[i]);
		}
	}

	/**
	 * Set the tile at the specified location. Updates the both the rows and columns
	 * to keep the reflective data structure intact.
	 *
	 * Package-private for easy arrangements of tests.
	 *
	 * @param c
	 *            The column on the Grid (x-coordinate).
	 * @param r
	 *            The row on the Grid (y-coordinate).
	 * @param tile
	 *            The Tile to ad to the Grid.
	 */
	void setTile(int c, int r, Tile tile) {
		rows[r][c] = tile;
		cols[c][r] = tile;
	}

	private static Tile[][] createTiles(int length) {
		Tile[][] tiles = new Tile[length][];

		for (int i = 0; i < length; i++) {
			tiles[i] = new Tile[length];
		}

		return tiles;
	}
}
