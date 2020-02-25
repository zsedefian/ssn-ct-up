package services;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PersistenceService {

    private static final Regions REGION = Regions.DEFAULT_REGION;
    private static final String BUCKET_NAME = ""; // TODO

    private AmazonS3 s3Client;

    public PersistenceService() {
        this.s3Client = AmazonS3ClientBuilder.standard().withRegion(REGION).build();
    }

    public void persistImage(BufferedImage image) {
        String key = UUID.randomUUID().toString();
        persistToS3(image, key);
        persistToDocumentDb(key);
    }

    private void persistToS3(BufferedImage image, String key) {
        try {
            File tempFile = File.createTempFile("redacted", "tmp");
            ImageIO.write(image, "PNG", tempFile);
            PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, key, tempFile);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/png");
            metadata.addUserMetadata("x-amz-meta-title", "redacted_image");
            request.setMetadata(metadata);
            s3Client.putObject(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void persistToDocumentDb(String key) {

    }
}
