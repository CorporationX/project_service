package faang.school.projectservice.service.projectresource;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ImageChecker {
    private static final List<String> IMAGE_MIME_TYPES = Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp"
    );

    public boolean checkResourceTypeIsImage(String contentType) {
        if (contentType == null) {
            return false;
        }
        return IMAGE_MIME_TYPES.contains(contentType.toLowerCase());
    }
}
