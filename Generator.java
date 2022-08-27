import java.util.*;
import java.io.FileWriter; 
import java.io.IOException;

public class Generator {

    int boardSize = 8;
    int levelsToGenerate = 3;
    int maxIterations = 900;

    char[][] board;
    String rotationsReflections = "";
    Tile[] tiles = new Tile[21];

    List<int[]> redCornerCells = new ArrayList<int[]>();
    List<int[]> redSideCells = new ArrayList<int[]>();
    List<int[]> blueCornerCells = new ArrayList<int[]>();
    List<int[]> blueSideCells = new ArrayList<int[]>();
    List<int[]> yellowCornerCells = new ArrayList<int[]>();
    List<int[]> yellowSideCells = new ArrayList<int[]>();

    Map<Integer, Character> redTileMap = new HashMap<Integer, Character>();
    Map<Integer, Character> blueTileMap = new HashMap<Integer, Character>();
    Map<Integer, Character> yellowTileMap = new HashMap<Integer, Character>();

    ArrayList<Integer> redTileNumbers = new ArrayList<Integer>();
    ArrayList<Integer> blueTileNumbers = new ArrayList<Integer>();
    ArrayList<Integer> yellowTileNumbers = new ArrayList<Integer>();

    public static void main(String[] args) {
        Generator generator = new Generator();

        // Prompt for board size, number of colors, and number of levels to generate
        System.out.print("What board size do you want to generate? : ");
        Scanner scanner = new Scanner(System.in);
        generator.boardSize = scanner.nextInt();

        System.out.println("");
        System.out.print("How many levels should we generate? : ");
        generator.levelsToGenerate = scanner.nextInt();

        generator.generateLevels();
    }

    public void generateLevels() {
        populateTileNumberLists();
        createBoard(boardSize);
        createTileMaps();
        createTileArray();

        int levelsGenerated = 0;
        while (levelsGenerated < levelsToGenerate) {
            redCornerCells = new ArrayList<int[]>();
            redSideCells = new ArrayList<int[]>();
            blueCornerCells = new ArrayList<int[]>();
            blueSideCells = new ArrayList<int[]>();
            yellowCornerCells = new ArrayList<int[]>();
            yellowSideCells = new ArrayList<int[]>();

            redTileNumbers = new ArrayList<Integer>();
            blueTileNumbers = new ArrayList<Integer>();
            yellowTileNumbers = new ArrayList<Integer>();
            populateTileNumberLists();
            createBoard(boardSize);
            createTileArray();

            if (boardSize < 5) {
                if (placeTiles("red") && placeTiles("blue")) {
                    fillVacantCells();
                    generateFile(levelsGenerated + 1);
                    printBoard();
                    levelsGenerated += 1;
                }
            } else {
                if (placeTiles("red") && placeTiles("blue") && placeTiles("yellow")) {
                    fillVacantCells();
                    generateFile(levelsGenerated + 1);
                    printBoard();
                    levelsGenerated += 1;
                }
            }
        }
    }

    private boolean placeTiles(String color) {

        int tilesPlaced = 0;
        int iterations = 0;
        int tilesToPlace = 0;

        // Set the number of tiles to place for each color depending on board size
        if (boardSize == 5) {
            tilesToPlace = 2;
        } else if (boardSize == 6) {
            tilesToPlace = 3;
        } else if (boardSize == 7) {
            if (color == "red") {
                tilesToPlace = 3;
            } else {
                tilesToPlace = 2;
            }
        } else if (boardSize == 8) {
            tilesToPlace = 3;
        } else if (boardSize == 9) {
            tilesToPlace = 4;
        }

        while (tilesPlaced < tilesToPlace) {

            // Get a random red tile from the list
            Tile tile = GetRandomTile(color);

            // Rotate tile n times between 0 and 3
            int rnd = new Random().nextInt(4);
            tile.rotations = rnd;
            for (int i = 0; i < rnd; i++) {
                tile.rotate();
            }

            // Decide randomly to flip tile or not
            Random rd = new Random();
            Boolean rd1 = rd.nextBoolean();
            if (rd1) {
                tile.reflect();
                tile.reflected = 1;
            }

            // If this is the first tile placed...
            if (tilesPlaced == 0) {

                // Generate random location on the board
                int[] randomCoords = getRandomLocationOnBoard(board);
                //System.out.println("Random Board Coords: " + randomCoords[0] + ", " + randomCoords[1]);

                boolean tilePlaced = true;

                // Check each tile cell in the tile to make sure it's in a valid place
                for (int i = 0; i < tile.coords.length; i++) {
                    int x = tile.coords[i][0];
                    int y = tile.coords[i][1];

                    // First check that the tile coords are within the board
                    if ((randomCoords[0] + x) < board.length && (randomCoords[1] + y) < board[0].length && (randomCoords[0] + x) >= 0 && (randomCoords[1] + y) >= 0) {
                        // If the tile cell is covering an occupied cell, don't place the tile
                        if (board[randomCoords[0] + x][randomCoords[1] + y] != '0') {
                            //board[randomCoords[1] + x][randomCoords[0] + y] = redTileMap.get(tile.name);
                            tilePlaced = false;
                        }
                    } else {
                        tilePlaced = false;
                    }
                }

                // If we should place the tile...
                if (tilePlaced) {
                    
                    // Create a list of sideCells and cornerCells for this tile
                    CreateSideAndCornerCellList(tile, color, randomCoords);
                    tilesPlaced += 1;
                    printBoard();
                }
            } else {

                if (color == "red") {

                    if (iterations < maxIterations) {
                        // Loop through corner cells and try to place tile on one of them
                        Collections.shuffle(redCornerCells);
                        int[][] redCornerCellArray = new int[redCornerCells.size()][2];
                        redCornerCellArray = redCornerCells.toArray(redCornerCellArray);
                        for (int i = 0; i < redCornerCellArray.length; i++) {
        
                            boolean tilePlaced = true;
                            
                            // Check each tile cell in the tile to make sure it's in a valid place
                            for (int j = 0; j < tile.coords.length; j++) {
                                int x = tile.coords[j][0];
                                int y = tile.coords[j][1];
        
                                // First check that the tile coords are within the board
                                if ((redCornerCellArray[i][0] + x) < board.length && (redCornerCellArray[i][1] + y) < board[0].length && (redCornerCellArray[i][0] + x) >= 0 && (redCornerCellArray[i][1] + y) >= 0) {
                                    // If the tile cell is covering an occupied cell, don't place the tile
                                    if (board[redCornerCellArray[i][0] + x][redCornerCellArray[i][1] + y] != '0') {
                                        tilePlaced = false;
                                    }
                                } else {
                                    tilePlaced = false;
                                }
        
                                // If the tile is touching any side cells, don't place it
                                for (int[] sideCell : redSideCells) {
                                    if (redCornerCellArray[i][0] + x == sideCell[0] && redCornerCellArray[i][1] + y == sideCell[1]) {
                                        tilePlaced = false;
                                    }
                                }
                            }
        
                            // If we should place the tile...
                            if (tilePlaced) {
                                // Create a list of sideCells and cornerCells for this tile
                                CreateSideAndCornerCellList(tile, color, redCornerCellArray[i]);
                                //System.out.println(color + ", " + redCornerCellArray[i][1] + ", " + redCornerCellArray[i][0]);
                                tilesPlaced += 1;
                                break;
                            }
                        }
                        iterations += 1;
                    } else {
                        return false;
                    }
                    
                } else if (color == "blue") {

                    if (iterations < maxIterations) {
                        // Loop through corner cells and try to place tile on one of them
                        Collections.shuffle(blueCornerCells);
                        int[][] blueCornerCellArray = new int[blueCornerCells.size()][2];
                        blueCornerCellArray = blueCornerCells.toArray(blueCornerCellArray);
                        for (int i = 0; i < blueCornerCellArray.length; i++) {
        
                            boolean tilePlaced = true;
                            
                            // Check each tile cell in the tile to make sure it's in a valid place
                            for (int j = 0; j < tile.coords.length; j++) {
                                int x = tile.coords[j][0];
                                int y = tile.coords[j][1];
        
                                // First check that the tile coords are within the board
                                if ((blueCornerCellArray[i][0] + x) < board.length && (blueCornerCellArray[i][1] + y) < board[0].length && (blueCornerCellArray[i][0] + x) >= 0 && (blueCornerCellArray[i][1] + y) >= 0) {
                                    // If the tile cell is covering an occupied cell, don't place the tile
                                    if (board[blueCornerCellArray[i][0] + x][blueCornerCellArray[i][1] + y] != '0') {
                                        tilePlaced = false;
                                    }
                                } else {
                                    tilePlaced = false;
                                }
        
                                // If the tile is touching any side cells, don't place it
                                for (int[] sideCell : blueSideCells) {
                                    if (blueCornerCellArray[i][0] + x == sideCell[0] && blueCornerCellArray[i][1] + y == sideCell[1]) {
                                        tilePlaced = false;
                                    }
                                }
                            }
        
                            // If we should place the tile...
                            if (tilePlaced) {
                                // Create a list of sideCells and cornerCells for this tile
                                CreateSideAndCornerCellList(tile, color, blueCornerCellArray[i]);
                                //System.out.println(color + ", " + blueCornerCellArray[i][1] + ", " + blueCornerCellArray[i][0]);
                                tilesPlaced += 1;
                                break;
                            }
                        }
                        iterations += 1;
                    } else {
                        return false;
                    }
                    
                } else {

                    if (iterations < maxIterations) {
                        // Loop through corner cells and try to place tile on one of them
                        Collections.shuffle(yellowCornerCells);
                        int[][] yellowCornerCellArray = new int[yellowCornerCells.size()][2];
                        yellowCornerCellArray = yellowCornerCells.toArray(yellowCornerCellArray);
                        for (int i = 0; i < yellowCornerCellArray.length; i++) {
        
                            boolean tilePlaced = true;
                            
                            // Check each tile cell in the tile to make sure it's in a valid place
                            for (int j = 0; j < tile.coords.length; j++) {
                                int x = tile.coords[j][0];
                                int y = tile.coords[j][1];
        
                                // First check that the tile coords are within the board
                                if ((yellowCornerCellArray[i][0] + x) < board.length && (yellowCornerCellArray[i][1] + y) < board[0].length && (yellowCornerCellArray[i][0] + x) >= 0 && (yellowCornerCellArray[i][1] + y) >= 0) {
                                    // If the tile cell is covering an occupied cell, don't place the tile
                                    if (board[yellowCornerCellArray[i][0] + x][yellowCornerCellArray[i][1] + y] != '0') {
                                        tilePlaced = false;
                                    }
                                } else {
                                    tilePlaced = false;
                                }
        
                                // If the tile is touching any side cells, don't place it
                                for (int[] sideCell : yellowSideCells) {
                                    if (yellowCornerCellArray[i][0] + x == sideCell[0] && yellowCornerCellArray[i][1] + y == sideCell[1]) {
                                        tilePlaced = false;
                                    }
                                }
                            }
        
                            // If we should place the tile...
                            if (tilePlaced) {
                                // Create a list of sideCells and cornerCells for this tile
                                CreateSideAndCornerCellList(tile, color, yellowCornerCellArray[i]);
                                //System.out.println(color + ", " + yellowCornerCellArray[i][1] + ", " + yellowCornerCellArray[i][0]);
                                tilesPlaced += 1;
                                break;
                            }
                        }
                        iterations += 1;
                    } else  {
                        return false;
                    }
                }
            }
            
            CleanUpCornerAndSideCells("red");
            CleanUpCornerAndSideCells("blue");
            CleanUpCornerAndSideCells("yellow");

            if (iterations == maxIterations) {
                rotationsReflections = "";
                return false;
            }
        }
        
        /*// Print redSideCells
        System.out.println("redSideCells:");
        for (int[] coords : redSideCells) {
            System.out.println(coords[1] + ", " + coords[0]);
        }

        // Print redCornerCells
        System.out.println("redCornerCells:");
        for (int[] coords : redCornerCells) {
            System.out.println(coords[1] + ", " + coords[0]);
        }*/

        if (tilesPlaced < tilesToPlace) {
            System.out.println("Could not generate board.");
            return false;
        } else {
            return true;
        }
    }

    private void generateFile(int levelNumber) {
        // Create board file
        try {
            // Create a new FileWriter object with name of level
            FileWriter myWriter = new FileWriter("levels/" + levelNumber + ".txt");
            String text = "";

            // Loop through board and append each character to the string
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    text += board[i][j];
                }
                text += "\n";
            }

            // Write the string to the file
            myWriter.write(text);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // Create tile orientation file
        try {
            // Create a new FileWriter object with name of level
            FileWriter myWriter = new FileWriter("levels/" + levelNumber + "_orientations.txt");

            // Write the string to the file
            myWriter.write(rotationsReflections);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        System.out.println(rotationsReflections);
    }

    public int[] getRandomLocationOnBoard(char[][] board) {
        int random1 = new Random().nextInt(board.length);
        int random2 = new Random().nextInt(board[0].length);

        return new int[] {random1,random2};
    }

    public void fillVacantCells() {
        // Randomly fill the remaining empty cells with borders
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == '0') {
                    Random rd = new Random();
                    if (rd.nextBoolean())
                    board[i][j] = '1';
                }
            }
        }
    }

    public void printBoard() {
        System.out.println("Board status:");
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    public void createBoard(int size) {
        board = new char[size][size];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = '0';
            }
        }
    }

    public void createTileMaps() {
        redTileMap.put(1,'a');
        redTileMap.put(2,'b');
        redTileMap.put(3,'c');
        redTileMap.put(4,'d');
        redTileMap.put(5,'e');
        redTileMap.put(6,'f');
        redTileMap.put(7,'g');
        redTileMap.put(8,'h');
        redTileMap.put(9,'i');
        redTileMap.put(10,'j');
        redTileMap.put(11,'k');
        redTileMap.put(12,'l');
        redTileMap.put(13,'m');
        redTileMap.put(14,'n');
        redTileMap.put(15,'o');
        redTileMap.put(16,'p');
        redTileMap.put(17,'q');
        redTileMap.put(18,'r');
        redTileMap.put(19,'s');
        redTileMap.put(20,'t');
        redTileMap.put(21,'u');

        blueTileMap.put(1,'A');
        blueTileMap.put(2,'B');
        blueTileMap.put(3,'C');
        blueTileMap.put(4,'D');
        blueTileMap.put(5,'E');
        blueTileMap.put(6,'F');
        blueTileMap.put(7,'G');
        blueTileMap.put(8,'H');
        blueTileMap.put(9,'I');
        blueTileMap.put(10,'J');
        blueTileMap.put(11,'K');
        blueTileMap.put(12,'L');
        blueTileMap.put(13,'M');
        blueTileMap.put(14,'N');
        blueTileMap.put(15,'O');
        blueTileMap.put(16,'P');
        blueTileMap.put(17,'Q');
        blueTileMap.put(18,'R');
        blueTileMap.put(19,'S');
        blueTileMap.put(20,'T');
        blueTileMap.put(21,'U');

        yellowTileMap.put(1,'v');
        yellowTileMap.put(2,'V');
        yellowTileMap.put(3,'w');
        yellowTileMap.put(4,'W');
        yellowTileMap.put(5,'x');
        yellowTileMap.put(6,'X');
        yellowTileMap.put(7,'y');
        yellowTileMap.put(8,'Y');
        yellowTileMap.put(9,'z');
        yellowTileMap.put(10,'Z');
        yellowTileMap.put(11,'2');
        yellowTileMap.put(12,'3');
        yellowTileMap.put(13,'4');
        yellowTileMap.put(14,'5');
        yellowTileMap.put(15,'6');
        yellowTileMap.put(16,'7');
        yellowTileMap.put(17,'8');
        yellowTileMap.put(18,'9');
        yellowTileMap.put(19,'<');
        yellowTileMap.put(20,'>');
        yellowTileMap.put(21,'+');
    }

    public void createTileArray() {
        tiles[0] = new Tile(1, new int[][]{{0,0}});
        tiles[1] = new Tile(2, new int[][]{{0,0},{1,0}});
        tiles[2] = new Tile(3, new int[][]{{0,0},{1,0},{2,0}});
        tiles[3] = new Tile(4, new int[][]{{0,0},{1,0},{1,1}});
        tiles[4] = new Tile(5, new int[][]{{0,0},{1,0},{2,0},{3,0}});
        tiles[5] = new Tile(6, new int[][]{{0,0},{1,0},{2,0},{2,1}});
        tiles[6] = new Tile(7, new int[][]{{0,0},{1,0},{2,0},{1,-1}});
        tiles[7] = new Tile(8, new int[][]{{0,0},{1,0},{0,-1},{1,-1}});
        tiles[8] = new Tile(9, new int[][]{{0,0},{1,0},{1,-1},{2,-1}});
        tiles[9] = new Tile(10, new int[][]{{0,0},{1,0},{2,0},{3,0},{4,0}});
        tiles[10] = new Tile(11, new int[][]{{0,0},{1,0},{2,0},{3,0},{3,-1}});
        tiles[11] = new Tile(12, new int[][]{{0,0},{1,0},{2,0},{2,-1},{3,-1}});
        tiles[12] = new Tile(13, new int[][]{{0,0},{1,0},{2,0},{1,-1},{2,-1}});
        tiles[13] = new Tile(14, new int[][]{{0,0},{2,0},{0,-1},{1,-1},{2,-1}});
        tiles[14] = new Tile(15, new int[][]{{0,0},{1,0},{2,0},{3,0},{1,-1}});
        tiles[15] = new Tile(16, new int[][]{{0,0},{1,0},{2,0},{2,1},{2,-1}});
        tiles[16] = new Tile(17, new int[][]{{0,0},{0,-1},{0,-2},{1,-2},{2,-2}});
        tiles[17] = new Tile(18, new int[][]{{0,0},{1,0},{1,-1},{2,-1},{2,-2}});
        tiles[18] = new Tile(19, new int[][]{{0,0},{0,-1},{1,-1},{2,-1},{2,-2}});
        tiles[19] = new Tile(20, new int[][]{{0,0},{0,-1},{1,-1},{2,-1},{1,-2}});
        tiles[20] = new Tile(21, new int[][]{{0,0},{1,0},{2,0},{1,1},{1,-1}});
    }

    public void populateTileNumberLists() {
        redTileNumbers.add(1);
        redTileNumbers.add(2);
        redTileNumbers.add(3);
        redTileNumbers.add(4);
        redTileNumbers.add(5);
        redTileNumbers.add(6);
        redTileNumbers.add(7);
        redTileNumbers.add(8);
        redTileNumbers.add(9);
        redTileNumbers.add(10);
        redTileNumbers.add(11);
        redTileNumbers.add(12);
        redTileNumbers.add(13);
        redTileNumbers.add(14);
        redTileNumbers.add(15);
        redTileNumbers.add(16);
        redTileNumbers.add(17);
        redTileNumbers.add(18);
        redTileNumbers.add(19);
        redTileNumbers.add(20);
        redTileNumbers.add(21);

        blueTileNumbers.add(1);
        blueTileNumbers.add(2);
        blueTileNumbers.add(3);
        blueTileNumbers.add(4);
        blueTileNumbers.add(5);
        blueTileNumbers.add(6);
        blueTileNumbers.add(7);
        blueTileNumbers.add(8);
        blueTileNumbers.add(9);
        blueTileNumbers.add(10);
        blueTileNumbers.add(11);
        blueTileNumbers.add(12);
        blueTileNumbers.add(13);
        blueTileNumbers.add(14);
        blueTileNumbers.add(15);
        blueTileNumbers.add(16);
        blueTileNumbers.add(17);
        blueTileNumbers.add(18);
        blueTileNumbers.add(19);
        blueTileNumbers.add(20);
        blueTileNumbers.add(21);

        yellowTileNumbers.add(1);
        yellowTileNumbers.add(2);
        yellowTileNumbers.add(3);
        yellowTileNumbers.add(4);
        yellowTileNumbers.add(5);
        yellowTileNumbers.add(6);
        yellowTileNumbers.add(7);
        yellowTileNumbers.add(8);
        yellowTileNumbers.add(9);
        yellowTileNumbers.add(10);
        yellowTileNumbers.add(11);
        yellowTileNumbers.add(12);
        yellowTileNumbers.add(13);
        yellowTileNumbers.add(14);
        yellowTileNumbers.add(15);
        yellowTileNumbers.add(16);
        yellowTileNumbers.add(17);
        yellowTileNumbers.add(18);
        yellowTileNumbers.add(19);
        yellowTileNumbers.add(20);
        yellowTileNumbers.add(21);
    }

    private List<int[]> getCornerAdjacentCells(int x, int y) {

        List<int[]> cornerCellsToTest = new ArrayList<int[]>();

        // Gather all the corner adjacent cells for this grid cell
        if (x - 1 >= 0 && y - 1 >= 0)
            cornerCellsToTest.add(new int[] {x - 1, y - 1});
        if (x + 1 < boardSize && y - 1 >= 0)
            cornerCellsToTest.add(new int[] {x + 1, y - 1});
        if (x - 1 >= 0 && y + 1 < boardSize)
            cornerCellsToTest.add(new int[] {x - 1, y + 1});
        if (x + 1 < boardSize && y + 1 < boardSize)
            cornerCellsToTest.add(new int[] {x + 1, y + 1});

        return cornerCellsToTest;
    }

    private List<int[]> getSideAdjacentCells(int x, int y) {
        
        List<int[]> sideCellsToTest = new ArrayList<int[]>();

        // Gather all the side adjacent cells for this grid cell
        if (x - 1 >= 0)
            sideCellsToTest.add(new int[] {x - 1, y});
        if (x + 1 < boardSize)
            sideCellsToTest.add(new int[] {x + 1, y});
        if (y - 1 >= 0)
            sideCellsToTest.add(new int[] {x, y - 1});
        if (y + 1 < boardSize)
            sideCellsToTest.add(new int[] {x, y + 1});

        return sideCellsToTest;
    }

    private void CleanUpCornerAndSideCells(String color) {

        List<int[]> sideCells = new ArrayList<int[]>();
        List<int[]> cornerCells = new ArrayList<int[]>();

        if (color == "red") {
            sideCells = redSideCells;
        } else if (color == "blue") {
            sideCells = blueSideCells;
        } else {
            sideCells = yellowSideCells;
        }

        // Remove occupied cells from sideCells
        List<int[]> sideCellsToRemove = new ArrayList<int[]>();
        for (int[] sideCell : sideCells) {
            if (board[sideCell[0]][sideCell[1]] != '0')
            sideCellsToRemove.add(sideCell);
        }
        if (color == "red") {
            redSideCells.removeAll(sideCellsToRemove);
        } else if (color == "blue") {
            blueSideCells.removeAll(sideCellsToRemove);
        } else {
            yellowSideCells.removeAll(sideCellsToRemove);
        }

        // Remove occupied cells from cornerCells
        List<int[]> cornerCellsToRemove = new ArrayList<int[]>();
        for (int[] cornerCell : cornerCells) {
            if (board[cornerCell[0]][cornerCell[1]] != '0')
            cornerCellsToRemove.add(cornerCell);
        }
        if (color == "red") {
            redCornerCells.removeAll(cornerCellsToRemove);
        } else if (color == "blue") {
            blueCornerCells.removeAll(cornerCellsToRemove);
        } else {
            yellowCornerCells.removeAll(cornerCellsToRemove);
        }

        // Remove cornerCells that are also side cells
        List<int[]> cornerCellsToRemove2 = new ArrayList<int[]>();
        for (int[] cornerCell : cornerCells) {
            for (int[] sideCell : sideCells) {
                if (cornerCell[0] == sideCell[0] && cornerCell[1] == sideCell[1])
                cornerCellsToRemove2.add(cornerCell);
            }
        }
        if (color == "red") {
            redCornerCells.removeAll(cornerCellsToRemove2);
        } else if (color == "blue") {
            blueCornerCells.removeAll(cornerCellsToRemove2);
        } else {
            yellowCornerCells.removeAll(cornerCellsToRemove2);
        }
    }

    private Tile GetRandomTile(String name) {
        if (name == "red") {
            // Get random index from array
            int rnd = new Random().nextInt(redTileNumbers.size());
            // Get tile number at that index
            int tileNumber = redTileNumbers.get(rnd);
            // Get the tile associated with that number
            return tiles[tileNumber - 1];
        } else if (name == "blue") {
            // Get random index from array
            int rnd = new Random().nextInt(blueTileNumbers.size());
            // Get tile number at that index
            int tileNumber = blueTileNumbers.get(rnd);
            // Get the tile associated with that number
            return tiles[tileNumber - 1];
        } else {
            // Get random index from array
            int rnd = new Random().nextInt(yellowTileNumbers.size());
            // Get tile number at that index
            int tileNumber = yellowTileNumbers.get(rnd);
            // Get the tile associated with that number
            return tiles[tileNumber - 1];
        }
    }

    private void CreateSideAndCornerCellList(Tile tile, String color, int[] randomCoords) {

        List<int[]> sideCellList = new ArrayList<int[]>();
        List<int[]> cornerCellList = new ArrayList<int[]>();

        // Add the tile's orientation to the file
        if (color == "red") {
            rotationsReflections += redTileMap.get(tile.name).toString() + tile.rotations + tile.reflected;
        } else if (color == "blue") {
            rotationsReflections += blueTileMap.get(tile.name).toString() + tile.rotations + tile.reflected;
        } else {
            rotationsReflections += yellowTileMap.get(tile.name).toString() + tile.rotations + tile.reflected;
        }
        
        for (int i = 0; i < tile.coords.length; i++) {
            int x = tile.coords[i][0];
            int y = tile.coords[i][1];

            // Get all corner cells and add to cornerCellList
            cornerCellList.addAll(getCornerAdjacentCells(randomCoords[0] + x, randomCoords[1] + y));

            // Get all side cells and add to sideCellList
            sideCellList.addAll(getSideAdjacentCells(randomCoords[0] + x, randomCoords[1] + y));
            
            // Check that the tile coords are within the board
            if ((randomCoords[0] + x) < board.length && (randomCoords[1] + y) < board[0].length && (randomCoords[0] + x) >= 0 && (randomCoords[1] + y) >= 0) {
                if (board[randomCoords[0] + x][randomCoords[1] + y] == '0') {
                    // Set the board coords to the tile name

                    if (color == "red") {
                        board[randomCoords[0] + x][randomCoords[1] + y] = redTileMap.get(tile.name);

                        // Remove that tile number from the list
                        redTileNumbers.remove(Integer.valueOf(tile.name));
                    } else if (color == "blue") {
                        board[randomCoords[0] + x][randomCoords[1] + y] = blueTileMap.get(tile.name);

                        // Remove that tile number from the list
                        blueTileNumbers.remove(Integer.valueOf(tile.name));                        
                    } else {
                        board[randomCoords[0] + x][randomCoords[1] + y] = yellowTileMap.get(tile.name);

                        // Remove that tile number from the list
                        yellowTileNumbers.remove(Integer.valueOf(tile.name));                         
                    }

                }
            }
        }

        // Remove corner cells that are part of the tile
        int[][] cornerCellListArray = new int[cornerCellList.size()][2];
        cornerCellListArray = cornerCellList.toArray(cornerCellListArray);
        for (int g = 0; g < cornerCellListArray.length; g++) {
            for (int j = 0; j < tile.coords.length; j++) {

                int x = tile.coords[j][0];
                int y = tile.coords[j][1];

                if (cornerCellListArray[g][0] == (randomCoords[0] + x) && cornerCellListArray[g][1] == (randomCoords[1] + y)) {
                    cornerCellList.remove(cornerCellListArray[g]);
                }
            }
        }

        // Loop through cornerCellList and remove any that are in sideCellList
        List<int[]> valuesToRemove = new ArrayList<int[]>();
        for (int[] cornerCell : cornerCellList) {
            for (int[] sideCell : sideCellList) {
                if (cornerCell[0] == sideCell[0] && cornerCell[1] == sideCell[1]) {
                    valuesToRemove.add(cornerCell);
                    //System.out.println("corner cell removed");
                }
            }
        }
        cornerCellList.removeAll(valuesToRemove);


        // Add sideCellList to master side cell list and cornerCellList to master corner cell list
        if (color == "red") {
            redSideCells.addAll(sideCellList);
            redCornerCells.addAll(cornerCellList);
        } else if (color == "blue") {
            blueSideCells.addAll(sideCellList);
            blueCornerCells.addAll(cornerCellList);          
        } else {
            yellowSideCells.addAll(sideCellList);
            yellowCornerCells.addAll(cornerCellList);              
        }
    }
}