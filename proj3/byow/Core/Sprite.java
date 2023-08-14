package byow.Core;

// import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Sprite {

    private int x;
    private int y;
    private TETile[][] mainTiles;
    private TETile[][] hiddenTiles;
    private TETile tile;
    private boolean paused;
    private int headStart;
    private int score;
    private boolean isIt;
    private int movementSpeed;

    public Sprite(Random random, int worldWidth, int worldHeight, TETile[][] mainTiles, TETile[][] hiddenTiles,
                  TETile tile) {

        this.x = RandomUtils.uniform(random, worldWidth);
        this.y = RandomUtils.uniform(random, worldHeight);
        this.mainTiles = mainTiles;
        this.hiddenTiles = hiddenTiles;
        this.tile = tile;
        this.headStart = 0;
        this.score = 0;
        this.isIt = false;

        while (this.mainTiles[this.x][this.y] != Constants.FLOOR) {
            this.x = RandomUtils.uniform(random, worldWidth);
            this.y = RandomUtils.uniform(random, worldHeight);
        }

        this.paused = false;
        this.movementSpeed = 1;

    }

    /** Adds the sprite to the tiles array. */
    public void loadSprite() {
        this.mainTiles[this.x][this.y] = this.tile;
    }

    /** Moves the sprite left. */
    public void moveLeft() {
        if (!this.paused && this.mainTiles[this.x - this.movementSpeed][this.y] == Constants.FLOOR) {
            this.mainTiles[this.x][this.y] = Constants.FLOOR;
            this.mainTiles[this.x - this.movementSpeed][this.y] = this.tile;
            this.hiddenTiles[this.x][this.y] = Constants.FLOOR;
            this.hiddenTiles[this.x - this.movementSpeed][this.y] = this.tile;
            this.headStart++;
            this.x -= this.movementSpeed;
            incrementScore();
        } else if (this.movementSpeed == 2 && !this.paused
                && this.mainTiles[this.x - 1][this.y] == Tileset.FLOOR) {
            this.mainTiles[this.x][this.y] = Constants.FLOOR;
            this.mainTiles[this.x - 1][this.y] = this.tile;
            this.hiddenTiles[this.x][this.y] = Constants.FLOOR;
            this.hiddenTiles[this.x - 1][this.y] = this.tile;
            this.x--;
        }
    }

    /** Moves the sprite right. */
    public void moveRight() {
        if (!this.paused && this.mainTiles[this.x + this.movementSpeed][this.y] == Constants.FLOOR) {
            this.mainTiles[this.x][this.y] = Constants.FLOOR;
            this.mainTiles[this.x + this.movementSpeed][this.y] = this.tile;
            this.hiddenTiles[this.x][this.y] = Constants.EMPTY;
            this.hiddenTiles[this.x + this.movementSpeed][this.y] = this.tile;
            this.headStart++;
            this.x += this.movementSpeed;
            incrementScore();
        } else if (this.movementSpeed == 2 && !this.paused
                && this.mainTiles[this.x + 1][this.y] == Tileset.FLOOR) {
            this.mainTiles[this.x][this.y] = Constants.FLOOR;
            this.mainTiles[this.x + 1][this.y] = this.tile;
            this.hiddenTiles[this.x][this.y] = Constants.EMPTY;
            this.hiddenTiles[this.x + 1][this.y] = this.tile;
            this.x++;
        }
    }

    /** Moves the sprite up. */
    public void moveUp() {
        if (!this.paused && this.mainTiles[this.x][this.y + this.movementSpeed] == Constants.FLOOR) {
            this.mainTiles[this.x][this.y] = Constants.FLOOR;
            this.mainTiles[this.x][this.y + this.movementSpeed] = this.tile;
            this.hiddenTiles[this.x][this.y] = Constants.FLOOR;
            this.hiddenTiles[this.x][this.y + this.movementSpeed] = this.tile;
            this.headStart++;
            this.y += this.movementSpeed;
            incrementScore();
        } else if (this.movementSpeed == 2 && !this.paused
                && this.mainTiles[this.x][this.y + 1] == Tileset.FLOOR) {
            this.mainTiles[this.x][this.y] = Constants.FLOOR;
            this.mainTiles[this.x][this.y + 1] = this.tile;
            this.hiddenTiles[this.x][this.y] = Constants.FLOOR;
            this.hiddenTiles[this.x][this.y + 1] = this.tile;
            this.y++;
        }
    }

    /** Moves the sprite down. */
    public void moveDown() {
        if (!this.paused && this.mainTiles[this.x][this.y - this.movementSpeed] == Tileset.FLOOR) {
            this.mainTiles[this.x][this.y] = Constants.FLOOR;
            this.mainTiles[this.x][this.y - this.movementSpeed] = this.tile;
            this.hiddenTiles[this.x][this.y] = Constants.FLOOR;
            this.hiddenTiles[this.x][this.y - this.movementSpeed] = this.tile;
            this.headStart++;
            this.y -= this.movementSpeed;
            incrementScore();
        } else if (this.movementSpeed == 2 && !this.paused
                && this.mainTiles[this.x][this.y - 1] == Tileset.FLOOR) {
            this.mainTiles[this.x][this.y] = Constants.FLOOR;
            this.mainTiles[this.x][this.y - 1] = this.tile;
            this.hiddenTiles[this.x][this.y] = Constants.FLOOR;
            this.hiddenTiles[this.x][this.y - 1] = this.tile;
            this.y--;
        }
    }

    /** Increments the sprite's score after movement if not it. */
    private void incrementScore() {
        if (!this.isIt) {
            this.score++;
        }
    }

    /** Returns the x coordinate of the sprite. */
    public int x() {
        return this.x;
    }

    /** Returns the y coordinate of the sprite. */
    public int y() {
        return this.y;
    }

    /** Returns the coordinate location of the sprite. */
    public Coordinates getCoordinates() {
        return new Coordinates(this.x, this.y);
    }

    /** Freezes the sprite. */
    public void pause() {
        this.paused = true;
    }

    /** Unfreezes the sprite. */
    public void unPause() {
        this.paused = false;
    }

    /** Returns the headStart value of the sprite. */
    public int getHeadStart() {
        return this.headStart;
    }

    /** Resets the headStart value of the sprite. */
    public void resetHeadStart() {
        this.headStart = 0;
    }

    /** Sets isIt to true. */
    public void setIt() {
        this.isIt = true;
    }

    /** Sets isIt to false. */
    public void setNotIt() {
        this.isIt = false;
    }

    /** Returns the sprite's score. */
    public int getScore() {
        return this.score;
    }

    /** Switch between fast and regular movement speed. */
    public void toggleMovementSpeed() {
        this.movementSpeed = 3 - this.movementSpeed;
    }

    /** Set movementSpeed back to default. */
    public void setDefaultMovementSpeed() {
        this.movementSpeed = 1;
    }

}
