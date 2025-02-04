package faang.school.projectservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {

    public static MultipartFile convertToMultipartFile(byte[] fileBytes, String fileName, String contentType) {
        return new ByteArrayMultipartFile(fileName, fileBytes, contentType);
    }
}

@RequiredArgsConstructor
class ByteArrayMultipartFile implements MultipartFile {
    private final String fileName;
    private final byte[] bytes;
    private final String contentType;

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return bytes.length == 0;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public ByteArrayInputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException {
        Files.write(dest.toPath(), bytes);
    }
}
