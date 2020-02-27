package services;

import models.RedactedDocument;
import repositories.DynamoRepository;
import repositories.S3Repository;

public class PersistenceService {

    private final S3Repository s3Repository;
    private final DynamoRepository dynamoRepository;

    public PersistenceService() {
        this(
                new S3Repository(),
                new DynamoRepository()
        );
    }

    public PersistenceService(S3Repository s3Repository, DynamoRepository dynamoRepository) {
        this.s3Repository = s3Repository;
        this.dynamoRepository = dynamoRepository;
    }

    /**
     * Persists the redacted document.
     * Uploads the image itself to S3, while its metadata and redacted text is stored in DocumentDB.
     *
     * @param redactedDocument Image and text which have been redacted.
     */
    public void save(RedactedDocument redactedDocument) {
        s3Repository.save(redactedDocument);
        dynamoRepository.save(redactedDocument);
    }
}
