package faang.school.projectservice.config.minio;

public enum ImageFormat {
    JPG("jpg"),
    PNG("png");

    private final String format;

    ImageFormat(String format) {
        this.format = format;
    }

    public static ImageFormat fromContentType(String contentType) {
        if (contentType.equalsIgnoreCase("image/png")) {
            return PNG;
        } else if (contentType.equalsIgnoreCase("image/jpeg")
                || contentType.equalsIgnoreCase("image/jpg")) {
            return JPG;
        }
        throw new IllegalArgumentException("Unsupported image format: " + contentType);
    }

    public String getFormat() {
        return format;
    }
}