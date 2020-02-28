package services;

import models.RedactedDocument;
import repositories.DynamoWriter;
import repositories.S3Writer;

public class PersistenceService {

    private final S3Writer s3Writer;
    private final DynamoWriter dynamoWriter;

    public PersistenceService() {
        this(
                new S3Writer(),
                new DynamoWriter()
        );
    }

    public PersistenceService(S3Writer s3Writer, DynamoWriter dynamoWriter) {
        this.s3Writer = s3Writer;
        this.dynamoWriter = dynamoWriter;
    }

    /**
     * Persists the redacted document.
     * Uploads the image itself to S3, while its metadata and redacted text is stored in DocumentDB.
     *
     * @param redactedDocument Image and text which have been redacted.
     */
    public void save(RedactedDocument redactedDocument) {
        s3Writer.save(redactedDocument);
        dynamoWriter.save(redactedDocument);
    }
}
