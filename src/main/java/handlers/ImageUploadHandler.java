package handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.textract.model.*;
import models.UploadedImage;
import services.RedactionService;
import services.PersistenceService;
import services.SsnDetectionService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static services.SsnDetectionService.SSN_PATTERN;

/**
 * Handles image uploading.
 */
public class ImageUploadHandler implements RequestHandler<Document, String> {

    private static final String REPLACEMENT_TEXT = "***";
    private PersistenceService persistenceService;
    private RedactionService redactionService;
    private SsnDetectionService ssnDetectionService;

    public ImageUploadHandler() {
        this(
                new SsnDetectionService(),
                new PersistenceService(),
                new RedactionService()
        );
    }

    private ImageUploadHandler(SsnDetectionService ssnDetectionService,
                               PersistenceService persistenceService,
                               RedactionService redactionService) {
        this.ssnDetectionService = ssnDetectionService;
        this.persistenceService = persistenceService;
        this.redactionService = redactionService;
    }

    /**
     * Handles image uploading.
     * First runs OCR analysis on uploaded image.
     * If image contains a social security number (SSN), that portion of the image and text will be redacted.
     * If an SSN is found, an SMS message will be sent to a set of individuals to notify them of the occurrence.
     * The image and text are then persisted if their redacted form in DocumentDB.
     *
     * @param document Image
     * @param context  {@link Context} object
     * @return Message indicating upload status.
     */
    @Override
    public String handleRequest(Document document, Context context) {
        try {
            List<Block> ssnBlocks = ssnDetectionService.detectSsnBlocks(document);

            // Redact from image
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(document.getBytes().array()));
            ssnBlocks.forEach(ssnBlock -> redactionService.redactImage(img, ssnBlock));

            // Redact from text
            String text = ssnBlocks.stream()
                    .filter(block -> block.getBlockType().equals(BlockType.WORD.toString()))
                    .map(block -> SSN_PATTERN.matcher(block.getText()).matches() ? REPLACEMENT_TEXT : block.getText())
                    .collect(Collectors.joining(" "));

            // Persist redacted data
            persistenceService.persistImage(new UploadedImage(img, text, context.getIdentity().getIdentityId());

            // Send SMS to notify interested parties


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Uploaded image.";
    }
}
