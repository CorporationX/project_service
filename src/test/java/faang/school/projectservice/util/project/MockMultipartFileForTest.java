package faang.school.projectservice.util.project;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

public class MockMultipartFileForTest implements MultipartFile {
    private String name;
    private String originalFileName;
    private String contentType;
    private Boolean isEmpty;
    private Long size;
    private byte[] bytes;
    private InputStream inputStream;

    public MockMultipartFileForTest() {}

    public MockMultipartFileForTest(String name, String originalFileName, String contentType, Boolean isEmpty, Long size, byte[] bytes, InputStream inputStream) {
        this.name = name;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.isEmpty = isEmpty;
        this.size = size;
        this.bytes = bytes;
        this.inputStream = inputStream;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFileName;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.isEmpty;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public byte @NotNull [] getBytes() {
        return this.bytes;
    }

    @Override
    public @NotNull InputStream getInputStream() {
        return this.inputStream;
    }

    @Override
    public void transferTo(@NotNull File dest) {

    }
}
