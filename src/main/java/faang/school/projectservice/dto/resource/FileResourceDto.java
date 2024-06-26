package faang.school.projectservice.dto.resource;

import java.io.InputStream;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileResourceDto {
    private String fileName;
    private String folderName;
    private InputStream resourceInputStream;
    private Long size;
    private String contentType;
}
