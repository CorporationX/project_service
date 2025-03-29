package faang.school.projectservice.filter;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Data
public class CustomMultipartFile implements MultipartFile {
    private final byte[] content;
    private final String name;
    private final String originalFilename;
    private final String contentType;

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() {
        return content;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
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
    public void transferTo(File dest) throws IOException {
        // Записываем содержимое в файл
        try (InputStream inputStream = getInputStream()) {
            Files.copy(inputStream, dest.toPath());
        }
    }
}

