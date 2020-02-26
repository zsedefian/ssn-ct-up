package handlers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.textract.model.Document;
import models.RedactedDocument;
import services.NotificationService;
import services.PersistenceService;
import services.RedactionService;

/**
 * Handles image uploading.
 */
public class ImageUploadHandler implements RequestHandler<Document, String> {

    private RedactionService redactionService;
    private PersistenceService persistenceService;
    private NotificationService notificationService;

    public ImageUploadHandler() {
        this(
                new RedactionService(),
                new PersistenceService(),
                new NotificationService()
        );
    }

    private ImageUploadHandler(RedactionService redactionService,
                               PersistenceService persistenceService,
                               NotificationService notificationService) {
        this.redactionService = redactionService;
        this.persistenceService = persistenceService;
        this.notificationService = notificationService;
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
        RedactedDocument redactedDocument = redactionService.redactTextAndImage(document);
        persistenceService.save(redactedDocument);
        notificationService.sendNotification();
        return "Uploaded image.";
    }
}
