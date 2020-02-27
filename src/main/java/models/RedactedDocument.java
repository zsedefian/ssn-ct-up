package models;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

public class RedactedDocument {
    private String objectKey;
    private BufferedImage image;
    private String text;
    private List<String> redactedSsnList;
    private String mimeType;
    private String uploaderId;

    public RedactedDocument(BufferedImage image,
                            String text,
                            List<String> redactedSsnList,
                            String mimeType) {
        this.image = image;
        this.text = text;
        this.redactedSsnList = redactedSsnList;
        this.mimeType = mimeType;
        this.objectKey = "document/" + UUID.randomUUID() + "." + mimeType;
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

    public String getUploaderId() {
        return uploaderId;
    }

    public RedactedDocument withUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
        return this;
    }
}
