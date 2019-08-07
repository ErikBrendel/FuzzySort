package fuzzysort.ui;

import fuzzysort.solver.Graph;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class SortDisplay extends Display2D {

    private final Graph graph;
    private List<String> itemOrder = Collections.emptyList();

    public SortDisplay(Graph graph) {
        this.graph = graph;
    }

    @Override
    protected void drawData(Graphics2D g, Viewport vp) {
        itemOrder = graph.getItemOrder();

        g.setColor(Color.darkGray);
        for (int i = 0; i < itemOrder.size() - 1; i++) {
            Point p1 = vp.plotPoint(getX(i), getY(i));
            Point p2 = vp.plotPoint(getX(i + 1), getY(i + 1));
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }

        g.setColor(Color.black);
        for (int i = 0; i < itemOrder.size(); i++) {
            Point pos = vp.plotPoint(getX(i), getY(i));
            g.fillOval(pos.x - DotRadius, pos.y - DotRadius, DotDiameter, DotDiameter);
        }
    }

    @Override
    protected int getDataCount() {
        return graph.getAllNodes().size();
    }

    @Override
    protected float getX(int dataIndex) {
        return dataIndex;
    }

    @Override
    protected float getY(int dataIndex) {
        if (itemOrder.size() > dataIndex) {
            return Float.parseFloat(itemOrder.get(dataIndex));
        }
        return 0;
    }
}
