package models;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

/**
 * Models a redacted image and related metadata as well as its redacted, OCR'd text.
 */
public class RedactedDocument {
    private final String objectKey;
    private final BufferedImage image;
    private final String text;
    private final List<String> redactedSsnList;
    private final String mimeType;
    private UserCredentials userCredentials;

    public RedactedDocument(BufferedImage image,
                            String text,
                            List<String> redactedSsnList,
                            String mimeType) {
        this.objectKey = "document/" + UUID.randomUUID() + "." + mimeType;
        this.image = image;
        this.text = text;
        this.redactedSsnList = redactedSsnList;
        this.mimeType = mimeType;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public List<String> getRedactedSsnList() {
        return redactedSsnList;
    }

    public String getMimeType() {
        return mimeType;
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public RedactedDocument withUserCredentials(UserCredentials $paramName) {
        userCredentials = $paramName;
        return this;
    }
}
