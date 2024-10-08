package faang.school.projectservice.validator.util.image;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class MultipartImage implements MultipartFile {
    private final BufferedImage image;
    private final byte[] bytes;
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final String imageType;
    private final boolean isEmpty;
    private final long size;

    public MultipartImage(BufferedImage image, String name, String originalFilename, String contentType)
            throws IOException {
        this.image = image;
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.imageType = StringUtils.substringAfter(contentType, "/");
        this.bytes = getImageBytes(image, imageType);
        this.size = bytes.length;
        this.isEmpty = false;
    }

    private byte[] getImageBytes(BufferedImage image, String imageType) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, imageType, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return isEmpty;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(File dest) {
        // TODO Auto-generated method stub
    }
}
