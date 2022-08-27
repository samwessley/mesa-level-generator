public class Tile {

    public int name;
    public int[][] coords;
    public int rotations = 0;
    public int reflected = 0;

    public Tile(int n, int[][] c) {
        name = n;
        coords = c;
    }

    public void rotate() {
        for (int i = 0; i < coords.length; i++) {
            int oldX = coords[i][0];
            int oldY = coords[i][1];

            coords[i][0] = -oldY;
            coords[i][1] = oldX;
        }
    }

    public void reflect() {
        for (int i = 0; i < coords.length; i++) {
            coords[i][0] = -coords[i][0];
        }
    }
}