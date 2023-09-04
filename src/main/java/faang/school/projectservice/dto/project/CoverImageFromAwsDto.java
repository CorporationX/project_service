package faang.school.projectservice.dto.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CoverImageFromAwsDto {
    private byte[] file;
    private String contentType;
}

