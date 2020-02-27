package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import models.RedactedDocument;
import models.UserCredentials;
import services.NotificationService;
import services.PersistenceService;
import services.SsnRedactionService;

/**
 * Handles image uploading.
 */
public class ImageUploadHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private SsnRedactionService ssnRedactionService;
    private PersistenceService persistenceService;
    private NotificationService notificationService;

    public ImageUploadHandler() {
        this(
                new SsnRedactionService(),
                new PersistenceService(),
                new NotificationService()
        );
    }

    private ImageUploadHandler(SsnRedactionService ssnRedactionService,
                               PersistenceService persistenceService,
                               NotificationService notificationService) {
        this.ssnRedactionService = ssnRedactionService;
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
     * @param request Body contains uploaded image as byte array
     * @param context {@link Context} object
     * @return Message indicating upload status.
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        RedactedDocument redactedDocument = ssnRedactionService
                .redact(request.getBody())
                .withUserCredentials(new UserCredentials("zach", "555-555-5555")); // context.getIdentity().getIdentityId()
        persistenceService.save(redactedDocument);
        if (!redactedDocument.getRedactedSsnList().isEmpty()) {
            notificationService.sendNotification(); // send two msgs for each redacted ssn
        }
        return new APIGatewayProxyResponseEvent().withBody("Success.").withStatusCode(200);
    }
}
