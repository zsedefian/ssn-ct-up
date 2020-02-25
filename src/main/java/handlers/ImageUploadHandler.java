package handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.*;
import models.UploadedImage;
import services.ImageRedactionService;
import services.PersistenceService;
import services.SsnDetectionService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Handles image uploading.
 */
public class ImageUploadHandler implements RequestHandler<Document, String> {

    private PersistenceService persistenceService;
    private ImageRedactionService imageRedactionService;
    private SsnDetectionService ssnDetectionService;

    public ImageUploadHandler() {
        this(
                new SsnDetectionService(),
                new PersistenceService(),
                new ImageRedactionService()
        );
    }

    private ImageUploadHandler(SsnDetectionService ssnDetectionService,
                               PersistenceService persistenceService,
                               ImageRedactionService imageRedactionService) {
        this.ssnDetectionService = ssnDetectionService;
        this.persistenceService = persistenceService;
        this.imageRedactionService = imageRedactionService;
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
            ssnBlocks.forEach(ssnBlock -> imageRedactionService.redact(img, ssnBlock));

            // Redact from text
            List<String> ssn = ssnBlocks.stream()
                    .map(block -> block.getText().equals("SSN") ? "***" : block.getText())
                    .collect(Collectors.toList());

            // Persist redacted data
            persistenceService.persistImage(new UploadedImage(img, text, context.getIdentity().getIdentityId());

            // Send SMS to notify interested parties


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Uploaded image.";
    }
}
