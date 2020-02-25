package models;

import java.awt.image.BufferedImage;

public class UploadedImage {
    private BufferedImage image;
    private String text;
    private String username; // User who uploaded

    public UploadedImage(BufferedImage image, String text, String username) {
        this.image = image;
        this.text = text;
        this.username = username;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
