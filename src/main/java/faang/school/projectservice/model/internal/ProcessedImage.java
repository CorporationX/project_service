package faang.school.projectservice.model.internal;

import java.io.IOException;
import java.io.InputStream;

public record ProcessedImage(
        InputStream inputStream,
        long size,
        String contentType,
        String outputExtension) implements AutoCloseable {
    @Override
    public void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }
}
