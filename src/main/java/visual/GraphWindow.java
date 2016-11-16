package visual;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

@SuppressWarnings("serial")
public class GraphWindow extends JPanel {

    private static int maxY = 1500;
    private static int maxX = 2100;
    private static final int PREF_W = 1200;
    private static final int PREF_H = 800;
    private static final int BORDER_GAP_X = 20;
    private static final int BORDER_GAP_Y = 20;
    private static final Color GRAPH_COLOR = Color.green;
    private static final Color GRAPH_COLOR2 = Color.red;
    private static final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
    private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
    private static final int GRAPH_POINT_WIDTH = 12;
    private static int yHatchCnt = 15;
    private static int xHatchCnt = 21;
    private static final int INTERVAL = 50;
    private double[][] points;
    private double[][] formula;
    Scanner input = new Scanner(System.in);

    public GraphWindow(Map<Double, Integer> points, Map<Double, Integer> formula) {
        TreeSet<Double> orderset = new TreeSet();
        orderset.addAll(points.keySet());
        this.points = new double[points.size()][2];
        int i = 0;
        for (Double afstand : orderset) {
            this.points[i][1] = afstand*100;
            this.points[i][0] = points.get(afstand);
            maxX=Math.max(maxX, (int)this.points[i][0]);
            maxY=Math.max(maxY, (int)this.points[i][1]);
            i++;
        }
        if (formula != null) {
            orderset = new TreeSet();
            orderset.addAll(formula.keySet());
            this.formula = new double[formula.size()][2];
            i = 0;
            for (Double afstand : orderset) {
                this.formula[i][1] = afstand*100;
                this.formula[i][0] = formula.get(afstand);
                maxX=Math.max(maxX, (int)this.formula[i][1]);
                maxY=Math.max(maxY, (int)this.formula[i][0]);
                i++;
            }
        }
        //changes maxX to add one reletive to max round number.
        System.out.printf("%d,\n%d,\n%d,\n%d,\n-----\n",maxX,maxY,xHatchCnt,yHatchCnt);
        maxX+=Math.pow(10,(maxX+"").length()/2);
        maxX=(int)(maxX/Math.pow(10,(maxX+"").length()/2))*(int)Math.pow(10,(maxX+"").length()/2);
        maxY+=Math.pow(10,(maxY+"").length()/2);
        maxY=(int)(maxY/Math.pow(10,(maxY+"").length()/2))*(int)Math.pow(10,(maxY+"").length()/2);
        xHatchCnt=Integer.parseInt((maxX+"").substring(0,2));
        yHatchCnt=Integer.parseInt((maxY+"").substring(0,2));
        System.out.printf("%d,\n%d,\n%d,\n%d,\n",maxX,maxY,xHatchCnt,yHatchCnt);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - 2 * BORDER_GAP_X) / (maxX - 1);
        double yScale = ((double) getHeight() - 2 * BORDER_GAP_Y) / (maxY - 1);

        //creat Point;
        List<Point> graphPoints = new ArrayList<Point>();
        List<Point> graphPoints2 = null;
        addPoints(xScale, yScale, graphPoints, g2, points);
        if (formula != null) {
            graphPoints2 = new ArrayList<Point>();
            addPoints(xScale, yScale, graphPoints2, g2, formula);
        }

        // create x and y axes 
        g2.drawLine(BORDER_GAP_X, getHeight() - BORDER_GAP_X, BORDER_GAP_X, BORDER_GAP_X);
        g2.drawLine(BORDER_GAP_X, getHeight() - BORDER_GAP_X, getWidth() - BORDER_GAP_X, getHeight() - BORDER_GAP_X);

        // create hatch marks for y axis. 
        for (int i = 0; i < yHatchCnt; i++) {
            int x0 = BORDER_GAP_X;
            int x1 = GRAPH_POINT_WIDTH + BORDER_GAP_X;
            int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP_X * 2)) / yHatchCnt + BORDER_GAP_X);
            int y1 = y0;
            g2.drawLine(x0, y0, x1, y1);
            g2.drawString("" + ((maxY / (double) yHatchCnt) * (i + 1)), 5, y0);
        }

        // and for x axis
        for (int i = 0; i < xHatchCnt; i++) {
            int x0 = (i + 1) * (getWidth() - BORDER_GAP_X * 2) / (xHatchCnt - 1) + BORDER_GAP_X;
            int x1 = x0;
            int y0 = getHeight() - BORDER_GAP_X;
            int y1 = y0 - GRAPH_POINT_WIDTH;
            g2.drawLine(x0, y0, x1, y1);
            g2.drawString("" + ((int) ((maxX / xHatchCnt) * (i + 1))), x0, y0);
        }

        Stroke oldStroke = g2.getStroke();
        g2.setColor(GRAPH_COLOR);
        g2.setStroke(GRAPH_STROKE);
        drawLine(g2, graphPoints, GRAPH_COLOR);
        if (graphPoints2 != null) {
            drawLine(g2, graphPoints2, GRAPH_COLOR2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(GRAPH_POINT_COLOR);
        drawPoints(graphPoints, g2, 0);
        drawPoints(graphPoints2, g2, -1);
    }

    public void drawPoints(List<Point> graphPoints, Graphics2D g2, int dir) {
        for (int i = 0; i < graphPoints.size(); i++) {
            int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
            int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
            int ovalW = GRAPH_POINT_WIDTH;
            int ovalH = GRAPH_POINT_WIDTH;
            g2.fillOval(x, y, ovalW, ovalH);
            AffineTransform defaultAt = g2.getTransform();
            AffineTransform at = AffineTransform.getQuadrantRotateInstance(dir);
            g2.setTransform(at);
            if (dir != 0) {
                g2.drawString(points[i * INTERVAL][1] + " , " + points[i * INTERVAL][0], y * (0 - 1), (x - 20));
            } else {
                g2.drawString(formula[i * INTERVAL][1] + " , " + formula[i * INTERVAL][0], x + 20, y);
            }

        }
    }

    public void drawLine(Graphics2D g2, List<Point> graphPoints2, Color color) {
        g2.setColor(color);
        for (int i = 0; i < graphPoints2.size() - 1; i++) {
            int x1 = graphPoints2.get(i).x;
            int y1 = graphPoints2.get(i).y;
            int x2 = graphPoints2.get(i + 1).x;
            int y2 = graphPoints2.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    public void addPoints(double xScale, double yScale, List<Point> graphPoints, Graphics2D g2, double[][] punten) {

        for (int i = 0; i < punten.length; i += INTERVAL) {
            int x1 = (int) ((punten[i][1]) * xScale + BORDER_GAP_X);
            int y1 = (int) ((maxY - punten[i][0]) * yScale + BORDER_GAP_Y);
            graphPoints.add(new Point(x1, y1));
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PREF_W, PREF_H);
    }

    public static void createAndShowGui(Map<Double, Integer> points) {
        createAndShowGui(points, null);
    }

    public static void createAndShowGui(Map<Double, Integer> points, Map<Double, Integer> formula) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                List<Integer> scores = new ArrayList<Integer>();

                GraphWindow mainPanel = new GraphWindow(points, formula);

                JFrame frame = new JFrame("DrawGraph");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(mainPanel);
                frame.pack();
                frame.setLocationByPlatform(true);
                frame.setVisible(true);
            }
        });
    }

}
