package faang.school.projectservice.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageResizer {
    public byte[] resizeImage(byte[] image, int width, int height) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(new ByteArrayInputStream(image))
                .size(width, height)
                .outputFormat("jpg")
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }
}
