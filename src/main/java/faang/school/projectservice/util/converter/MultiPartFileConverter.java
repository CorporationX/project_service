package faang.school.projectservice.util.converter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Builder
@AllArgsConstructor
public class MultiPartFileConverter implements MultipartFile {

    private byte[] input;
    private String name;
    private String originalFileName;
    private String contentType;

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return input == null || input.length == 0;
    }

    @Override
    public long getSize() {
        return input.length;
    }

    @Override
    @NonNull
    public byte[] getBytes() {
        return input;
    }

    @Override
    @NonNull
    public InputStream getInputStream() {
        return new ByteArrayInputStream(input);
    }

    @Override
    @NonNull
    public Resource getResource() {
        return MultipartFile.super.getResource();
    }

    @Override
    public void transferTo(@NonNull File destination) throws IOException, IllegalStateException {
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(input);
        }
    }
}