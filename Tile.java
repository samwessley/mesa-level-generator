public class Tile {

    public int name;
    public int[][] coords;
    public int[][] originalCoords;
    public int rotations = 0;
    public int reflected = 0;

    public Tile(int n, int[][] c) {
        name = n;
        coords = c;
        originalCoords = c;
    }

    public void rotate() {
        for (int i = 0; i < coords.length; i++) {
            int oldX = coords[i][0];
            int oldY = coords[i][1];

            coords[i][0] = oldY;
            coords[i][1] = -oldX;
        }
        rotations += 1;
        if (rotations == 4)
        rotations = 0;
    }

    public void reflect() {
        //reset();

        if (reflected == 0) {
            for (int i = 0; i < coords.length; i++) {
                coords[i][0] = -coords[i][0];
            }
            reflected = 1;
        } else {
            reflected = 0;
        }
    }

    public void reset() {
        rotations = 0;
        //coords = originalCoords;
    }
}