package faang.school.projectservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDownloadDto {
    byte[] fileBytes;
    String filename;
}
