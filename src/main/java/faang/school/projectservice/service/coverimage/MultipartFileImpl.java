package faang.school.projectservice.service.coverimage;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

@Data
@NoArgsConstructor
public class MultipartFileImpl implements MultipartFile {

    private byte[] bytes;
    private String name;
    private String originalFilename;
    private String contentType;
    private boolean isEmpty;
    private long size;

    public MultipartFileImpl(String contentType, String originalFilename, String name, byte[] bytes, long size) {
        this.contentType = contentType;
        this.originalFilename = originalFilename;
        this.name = name;
        this.bytes = bytes;
        this.size = size;
        this.isEmpty = false;
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
    public void transferTo(File dest) throws IllegalStateException {

    }
}
