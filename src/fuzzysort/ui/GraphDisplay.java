package fuzzysort.ui;

import fuzzysort.solver.Graph;

import java.awt.*;

public class GraphDisplay extends Display2D {
    private final Graph graph;

    public GraphDisplay(Graph graph) {
        this.graph = graph;
    }

    @Override
    protected void drawData(Graphics2D g, Viewport vp) {
        synchronized (graph) {

            for (Graph.Edge edge: graph.getAllEdges()) {
                g.setColor(LerpColor3(Color.red, Color.green, Color.blue, edge.getLengthForce()));
                Point p1 = vp.plotPoint(getX(edge.n1), getY(edge.n1));
                Point p2 = vp.plotPoint(getX(edge.n2), getY(edge.n2));
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            g.setColor(Color.black);
            for (Graph.Node node: graph.getAllNodes()) {
                Point pos = vp.plotPoint(getX(node), getY(node));
                g.fillOval(pos.x - DotRadius, pos.y - DotRadius, DotDiameter, DotDiameter);
                g.drawString(node.item, pos.x + 4, pos.y + 5);
            }
        }
    }

    @Override
    protected int getDataCount() {
        return graph.getAllNodes().size();
    }

    @Override
    protected float getX(int dataIndex) {
        return getX(graph.getAllNodes().get(dataIndex));
    }

    public float getX(Graph.Node node) {
        return node.mainCoordinate;
    }

    @Override
    protected float getY(int dataIndex) {
        return getY(graph.getAllNodes().get(dataIndex));
    }

    protected float getY(Graph.Node node) {
        float[] coords = node.otherCoordinates;
        if (coords.length > 0) return coords[0];
        try {
            return Integer.parseInt(node.item);
        } catch (NumberFormatException ex) {
            // no integers, sad
        }
        return graph.getAllNodes().indexOf(node);
    }
}
