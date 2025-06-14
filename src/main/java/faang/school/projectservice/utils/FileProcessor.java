package faang.school.projectservice.utils;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;

@Slf4j
@Component
public class FileProcessor {
    @Value("${team-avatar-file.maxDimension}")
    private int avatarMaxDimension;

    public File resizeImage(MultipartFile file) {
        try {
            File outputFile = new File(file.getOriginalFilename());
            Thumbnails.of(file.getInputStream())
                .size(avatarMaxDimension, avatarMaxDimension)
                .keepAspectRatio(true)
                .toFile(outputFile);
            return outputFile;
        } catch (IOException e) {
            log.error("Error while resizing a fiele, {}.", e.getMessage());
            throw new RuntimeException(String.format("Error while resizing a file, %s.", e.getMessage()));
        }
    }
}
