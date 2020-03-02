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

import java.util.Map;

/**
 * Handles image uploading.
 */
public class ImageUploadHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final SsnRedactionService ssnRedactionService;
    private final PersistenceService persistenceService;
    private final NotificationService notificationService;

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
                .withUserCredentials(new UserCredentials("zach", "+15555555555")); // context.getIdentity().getIdentityId()
        persistenceService.save(redactedDocument);
        if (!redactedDocument.getRedactedSsnList().isEmpty()) {
            notificationService.sendNotifications(redactedDocument);
        }
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withHeaders(Map.of("Access-Control-Allow-Origin", "*"))
                .withBody("Success.");
    }
}
