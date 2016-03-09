package net.whydah.crmservice.profilepicture.model;

public class ProfilePicture {
    private byte[] imageData;
    private String contentType;

    public ProfilePicture(byte[] imageData, String contentType) {
        this.imageData = imageData;
        this.contentType = contentType;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
