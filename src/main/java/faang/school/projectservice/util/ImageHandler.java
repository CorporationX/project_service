package faang.school.projectservice.util;

import lombok.experimental.UtilityClass;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UtilityClass
public class ImageHandler {

    public byte[] changeSize(BufferedImage bufferedImage, String imageFormat, int maxResolution) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(bufferedImage)
                .size(maxResolution, maxResolution)
                .outputQuality(1.0)
                .outputFormat(imageFormat)
                .toOutputStream(outputStream);
        return outputStream.toByteArray();
    }

}
