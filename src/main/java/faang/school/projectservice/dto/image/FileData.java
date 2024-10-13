package faang.school.projectservice.dto.image;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileData {
    private byte[] data;
    private String contentType;
}