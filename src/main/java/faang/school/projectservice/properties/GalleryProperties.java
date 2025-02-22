package faang.school.projectservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gallery.upload")
@Data
public class GalleryProperties {
    private String maxFileSize;
    private Integer maxImages;

    public long getMaxFileSizeGalleryInBytes() {
        return parseSizeToBytes(maxFileSize);
    }

    private long parseSizeToBytes(String size) {
        size = size.trim().toUpperCase();

        long multiplier;
        if (size.endsWith("GB")) {
            multiplier = 1024 * 1024 * 1024;
            size = size.replace("GB", "").trim();
        } else if (size.endsWith("MB")) {
            multiplier = 1024 * 1024;
            size = size.replace("MB", "").trim();
        } else if (size.endsWith("KB")) {
            multiplier = 1024;
            size = size.replace("KB", "").trim();
        } else {
            throw new IllegalArgumentException("Invalid size format");
        }

        return Long.parseLong(size) * multiplier;
    }
}
