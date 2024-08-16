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

    private final ThreadLocal<Integer> heightThread = new ThreadLocal<>();
    private final ThreadLocal<Integer> widthThread = new ThreadLocal<>();

    public byte[] resizeImage(MultipartFile image) {
        try {
            BufferedImage originalImage = ImageIO.read(image.getInputStream());
            heightThread.set(originalImage.getHeight());
            widthThread.set(originalImage.getWidth());
            boolean isSquare = heightThread.get().equals(widthThread.get());
            if ((isSquare && heightThread.get() > MAX_HEIGHT) ||
                    (!isSquare && (widthThread.get() > MAX_WIDTH || heightThread.get() > MAX_HEIGHT))) {
                getNewSize(isSquare);
            }
            BufferedImage newImage = new BufferedImage(
                    widthThread.get(),
                    heightThread.get(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = newImage.createGraphics();
            graphics2D.drawImage(originalImage, START_X, START_Y, widthThread.get(), heightThread.get(), null);
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
            heightThread.set(MAX_HEIGHT);
            widthThread.set(MAX_HEIGHT);
        } else {
            changeProportionally();
        }
    }

    private void changeProportionally() {
        double coefficient;
        if (heightThread.get() > MAX_HEIGHT && widthThread.get() > MAX_WIDTH) {
            coefficient = getCoefficientIfHeightAndWidthGreater();
        } else {
            coefficient = getCoefficientIfOneParameterGreater();
        }
        heightThread.set((int) (heightThread.get() / coefficient));
        widthThread.set((int) (widthThread.get() / coefficient));
    }

    private double getCoefficientIfOneParameterGreater() {
        double coefficient;
        if (heightThread.get() > MAX_HEIGHT) coefficient = (double) heightThread.get() / MAX_HEIGHT;
        else coefficient = (double) widthThread.get() / MAX_WIDTH;
        return coefficient;
    }

    private double getCoefficientIfHeightAndWidthGreater() {
        double coefficient;
        if (widthThread.get() > heightThread.get()) coefficient = (double) widthThread.get() / MAX_WIDTH;
        else coefficient = (double) heightThread.get() / MAX_HEIGHT;
        return coefficient;
    }
}
