package repositories;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import models.RedactedDocument;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class S3Repository {
    private static final Regions REGION = Regions.DEFAULT_REGION;
    private static final String BUCKET_NAME = ""; // TODO get bucket name

    private AmazonS3 s3Client;

    public S3Repository() {
        this(AmazonS3ClientBuilder.standard().withRegion(REGION).build());
    }

    public S3Repository(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public void save(RedactedDocument redactedDocument) {
        try {
            InputStream inputStream = createInputStream(redactedDocument);
            PutObjectRequest request = new PutObjectRequest(
                    BUCKET_NAME, redactedDocument.getId(), inputStream, getMetadata()
            );
            System.out.println("Saving image to S3...");
            s3Client.putObject(request);
            System.out.println("Successfully saved image to S3 bucket.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream createInputStream(RedactedDocument redactedDocument) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(redactedDocument.getImage(),"png", outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private ObjectMetadata getMetadata() {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/png");
        metadata.addUserMetadata("x-amz-meta-title", "redacted_image");
        return metadata;
    }
}
