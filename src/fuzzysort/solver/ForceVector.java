package fuzzysort.solver;

public class ForceVector {
    public float mainCoordinate;
    public final float[] otherCoordinates;

    public ForceVector scale(float scale) {
        mainCoordinate *= scale;
        for (int i = 0; i < otherCoordinates.length; i++) {
            otherCoordinates[i] *= scale;
        }
        return this;
    }

    public ForceVector add(ForceVector other) {
        mainCoordinate += other.mainCoordinate;
        for (int i = 0; i < otherCoordinates.length; i++) {
            otherCoordinates[i] += other.otherCoordinates[i];
        }
        return this;
    }

    public ForceVector copy() {
        float[] newOthers = new float[otherCoordinates.length];
        System.arraycopy(otherCoordinates, 0, newOthers, 0, otherCoordinates.length);
        return new ForceVector(mainCoordinate, newOthers);
    }

    public static ForceVector between(Graph.Node n1, Graph.Node n2) {
        float[] others = new float[n1.otherCoordinates.length];
        for (int i = 0; i < n1.otherCoordinates.length; i++) {
            others[i] = n2.otherCoordinates[i] - n1.otherCoordinates[i];
        }
        return new ForceVector(n2.mainCoordinate - n1.mainCoordinate, others);
    }

    public static ForceVector nullFor(Graph.Node node) {
        return new ForceVector(0, new float[node.otherCoordinates.length]);
    }

    public static ForceVector upFor(Graph.Node node) {
        return new ForceVector(1, new float[node.otherCoordinates.length]);
    }

    private ForceVector(float mainCoordinate, float[] otherCoordinates) {
        this.mainCoordinate = mainCoordinate;
        this.otherCoordinates = otherCoordinates;
    }

    public float getSqrMagnitude() {
        float sum = mainCoordinate * mainCoordinate;
        for (float otherCoordinate : otherCoordinates) {
            sum += otherCoordinate * otherCoordinate;
        }
        return sum;
    }

    public float getMagnitude() {
        return (float) Math.sqrt(getSqrMagnitude());
    }

}
