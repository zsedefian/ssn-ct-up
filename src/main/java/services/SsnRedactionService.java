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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Redacts any blocks that contains a social security number from an image and its OCR'd text.
 */
public class SsnRedactionService {

    private static final String SSN = "^\\d{3}-\\d{2}-(\\d{4}$)";
    private static final Pattern SSN_PATTERN = Pattern.compile("^\\d{3}-\\d{2}-\\d{4}$");
    private static final String REPLACEMENT_TEXT = "***";

    private final AmazonTextract textract;

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
        String encodedImg = input.split(",")[1];
        byte[] decodedImg = Base64.getDecoder().decode(encodedImg.getBytes(StandardCharsets.UTF_8));

        List<Block> wordBlocks = detectWordBlocks(decodedImg);
        List<Block> ssnBlocks = wordBlocks.stream()
                .filter(block -> SSN_PATTERN.matcher(block.getText()).matches())
                .collect(Collectors.toList());

        BufferedImage redactedImage = redactFromImage(ssnBlocks, decodedImg);
        String fullText = wordBlocks.stream().map(Block::getText).collect(Collectors.joining(" "));
        System.out.println(fullText);
        String redactedText = SSN_PATTERN.matcher(fullText).replaceAll(REPLACEMENT_TEXT);
        List<String> redactedSsnList = ssnBlocks.stream()
                .map(block -> block.getText().substring(7))
                .collect(Collectors.toList());
        String mimeType = input.substring(input.indexOf(":") + 1, input.indexOf(";")); // e.g. data:image/png;base64
        return new RedactedDocument(redactedImage, redactedText, redactedSsnList, mimeType);
    }

    private List<Block> detectWordBlocks(byte[] bytes) {
        Document document = new Document().withBytes(ByteBuffer.wrap(bytes));
        DetectDocumentTextRequest detectDocumentTextRequest = new DetectDocumentTextRequest()
                .withDocument(document);
        return textract
                .detectDocumentText(detectDocumentTextRequest)
                .getBlocks()
                .stream()
                .filter(block -> block.getBlockType().equals(BlockType.WORD.toString()))
                .collect(Collectors.toList());
    }

    private BufferedImage redactFromImage(List<Block> ssnBlocks, byte[] decodedImg) {
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(decodedImg));
            ssnBlocks.forEach(block -> {
                Graphics2D graph = img.createGraphics();
                graph.setColor(Color.BLACK);
                graph.fill(createRedactionBox(img, block.getGeometry().getBoundingBox()));
                graph.dispose();
            });
            return img;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Rectangle createRedactionBox(BufferedImage img, BoundingBox boundingBox) {
        int width = img.getWidth();
        int height = img.getHeight();
        int x1 = (int) (boundingBox.getLeft() * width);
        int y1 = (int) (boundingBox.getTop() * height);
        int x2 = (int) (boundingBox.getWidth() * width);
        int y2 = (int) (boundingBox.getHeight() * height);
        return new Rectangle(x1, y1, x2, y2);
    }
}
