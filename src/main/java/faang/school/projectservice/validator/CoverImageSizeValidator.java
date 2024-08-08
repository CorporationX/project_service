package faang.school.projectservice.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CoverImageSizeValidator {

    @Value("${coverImage.maxsize}")
    private long maxImageSize;

    public void validateSize(MultipartFile file) {
        if (file.getSize() > maxImageSize) {
            long maxSize = maxImageSize / 1_000_000;
            throw new RuntimeException("File " + file.getOriginalFilename() + " should be max " + maxSize + " mb");
        }
    }
}
