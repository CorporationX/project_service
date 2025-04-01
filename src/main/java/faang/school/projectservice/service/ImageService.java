package faang.school.projectservice.service;

import faang.school.projectservice.exception.ExceptionMessage;
import faang.school.projectservice.exception.ImageProcessingException;
import faang.school.projectservice.exception.InvalidFileException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ImageService {
    private static final int MAX_WIDTH_HORIZONTAL = 1080;
    private static final int MAX_HEIGHT_HORIZONTAL = 566;
    private static final int MAX_WIDTH_SQUARE = 1080;
    private static final int MAX_HEIGHT_SQUARE = 1080;

    public byte[] resizeImage(MultipartFile multipartFile) throws IOException {
        BufferedImage originalImage = ImageIO.read(multipartFile.getInputStream());

        if (originalImage == null) {
            throw new InvalidFileException(ExceptionMessage.FILE_NOT_VALID);
        }

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();


        int[] newSizes = calculateNewSize(width, height);
        int newWidth = newSizes[0];
        int newHeight = newSizes[1];

        if (newWidth == width && newHeight == height) {
            return multipartFile.getBytes();
        }

        return createResizedImageBytes(multipartFile, originalImage, newWidth, newHeight);
    }

    private byte[] createResizedImageBytes(MultipartFile multipartFile, BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, width, height, null);
        graphics2D.dispose();

        String contentType = Objects.requireNonNull(multipartFile.getContentType()).split("/")[1];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(resizedImage, contentType, outputStream);
        } catch (IOException e) {
            throw new ImageProcessingException(ExceptionMessage.IMAGE_PROCESSING_ERROR);
        }
        return outputStream.toByteArray();
    }

    private int[] calculateNewSize(int width, int height) {
        int newWidth = width;
        int newHeight = height;

        if (width > height) {
            if (width > MAX_HEIGHT_HORIZONTAL) {
                newWidth = MAX_WIDTH_HORIZONTAL;
                newHeight = (height * newWidth) / width;
            }

            if (newHeight > MAX_HEIGHT_HORIZONTAL) {
                newHeight = MAX_HEIGHT_HORIZONTAL;
                newWidth = (width * newHeight) / height;
            }
        } else if (width == height) {
            if (width > MAX_WIDTH_SQUARE) {
                newWidth = MAX_WIDTH_SQUARE;
                newHeight = MAX_HEIGHT_SQUARE;
            }
        }

        return new int[]{newWidth, newHeight};
    }

}
