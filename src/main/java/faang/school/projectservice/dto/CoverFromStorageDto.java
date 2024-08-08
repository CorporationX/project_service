package faang.school.projectservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoverFromStorageDto {
    private byte[] cover;
    private String contentType;
}
