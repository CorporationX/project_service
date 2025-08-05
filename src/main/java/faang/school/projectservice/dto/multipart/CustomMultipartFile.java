package faang.school.projectservice.dto.multipart;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Value
@Builder
public class CustomMultipartFile implements MultipartFile {
    String name;
    String originalFilename;
    String contentType;
    byte[] bytes;

    @Override
    public boolean isEmpty() {
        return bytes == null || bytes.length == 0;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(@NonNull File destination) throws IOException, IllegalStateException {
        try (OutputStream outputStream = new FileOutputStream(destination)) {
            outputStream.write(bytes);
        }
    }
}
