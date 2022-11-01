package fr.jmini.gitgraph4j;

public class Shape {
    public int x;
    public int y;
    public int width;
    public int height;
    public String graphId;

    public int xLeft() {
        return x;
    }

    public int xCenter() {
        return x + (width / 2);
    }

    public int xRight() {
        return x + width;
    }

    public int yTop() {
        return y;
    }

    public int yCenter() {
        return y + (height / 2);
    }

    public int yBottom() {
        return y + height;
    }
}