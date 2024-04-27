package faang.school.projectservice.config.context;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
public class ImageUploadConfig {

    @Value("${image.upload.maxFileSize}")
    private Long maxFileSize;

    @Value("${image.upload.landscape.maxWidth}")
    private int maxWidthLandscape;

    @Value("${image.upload.landscape.maxHeight}")
    private int maxHeightLandscape;

    @Value("${image.upload.square.maxWidth}")
    private int maxWidthSquare;

    @Value("${image.upload.square.maxHeight}")
    private int maxHeightSquare;
}
