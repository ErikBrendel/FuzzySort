package fuzzysort.ui;

import fuzzysort.solver.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

public abstract class Display2D extends JPanel {
    protected static final boolean equalScaleDimensions = false;
    protected static final int padding = 30;
    protected static final int DotRadius = 3;
    protected static final int DotDiameter = DotRadius * 2 + 1;
    protected static final DecimalFormat df = new DecimalFormat("00.00");

    @Override
    public void paint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        int w = getWidth();
        int h = getHeight();

        g.setColor(Color.white);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.black);

        Rectangle2D.Float dataBounds = getDataBounds();
        g.drawString("Rect: (x=" + df.format(dataBounds.x) + ", y=" + df.format(dataBounds.y) +
                ", w=" + df.format(dataBounds.width) + ", h=" + df.format(dataBounds.height) + ")", 50, 20);

        w -= padding * 2;
        h -= padding * 2;
        drawData(g, new Viewport(dataBounds, w, h));
    }



    protected Color LerpColor3(Color c1, Color c2, Color c3, float factor) {
        if (factor > 0) {
            return LerpColor(c2, c3, factor);
        } else if (factor < 0) {
            return LerpColor(c2, c1, -factor);
        }
        return c2;
    }

    protected Color LerpColor(Color c1, Color c2, float factor) {
        if (factor > 1) factor = 1;
        if (factor < 0) factor = 0;
        float fac1 = 1 - factor;
        return new Color(
                Math.round(c1.getRed() * fac1 + c2.getRed() * factor),
                Math.round(c1.getGreen() * fac1 + c2.getGreen() * factor),
                Math.round(c1.getBlue() * fac1 + c2.getBlue() * factor));
    }

    private Rectangle2D.Float getDataBounds() {
        float minX = Integer.MAX_VALUE;
        float minY = Integer.MAX_VALUE;
        float maxX = Integer.MIN_VALUE;
        float maxY = Integer.MIN_VALUE;
        for (int i = 0; i < getDataCount(); i++) {
            float x = getX(i);
            float y = getY(i);
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }

        float width = maxX - minX;
        float height = maxY - minY;
        if (equalScaleDimensions) {
            if (width > height) {
                minY -= (width - height) / 2;
                //noinspection SuspiciousNameCombination
                height = width;
            } else if (height > width) {
                minX -= (height - width) / 2;
                //noinspection SuspiciousNameCombination
                width = height;
            }
        }
        return new Rectangle2D.Float(minX, minY, width, height);
    }

    public void showWindow() {
        JFrame frame = new JFrame("FuzzySort");
        frame.setContentPane(this);
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
        new Thread(() -> {
            while (true) {
                this.repaint();
                try {
                    Thread.sleep(30);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    protected abstract void drawData(Graphics2D g, Viewport vp);
    protected abstract int getDataCount();
    protected abstract float getX(int dataIndex);
    protected abstract float getY(int dataIndex);

    public static class Viewport {
        public final Rectangle2D.Float dataBounds;
        public final int w;
        public final int h;

        public Viewport(Rectangle2D.Float dataBounds, int w, int h) {
            this.dataBounds = dataBounds;
            this.w = w;
            this.h = h;
        }

        public Point plotPoint(float x, float y) {
            return new Point(
                    Math.round(padding + w * (x - dataBounds.x) / dataBounds.width),
                    Math.round(padding + h * (y - dataBounds.y) / dataBounds.height)
            );
        }
    }
}
