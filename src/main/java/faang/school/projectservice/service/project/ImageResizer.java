package faang.school.projectservice.service.project;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Component
public class ImageResizer {
    public BufferedImage resizeImage(BufferedImage inputImage,
                                     int targetWidth,
                                     int targetHeight) {

        return Scalr.resize(inputImage, targetWidth, targetHeight);
    }

}
