package faang.school.projectservice.service.impl;

import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.service.ImageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProcessorImpl implements ImageProcessor {
    @Value("${app.cover_max_size}")
    @Setter
    private Integer coverMaxSize;

    @Override
    public BufferedImage resizeImage(MultipartFile file) {
        BufferedImage bufferedImage;

        if (file == null) {
            log.error("File is null.");
            throw new FileException("File is null.");
        }
        try {
            double ratio = 0.0;
            bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage.getWidth() > coverMaxSize || bufferedImage.getHeight() > coverMaxSize) {
                if (bufferedImage.getWidth() > bufferedImage.getHeight()) {
                    ratio = (double) coverMaxSize / bufferedImage.getWidth();
                } else {
                    ratio = (double) coverMaxSize / bufferedImage.getHeight();
                }
                int widthNew = (int) (ratio * bufferedImage.getWidth());
                int heightNew = (int) (ratio * bufferedImage.getHeight());
                return resizeToNewImage(bufferedImage, widthNew, heightNew);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException(e.getMessage());
        }
        return bufferedImage;
    }

    public InputStream convertImageToInputStream(BufferedImage image, String contentType) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String fileType = contentType.substring(contentType.lastIndexOf("/") + 1);
        try {
            ImageIO.write(image, fileType, outputStream);
            outputStream.flush();
            byte[] imageByte = outputStream.toByteArray();
            outputStream.close();
            return new ByteArrayInputStream(imageByte);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException(e.getMessage());
        }
    }

    private BufferedImage resizeToNewImage(BufferedImage bufferedImage, int width, int height) {
        Image resultingImage = bufferedImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        log.info("Image resized.");
        return outputImage;
    }
}
