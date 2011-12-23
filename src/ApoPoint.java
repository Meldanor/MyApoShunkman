import java.awt.Point;

public class ApoPoint implements Comparable<ApoPoint> {

    private ApoPoint previous;
    private Point p;
    private double G;
    private double H;

    public ApoPoint(Point p, ApoPoint previous, Point goal, double G) {
        this.p = p;
        this.G = G;
        this.H = p.distance(goal);
        this.previous = previous;
    }

    public ApoPoint(int x, int y, ApoPoint previous, Point goal, double G) {
        this(new Point(x, y), previous, goal, G);
    }

    public double getF() {
        return G + H;
    }

    public double getH() {
        return H;
    }

    public double getG() {
        return G;
    }

    public Point getPoint() {
        return p;
    }

    public int getX() {
        return p.x;
    }

    public int getY() {
        return p.y;
    }

    public ApoPoint getPrevious() {
        return previous;
    }

    public void setPrevious(ApoPoint previous) {
        this.previous = previous;
        this.G = previous.getG() + 1.0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ApoPoint && ((ApoPoint) obj).getPoint().equals(p);
    }

    @Override
    public String toString() {
        return "(" + p.x + "," + p.y + ")" + " " + getG() + " " + getF() + " "
                + getH();
    }

    public int compareTo(ApoPoint o) {

        if (this.getF() < o.getF())
            return -1;
        if (this.getF() > o.getF())
            return 1;
        return this.getH() <= o.getH() ? -1 : 1;
    }

}
