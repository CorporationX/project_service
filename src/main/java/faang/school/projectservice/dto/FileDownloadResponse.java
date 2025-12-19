package faang.school.projectservice.dto;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class FileDownloadResponse {
    private String fileName;
    private String contentType;
    private Long size;
    private InputStream inputStream;
}
