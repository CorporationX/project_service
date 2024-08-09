package faang.school.projectservice.service.project;

import net.coobird.thumbnailator.Thumbnails;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

@Component
public class ImageResizer {
    public MultipartFile resizeImage(MultipartFile file,
                                     int targetWidth,
                                     int targetHeight) throws IOException {
        Thumbnails.of(file.getInputStream())
                .size(targetWidth, targetHeight)
                .toFile(file.getOriginalFilename());

        return file;
    }

}
