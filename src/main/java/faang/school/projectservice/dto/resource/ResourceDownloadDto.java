package faang.school.projectservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDownloadDto {
    private byte[] bytes;
    private MediaType type;
    private ContentDisposition contentDisposition;
}