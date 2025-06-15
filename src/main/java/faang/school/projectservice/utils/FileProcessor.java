package faang.school.projectservice.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@UtilityClass
public class FileProcessor {
    private static final int AVATAR_MAX_DEMINSION = 50;

    public File resizeImage(MultipartFile file) {
        log.info("Max picture size is: {}", AVATAR_MAX_DEMINSION);
        try {
            File outputFile = new File(file.getOriginalFilename());
            Thumbnails.of(file.getInputStream())
                .size(AVATAR_MAX_DEMINSION, AVATAR_MAX_DEMINSION)
                .keepAspectRatio(true)
                .toFile(outputFile);
            return outputFile;
        } catch (IOException e) {
            log.error("Error while resizing a fiele, {}.", e.getMessage());
            throw new RuntimeException(String.format("Error while resizing a file, %s.", e.getMessage()));
        }
    }
}
