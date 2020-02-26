package models;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.UUID;

public class RedactedDocument {
    private String id;
    private BufferedImage image;
    private String text;
    private List<String> redactedSsnList;
    private String fileExtension;
    private String uploaderId;

    public RedactedDocument(BufferedImage image,
                            String text,
                            List<String> redactedSsnList,
                            String fileExtension,
                            String uploaderId) {
        this.image = image;
        this.text = text;
        this.redactedSsnList = redactedSsnList;
        this.fileExtension = fileExtension;
        this.id = "document/" + UUID.randomUUID() + "." + fileExtension;
        this.uploaderId = uploaderId;
    }

    public String getId() {
        return id;
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

    public String getFileExtension() {
        return fileExtension;
    }

    public String getUploaderId() {
        return uploaderId;
    }
}
