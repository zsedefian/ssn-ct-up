package services;

import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.BoundingBox;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Redacts any block from an image by painting a black rectangle over it.
 */
public class ImageRedactionService {

    /**
     * Redacts a block from an image by painting a black rectangle over it.
     *
     * @param img Image to redact blocks fro
     * @param block Blocks which will be redacted (i.e., replaced by a black rectangle) from the image.
     */
    public void redact(BufferedImage img, Block block) {
        Coordinates coordinates = calculateCoordinates(img, block.getGeometry().getBoundingBox());
        Graphics2D graph = img.createGraphics();
        graph.setColor(Color.BLACK);
        graph.fill(new Rectangle(coordinates.x1, coordinates.y1, coordinates.x2, coordinates.y2));
        graph.dispose();
    }

    private Coordinates calculateCoordinates(BufferedImage img, BoundingBox boundingBox) {
        int width = img.getWidth();
        int height = img.getHeight();
        Coordinates coordinates = new Coordinates();
        coordinates.x1 = (int) (boundingBox.getLeft() * width);
        coordinates.y1 = (int) (boundingBox.getTop() * height);
        coordinates.x2 = (int) (coordinates.x1 + (boundingBox.getWidth() * width));
        coordinates.y2 = (int) (coordinates.y1 + (boundingBox.getHeight() * height));
        return coordinates;
    }

    private static class Coordinates {
        int x1;
        int y1;
        int x2;
        int y2;
    }
}
