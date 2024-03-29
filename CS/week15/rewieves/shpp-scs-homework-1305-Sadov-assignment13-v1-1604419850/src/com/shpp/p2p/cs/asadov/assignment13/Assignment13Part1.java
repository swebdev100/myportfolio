package com.shpp.p2p.cs.asadov.assignment13;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * This program get name of image file to scan and return how many silhouettes in it or -1 if errors.
 */

public class Assignment13Part1 {

    /** Precision for colors comparison */
    private static final int COLOR_PRECISION = 40;
    /** Proportion of trash to the largest object */
    private static final double SIZE_PROPORTION = 7.0;
    /** Distance between vertices in pixels */
    private static final int DELTA = 1;

    /** File to read */
    private final String fileName;
    /** Image to scan */
    private BufferedImage image;
    /** Array to mark tested pixels */
    private boolean[][] img;
    /** Dimensions of image */
    private int width, height;
    /** Accumulator to calculate size of graph */
    private int graphSize;
    /** Color of background */
    private int bgColor;
    /** Sizes of graphs */
    private ArrayList<Integer> sizes;

    /**
     * Create instance of scanner and start it.
     * @param args arg[0] is a file name
     */
    public static void main(String args[]) {
        String fileName = args.length > 0 ? args[0] : "test.jpg";
        Assignment13Part1 app = new Assignment13Part1(fileName);
        app.run();
    }

    /**
     * Constructor
     * @param fileName file to load
     */
    public Assignment13Part1(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Read file, set initial parameters,
     * scan image and print number of silhouettes.
     */
    private void run() {
        readFile();
        width = image.getWidth();
        height = image.getHeight();
        img = new boolean[height][width];
        sizes = new ArrayList<>();

        // try to determine background color
        bgColor = findBgColor();

        scanImage();
        System.out.println(getResultFromTrash());
    }

    /**
     * Try to determine bg color
     * Finds color that is most often located near the top and bottom of image.
     * @return background color
     */
    private int findBgColor() {
        int point1 = 0, num1 = 0, num2 = 0;
        int point2 = image.getRGB(DELTA, DELTA);
        for (int col = 0; col < width; col += DELTA) {
            int tmp = image.getRGB(col, DELTA);
            if (isColorSimilar(point2, tmp))
                num2++;
            else {
                if (point1 == 0)
                    point1 = tmp;
                num1++;
            }
        }
        if (num1 > num2)
            return point1;
        else
            return point2;
    }

    /**
     * Scans image for start of graph and starts breadth-first search
     */
    public void scanImage() {
        for (int row = 0; row < height; row += DELTA)
            for (int col = 0; col < width; col += DELTA) {
                img[row][col] = true;                       // mark as visited
                if (isPointBg(col, row))                    // color similar to background
                    continue;
                graphSize = 0;
                scanPointsBFS(col, row);                     //starts breadth-first search
                if (graphSize != 0)                         // after the end, adds size of graph
                    sizes.add(graphSize);
            }
    }

    /**
     * Gets result from trash
     * Trash may be in SIZE_PROPORTION less than largest silhouette
     * @return number of silhouettes
     */
    protected int getResultFromTrash() {
        if (sizes.size() == 0)
            return 0;
        Collections.sort(sizes);
        Collections.reverse(sizes);
        int silhouettes = 0;
        int maxSize = sizes.get(0);
        for (int s : sizes)
            if (maxSize / (double) s < SIZE_PROPORTION)
                silhouettes++;
            else
                break;
        return silhouettes;
    }

    /**
     * Test points in DELTA surround and scan not visited vertex with BFS algorithm
     * @param col x coordinate of start point
     * @param row y coordinate of start point
     */
    protected void scanPointsBFS(int col, int row) {
        LinkedList<Point> points = new LinkedList<>();                  // queue for points
        points.add(new Point(col, row));

        while (true) {
            // ?if change to poolLast it will be DFS?
            Point p = points.pollFirst();                              // get first point from queue
            if (p != null) {                                           // if queue not empty continue find
                col = (int)p.getX();
                row = (int)p.getY();
            } else                                                     // else break find
                break;

            for (int c = col - DELTA; c <= col + DELTA; c += DELTA)
                for (int r = row - DELTA; r <= row + DELTA; r += DELTA) {
                    if (c <= 0 || r <= 0 || c >= width - 1 ||
                            r >= height - 1 || img[r][c])
                        continue;
                    img[r][c] = true;                          // mark as visited
                    if (!isPointBg(c, r)) {                    // color not similar to background = it is vertex
                        graphSize++;
                        points.add(new Point(c, r));           // add point to queue
                    }
                }
        }
    }

    /**
     * Tests if color of point is similar to background color
     * @param col x coordinate of point
     * @param row y coordinate of point
     * @return result
     */
    protected boolean isPointBg(int col, int row) {
        int p = image.getRGB(col, row);
        return isColorSimilar(p, bgColor) || testPointIfEdge(col, row);
    }

    /**
     * Comparison of two colors
     * @param point1 RGB value of point1
     * @param point2 RGB value of point2
     * @return result of comparison
     */
    protected boolean isColorSimilar(int point1, int point2) {
        int a1 = (point1 >> 24) & 0xff, r1 = (point1 >> 16) & 0xff, g1 = (point1 >> 8) & 0xff, b1 = point1 & 0xff;
        int a2 = (point2 >> 24) & 0xff, r2 = (point2 >> 16) & 0xff, g2 = (point2 >> 8) & 0xff, b2 = point2 & 0xff;
        return (Math.abs(a1 - a2) < COLOR_PRECISION) && (Math.abs(r1 - r2) < COLOR_PRECISION)
                && (Math.abs(g1 - g2) < COLOR_PRECISION) && (Math.abs(b1 - b2) < COLOR_PRECISION);
    }

    /**
     * Test if point is on the edge
     * @param row y coordinate of point
     * @param col x coordinate of point
     * @return true if point has < 8 close points with similar color
     */
    protected boolean testPointIfEdge(int col, int row) {
        int p = image.getRGB(col, row), counter = 0;
        for (int c = col-1; c <= col + 1; c++)
            for (int r = row-1; r <= row + 1; r++)
                if (r >= 0 && c >= 0 && c < width && r < height) {
                    if(isColorSimilar(image.getRGB(c, r), p))
                        counter++;
                } else
                    counter++;
        return counter < 9; // 8 + center point
    }

    /**
     * Reads file of image to image buffer
     */
    private void readFile() {
        try {
            image = ImageIO.read(new File(fileName));
        } catch (IOException e) {
            System.out.println("-1");
            System.exit(1);
        }
    }
}
