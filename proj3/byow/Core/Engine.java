package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.io.FileNotFoundException;
import java.io.FileWriter;

import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Character.toLowerCase;

public class Engine {

    TERenderer ter = new TERenderer();

    private String savedString;
    private TETile[][] tiles;
    private TETile[][] mainTiles;
    private TETile[][] hiddenTiles;
    private boolean isHidden;
    private Random random;
    private Sprite sprite0;
    private Sprite sprite1;
    private Sprite it;
    private Sprite notIt;

    public Engine() {
        this.savedString = "";
        this.tiles = new TETile[Constants.WIDTH][Constants.HEIGHT + Constants.HUD_HEIGHT];
        this.mainTiles = new TETile[Constants.WIDTH][Constants.HEIGHT + Constants.HUD_HEIGHT];
        this.hiddenTiles = new TETile[Constants.WIDTH][Constants.HEIGHT + Constants.HUD_HEIGHT];
        this.isHidden = false;
        this.random = new Random();
        this.sprite0 = null;
        this.sprite1 = null;
        this.it = null;
        this.notIt = null;
    }

    /** Saves and quits the game if user enters ":q". */
    private void checkForQuit() {
        String savedStringLower = this.savedString.toLowerCase();
        if (savedStringLower.length() >= 2
                && savedStringLower.substring(savedStringLower.length() - 2).equals(":q")) {
            System.out.println("save game called");
            saveGame();
            quitGame();
        }
    }

    /** Saves the game. */
    private void saveGame() {
        try {
            // Creating a file:
            // @Source: https://www.w3schools.com/java/java_files_create.asp
            // Naming a file:
            // @Source: https://unix.stackexchange.com/questions/28219/what-is-the-file-naming-convention
            // -for-regular-text-files
            // Accessing user home directory:
            // @Source: https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html
            File saveData = new File("save-data");
            if (saveData.createNewFile()) {
                System.out.println("File created: " + saveData.getName());
            } else {
                System.out.println("File already exists.");
            }
            // Writing to a file:
            // @Source: https://www.w3schools.com/java/java_files_create.asp
            FileWriter writer = new FileWriter(saveData, false);
            writer.write(this.savedString.substring(0, this.savedString.length() - 2));
            writer.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /** Quits the game. */
    private void quitGame() {
        // Terminating a program:
        // @Source: https://java2blog.com/how-to-end-program-java/#:~:text=You%20can%20use%20exit(),
        // and%20terminate%20the%20current%20process.
        System.exit(0);
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        mainMenu();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char newChar = formattedChar(StdDraw.nextKeyTyped());
                checkForQuit();
                if (newChar == 'n') {
                    userEntersSeed();
                    newWorld(false);
                    playGame();
                } else if (newChar == 'l') {
                    loadSavedString();
                    System.out.println(this.savedString);
                    String lowerSavedString = this.savedString.toLowerCase();
                    if (lowerSavedString.indexOf("n") < 0 || lowerSavedString.indexOf("s") < 0) {
                        this.savedString = "n";
                        userEntersSeed();
                        newWorld(false);
                        playGame();
                    } else {
                        parseSeed(lowerSavedString.substring(
                                lowerSavedString.indexOf('n') + 1, lowerSavedString.indexOf('s')
                        ));
                        newWorld(true);
                        parseMovementString(lowerSavedString.substring(lowerSavedString.indexOf('s') + 1));
                        renderWorld();
                        playGame();
                    }
                }
            }
        }
    }

    /** Handles all inputs when game is being played. */
    private void playGame() {
        while (true) {
            this.ter.renderFrame(this.tiles);
            displayHUD();
            if (StdDraw.hasNextKeyTyped()) {
                char movementChar = formattedChar(StdDraw.nextKeyTyped());
                checkForQuit();
                interactWithMovementKeyBoard(movementChar);
            }
            StdDraw.pause(Constants.DELAY);
        }
    }

    /** Checks whether a tag has occurred. */
    private boolean tagOccurred() {
        return this.notIt.getHeadStart() >= Constants.MAX_HEAD_START
                && Coordinates.distance(this.sprite0.getCoordinates(), this.sprite1.getCoordinates()) <= 1;
    }

    /** Changes which player is 'it'. */
    private void changeIt() {
        Sprite tempSprite = this.it;
        this.it = this.notIt;
        this.notIt = tempSprite;
        this.it.pause();
        this.it.setIt();
        this.notIt.resetHeadStart();
        this.notIt.setNotIt();
        this.notIt.setDefaultMovementSpeed();
    }

    /** Checks whether 'it' can be unfrozen. */
    private void checkForEndPause() {
        if (this.notIt.getHeadStart() >= Constants.MAX_HEAD_START) {
            this.it.unPause();
        }
    }

    /** Handles in game keyboard inputs. */
    private void interactWithMovementKeyBoard(char c) {
        checkForEndPause();
        if (c == 'a') {
            this.sprite0.moveLeft();
        } else if (c == 'w') {
            this.sprite0.moveUp();
        } else if (c == 's') {
            this.sprite0.moveDown();
        } else if (c == 'd') {
            this.sprite0.moveRight();
        } else if (c == 'j') {
            this.sprite1.moveLeft();
        } else if (c == 'i') {
            this.sprite1.moveUp();
        } else if (c == 'k') {
            this.sprite1.moveDown();
        } else if (c == 'l') {
            this.sprite1.moveRight();
        } else if (c == 'g') {
            hideTiles();
            updateHiddenTiles();
        } else if (c == ' ') {
            this.it.toggleMovementSpeed();
        }
        if (this.isHidden) {
            updateHiddenTiles();
        }
        if (tagOccurred()) {
            changeIt();
        }
    }

    /** Renders the tiles 2D array. */
    private void renderWorld() {
        this.ter.initialize(Constants.WIDTH, Constants.HEIGHT + Constants.HUD_HEIGHT);
        this.ter.renderFrame(this.tiles);
    }

    /** Adds the new char to the input string and returns its lower-cased version. */
    private char formattedChar(char c) {
        this.savedString += c;
        return toLowerCase(c);
    }

    /** Creates and loads the main menu. */
    private void mainMenu() {
        StdDraw.setCanvasSize(Constants.WIDTH * Constants.SCALE,
                (Constants.HEIGHT + Constants.HUD_HEIGHT) * Constants.SCALE);
        StdDraw.setXscale(0, Constants.WIDTH);
        StdDraw.setYscale(0, Constants.HEIGHT + Constants.HUD_HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        title();
        menu();
        displayRules();
    }

    /** Adds a title to the main menu. */
    private void title() {
        StdDraw.setFont(Constants.TITLE_FONT);
        StdDraw.text(Constants.WIDTH / 2, Constants.TITLE_OFFSET * (Constants.HEIGHT + Constants.HUD_HEIGHT),
                Constants.TITLE_TEXT);
    }

    /** Adds the menu options to the main menu. */
    private void menu() {
        String text = "New Game (N)Load Game (L)Quit (:Q)";
        StdDraw.setFont(Constants.TEXT_FONT);
        String text1 = text.substring(0, text.indexOf("N)") + 2);
        String text2 = text.substring(text.indexOf("N)") + 2, text.indexOf("L)") + 2);
        String text3 = text.substring(text.indexOf("L)") + 2);
        StdDraw.text(Constants.WIDTH / 2, ((Constants.HEIGHT + Constants.HUD_HEIGHT) / 2)
                + ((double) Constants.TEXT_FONT_SIZE / Constants.MENU_OFFSET)
                * (Constants.HEIGHT + Constants.HUD_HEIGHT), text1);
        StdDraw.text(Constants.WIDTH / 2, (Constants.HEIGHT + Constants.HUD_HEIGHT) / 2, text2);
        StdDraw.text(Constants.WIDTH / 2, ((Constants.HEIGHT + Constants.HUD_HEIGHT) / 2)
                - ((double) Constants.TEXT_FONT_SIZE / Constants.MENU_OFFSET)
                * (Constants.HEIGHT + Constants.HUD_HEIGHT), text3);
    }

    /** Displays the rules. */
    private void displayRules() {
        String rules0 = "Rules:";
        String rules1 = "The goal of the game is to stay \"not it\" for as long as possible. Player 1 moves with awsd";
        String rules2 = "keys, and Player 2 moves with jikl keys. Everytime a \"tag\" occurs, the new \"it\" will not";
        String rules3 = "be able to move until the other player has moved " + Constants.MAX_HEAD_START + " steps. The"
                + " player who is \"it\" can toggle";
        String rules4 = "between faster and slower movement speeds by pressing space. Press 'g' for hard mode.";
        StdDraw.text(Constants.RULES_OFFSET_X, Constants.RULES_OFFSET + 4, rules0);
        StdDraw.text(Constants.RULES_OFFSET_X, Constants.RULES_OFFSET, rules1);
        StdDraw.text(Constants.RULES_OFFSET_X, Constants.RULES_OFFSET - 2, rules2);
        StdDraw.text(Constants.RULES_OFFSET_X, Constants.RULES_OFFSET - 4, rules3);
        StdDraw.text(Constants.RULES_OFFSET_X, Constants.RULES_OFFSET - 6, rules4);
    }

    /** Displays the screen the user will be prompted to type the seed in as well as the seed as it is being typed. */
    private void displaySeed(String seed) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.setFont(Constants.TITLE_FONT);
        StdDraw.text(Constants.WIDTH / 2, Constants.TITLE_OFFSET
                * (Constants.HEIGHT + Constants.HUD_HEIGHT), "Enter World Seed");
        StdDraw.setFont(Constants.TEXT_FONT);
        StdDraw.text(Constants.WIDTH / 2, Constants.TEXT_OFFSET * (Constants.HEIGHT + Constants.HUD_HEIGHT),
                "(Press 's' when you are finished entering the seed.)");

        StdDraw.setFont(Constants.SEED_FONT);
        StdDraw.text(Constants.WIDTH / 2, (Constants.HEIGHT + Constants.HUD_HEIGHT) / 2, seed);
    }

    /** Takes in a seed from user input. */
    private void userEntersSeed() {
        String seedString = "";
        displaySeed(seedString);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                // Changing a character to lowercase:
                // @Source: https://www.tutorialspoint.com/java/lang/character_tolowercase.htm
                char nextChar = formattedChar(StdDraw.nextKeyTyped());
                checkForQuit();
                if (nextChar == 's') {
                    break;
                } else if (Constants.INTEGERS.indexOf(nextChar) < 0) {
                    continue;
                }
                seedString += nextChar;
                displaySeed(seedString);
            }
        }
        System.out.println(seedString);
        if (seedString.equals("")) {
            this.random = new Random(0);
        } else {
            this.random = new Random(Long.parseLong(seedString));
        }
    }

    /** Creates and renders the new world for the first time. */
    private void newWorld(boolean loading) {
        this.mainTiles = (new World(Constants.WIDTH, Constants.HEIGHT, random)).getTiles();
        this.sprite0 = new Sprite(this.random, Constants.WIDTH, Constants.HEIGHT, this.mainTiles, this.hiddenTiles,
                Constants.SPRITE0);
        this.sprite1 = new Sprite(this.random, Constants.WIDTH, Constants.HEIGHT, this.mainTiles, this.hiddenTiles,
                Constants.SPRITE1);
        this.sprite0.loadSprite();
        this.sprite1.loadSprite();
        this.tiles = this.mainTiles;
        refreshHiddenTiles();
        if (!loading) {
            renderWorld();
        }
        if (this.random.nextInt() % 2 == 0) {
            this.it = sprite0;
            this.notIt = sprite1;
        } else {
            this.it = sprite1;
            this.notIt = sprite0;
        }
        this.it.pause();
        this.it.setIt();
    }

    /** Creates the hiddenTiles array. */
    private void refreshHiddenTiles() {
        for (int x = 0; x < this.mainTiles.length; x++) {
            for (int y = 0; y < this.mainTiles[0].length; y++) {
                this.hiddenTiles[x][y] = Constants.EMPTY;
            }
        }
        updateHiddenTiles();
        /*
        for (int x = 0; x < Constants.WIDTH; x++) {
            for (int y = 0; y < Constants.HEIGHT + Constants.HUD_HEIGHT; y++) {
                if ((Math.abs(this.sprite0.x() - x) <= Constants.VIEW_RADIUS
                        && Math.abs(sprite0.y() - y) <= Constants.VIEW_RADIUS)
                        || (Math.abs(this.sprite1.x() - x) <= Constants.VIEW_RADIUS
                        && Math.abs(sprite1.y() - y) <= Constants.VIEW_RADIUS)) {
                    this.hiddenTiles[x][y] = this.mainTiles[x][y];
                } else {
                    this.hiddenTiles[x][y] = Tileset.NOTHING;
                }
            }
        }
         */
    }

    /** Toggles between main tiles and hidden tiles. */
    private void hideTiles() {
        if (this.isHidden) {
            this.tiles = this.mainTiles;
        } else {
            refreshHiddenTiles();
            this.tiles = this.hiddenTiles;
        }
        this.isHidden = !this.isHidden;
    }

    /** Updates hidden tiles array. */
    private void updateHiddenTiles() {
        updateHiddenTilesForSprite(this.sprite0);
        updateHiddenTilesForSprite(this.sprite1);
    }

    /** Updates hidden tiles array for the tiles around a sprite. */
    private void updateHiddenTilesForSprite(Sprite sprite) {

        int xUpperBound = Math.min(this.mainTiles.length, sprite.x()
                + Constants.VIEW_RADIUS + Constants.HIDDEN_TILES_OFFSET);
        int xLowerBound = Math.max(0, sprite.x() - Constants.VIEW_RADIUS - Constants.HIDDEN_TILES_OFFSET);
        int yUpperBound = Math.min(this.mainTiles[0].length, sprite.y()
                + Constants.VIEW_RADIUS + Constants.HIDDEN_TILES_OFFSET);
        int yLowerBound = Math.max(0, sprite.y() - Constants.VIEW_RADIUS - Constants.HIDDEN_TILES_OFFSET);

        for (int i = xLowerBound; i < xUpperBound; i++) {
            for (int j = yLowerBound; j < yUpperBound; j++) {
                if ((Math.abs(this.sprite0.x() - i) <= Constants.VIEW_RADIUS
                        && Math.abs(this.sprite0.y() - j) <= Constants.VIEW_RADIUS)
                        || (Math.abs(this.sprite1.x() - i) <= Constants.VIEW_RADIUS
                        && Math.abs(this.sprite1.y() - j) <= Constants.VIEW_RADIUS)) {
                    this.hiddenTiles[i][j] = this.mainTiles[i][j];
                } else {
                    this.hiddenTiles[i][j] = Tileset.NOTHING;
                }
            }
        }
    }

    /** Gets the string from the tag-save-data.txt file and sets it to savedString. */
    private void loadSavedString() {
        // Reading from a file:
        // @Source: https://www.w3schools.com/java/java_files_read.asp
        try {
            File saveData = new File("save-data");
            Scanner reader = new Scanner(saveData);
            if (reader.hasNextLine()) {
                this.savedString = reader.nextLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            this.savedString = "";
        }
    }

    /** Takes in a string and uses the integers in the string to make a new random object. */
    private void parseSeed(String seedString) {
        String seed = "";
        for (int i = 0; i < seedString.length(); i++) {
            if (Constants.INTEGERS.indexOf(seedString.charAt(i)) >= 0) {
                seed += seedString.charAt(i);
            }
        }
        if (seed.equals("")) {
            this.random = new Random(0);
        } else {
            this.random = new Random(Long.parseLong(seed));
        }
    }

    /** Takes in an input string and moves the sprites accordingly. */
    private void parseMovementString(String movementString) {
        for (int i = 0; i < movementString.length(); i++) {
            interactWithMovementKeyBoard(movementString.charAt(i));
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        // Converting string to lowercase:
        // @Source: https://www.w3schools.com/java/ref_string_tolowercase.asp#:~:text=The%20toLowerCase()%20method%20
        // converts,string%20to%20upper%20case%20letters.

        System.out.println(input);

        this.savedString = input;
        String loweredString = this.savedString.toLowerCase();

        // Check whether input starts with n or l
        if (loweredString.charAt(0) == 'n') {

            // Read the seed
            String seedString = loweredString.substring(1, loweredString.indexOf('s'));
            parseSeed(seedString);
            // Create the new world
            newWorld(true);
            // Update all movements
            parseMovementString(loweredString.substring(loweredString.indexOf('s') + 1));
            // Save the game if quit
            if (loweredString.length() >= 2
                    && loweredString.substring(loweredString.length() - 2).equals(":q")) {
                System.out.println("save game called");
                saveGame();
            }

        } else if (loweredString.charAt(0) == 'l') {

            // Get save Data
            loadSavedString();
            // Add new data to the end of save data
            this.savedString += input.substring(1);
            String newLoweredString = this.savedString.toLowerCase();
            // Read the seed
            System.out.println("The new lowered string is " + newLoweredString);
            String seedString = newLoweredString.substring(1, newLoweredString.indexOf('s'));
            System.out.println("The seed is " + seedString);
            parseSeed(seedString);
            // Create the new world
            newWorld(true);
            // Update all movements
            parseMovementString(newLoweredString.substring(newLoweredString.indexOf('s') + 1));
            // Save the game if quit
            if (newLoweredString.length() >= 2
                    && newLoweredString.substring(newLoweredString.length() - 2).equals(":q")) {
                saveGame();
            }

        }

        return this.tiles;

    }

    /** Displays the type of tile the user's mouse is currently hovering over. */
    private void displayHUD() {

        // Display Mouse Pointer
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, Constants.HUD_SIZE);
        StdDraw.setFont(fontBig);

        int xCoor = (int) Math.floor(StdDraw.mouseX());
        int yCoor = (int) Math.floor(StdDraw.mouseY());

        String mouseStr;
        try {
            TETile tile = this.tiles[xCoor][yCoor];
            if (tile == Tileset.FLOOR) {
                mouseStr = "Floor";
            } else if (tile == Constants.SPRITE0) {
                mouseStr = "Player 1";
            } else if (tile == Constants.SPRITE1) {
                mouseStr = "Player 2";
            } else if (tile == Tileset.WALL) {
                mouseStr = "Wall";
            } else {
                mouseStr = "Empty";
            }
        } catch (ArrayIndexOutOfBoundsException a) {
            mouseStr = "Empty";
        }

        StdDraw.text(Constants.WIDTH - Constants.MOUSE_STRING_OFFSET,
                Constants.HEIGHT + Constants.HUD_HEIGHT - Constants.HUD_HEIGHT_OFFSET, mouseStr);

        // Display IT
        String itString = "Current IT: ";
        if (this.it == this.sprite0) {
            itString += "Player 1";
        } else {
            itString += "Player 2";
        }
        StdDraw.text(Constants.IT_TEXT_OFFSET, Constants.HEIGHT
                + Constants.HUD_HEIGHT - Constants.HUD_HEIGHT_OFFSET, itString);

        // Display Head Start Duration
        if (Constants.MAX_HEAD_START - this.notIt.getHeadStart() > 0) {
            String itPlayer;
            if (this.notIt == this.sprite0) {
                itPlayer = "Player 1 ";
            } else {
                itPlayer = "Player 2 ";
            }
            String headStartString = itPlayer + "remaining head start: ";
            headStartString += Constants.MAX_HEAD_START - this.notIt.getHeadStart();
            StdDraw.text(Constants.HEAD_START_DISPLAY_OFFSET,
                    Constants.HEIGHT + Constants.HUD_HEIGHT - Constants.HUD_HEIGHT_OFFSET, headStartString);
        }

        // Display Scores
        String scores = "Player 1: " + sprite0.getScore() + " Player 2: " + sprite1.getScore();
        StdDraw.text(Constants.SCORE_OFFSET, Constants.HEIGHT
                + Constants.HUD_HEIGHT - Constants.HUD_HEIGHT_OFFSET, scores);

        // Display Controls
        String controls = ":Q to save and quit | G to toggle difficulty | Space to toggle \"it\"'s speed";
        StdDraw.text(Constants.CONTROLS_OFFSET, 1, controls);
        StdDraw.show();

    }

}
