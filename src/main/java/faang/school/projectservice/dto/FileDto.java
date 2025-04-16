package faang.school.projectservice.dto;

import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    String originalFilename;
    String key;
    String contentType;
    InputStream inputStream;
    @Max(value = 5 * 1024, message = "the cover size exceeds the maximum")
    long size;

    public FileDto(String originalFilename, String key, String contentType, byte[] compressedImageBytes) {
        this.originalFilename = originalFilename;
        this.key = key;
        this.contentType = contentType;
        this.inputStream = new ByteArrayInputStream(compressedImageBytes);
        this.size = compressedImageBytes.length;
    }
}
