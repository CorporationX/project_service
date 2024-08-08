package faang.school.projectservice.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageService {
    private static final String MESSAGE_FAILED_RESIZE_IMAGE = "Failed resize image";
    private static final String IMAGE_FORMAT = "jpg";
    private static final int START_X = 0;
    private static final int START_Y = 0;
    private final int MAX_HEIGHT = 1080;
    private final int MAX_WIDTH = 566;

    private int height;
    private int width;

    public byte[] resizeImage(MultipartFile image) {
        try {
            BufferedImage originalImage = ImageIO.read(image.getInputStream());
            height = originalImage.getHeight();
            width = originalImage.getWidth();
            boolean isSquare = height == width;
            if ((isSquare && height > MAX_HEIGHT) || width > MAX_WIDTH || height > MAX_HEIGHT) {
                getNewSize(isSquare);
            }
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = newImage.createGraphics();
            graphics2D.drawImage(originalImage, START_X, START_Y, width, height, null);
            graphics2D.dispose();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(newImage, IMAGE_FORMAT, outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(MESSAGE_FAILED_RESIZE_IMAGE);
        }
    }

    private void getNewSize(boolean isSquare) {
        if (isSquare) {
            height = width = MAX_HEIGHT;
        } else {
            changeProportionally();
        }
    }

    private void changeProportionally() {
        double coefficient = 0;
        if (height > MAX_HEIGHT && width > MAX_WIDTH) {
            coefficient = getCoefficientIfHeightAndWidthGreater(coefficient);
        } else {
            coefficient = getCoefficientIfOneParameterGreater(coefficient);
        }
        height = (int) (height / coefficient);
        width = (int) (width / coefficient);
    }

    private double getCoefficientIfOneParameterGreater(double coefficient) {
        if (height > MAX_HEIGHT) coefficient = (double) height / MAX_HEIGHT;
        else coefficient = (double) width / MAX_WIDTH;
        return coefficient;
    }

    private double getCoefficientIfHeightAndWidthGreater(double coefficient) {
        if (width > height) coefficient = (double) width / MAX_WIDTH;
        else coefficient = (double) height / MAX_HEIGHT;
        return coefficient;
    }
}
