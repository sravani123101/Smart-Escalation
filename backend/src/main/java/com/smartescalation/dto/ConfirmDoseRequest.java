package com.smartescalation.dto;

public class ConfirmDoseRequest {
    private boolean withPhoto;
    private String photoPath;

    public ConfirmDoseRequest() {}

    public ConfirmDoseRequest(boolean withPhoto, String photoPath) {
        this.withPhoto = withPhoto;
        this.photoPath = photoPath;
    }

    public boolean isWithPhoto() { return withPhoto; }
    public void setWithPhoto(boolean withPhoto) { this.withPhoto = withPhoto; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}
