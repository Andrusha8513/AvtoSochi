package ru.avtoAra.AvtoSochi.users.DTO;

public class ImageDto {
    private Long id;
    private boolean isPreviewImage;
    private String originalFileName;

    public ImageDto(Long id,
                    boolean isPreviewImage,
                    String originalFileName) {
        this.id = id;
        this.isPreviewImage = isPreviewImage;
        this.originalFileName = originalFileName;
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public boolean isPreviewImage() {
        return isPreviewImage;
    }
    public void setPreviewImage(boolean previewImage) {
        isPreviewImage = previewImage;
    }
    public String getOriginalFileName() {
        return originalFileName;
    }
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
}
