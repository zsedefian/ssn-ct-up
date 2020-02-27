package services;

import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.*;
import models.RedactedDocument;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Redacts any blocks that contains a social security number from an image and its OCR'd text.
 */
public class SsnRedactionService {

    private static final Pattern SSN_PATTERN = Pattern.compile("^\\d{3}-\\d{2}-(\\d{4}$)");
    private static final String REPLACEMENT_TEXT = "***";

    private AmazonTextract textract;

    public SsnRedactionService() {
        this(AmazonTextractClientBuilder.defaultClient());
    }

    public SsnRedactionService(AmazonTextract textract) {
        this.textract = textract;
    }

    /**
     * Uses Amazon Textract to execute OCR on the uploaded image.
     * The extracted text blocks are then filtered for WORD block type.
     * If a block's text matches the SSN regular expression, the block's coordinates are calculated and a black box
     * is drawn over its location in the image, and its text is changed to {@link this#REPLACEMENT_TEXT}.
     * The method also tracks the SSNs that were redacted.
     *
     * @param input Base64-encoded String representation of image to be redacted
     * @return {@link RedactedDocument} containing the modified image and its redacted text.
     */
    public RedactedDocument redact(String input) {
        try {
            byte[] bytes = Base64.getDecoder().decode(input.split(",")[1]);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            BufferedImage img = ImageIO.read(inputStream);
            StringBuilder text = new StringBuilder();
            List<String> redactedItems = new ArrayList<>();
            for (Block block : detectBlocks(bytes)) {
                if (block.getBlockType().equals(BlockType.WORD.toString())) {
                    Matcher ssnMatcher = SSN_PATTERN.matcher(block.getText());
                    if (ssnMatcher.matches()) {
                        redactFromImage(img, block);
                        text.append(REPLACEMENT_TEXT);
                        redactedItems.add(ssnMatcher.group(1)); // Last four digits of SSN.
                    } else { // Nothing to redact, add the actual text
                        text.append(block.getText());
                    }
                    text.append(" ");
                }
            }
            String mimeType = input.substring(input.indexOf(":") + 1, input.indexOf(";"));
            return new RedactedDocument(img, text.toString(), redactedItems, mimeType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Block> detectBlocks(byte[] bytes) {
        Document document = new Document().withBytes(ByteBuffer.wrap(bytes));
        DetectDocumentTextRequest detectDocumentTextRequest = new DetectDocumentTextRequest()
                .withDocument(document);
        return textract
                .detectDocumentText(detectDocumentTextRequest)
                .getBlocks();
    }

    /**
     * Redacts a block from an image by painting a black rectangle over it.
     * Mutates the image parameter.
     *
     * @param img Image to redact blocks from
     * @param block Blocks which will be redacted (i.e., replaced by a black rectangle) from the image.
     */
    private void redactFromImage(BufferedImage img, Block block) {
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
