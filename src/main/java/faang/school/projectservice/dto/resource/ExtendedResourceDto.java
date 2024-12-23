package faang.school.projectservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;

@Builder
@AllArgsConstructor
@Data
public class ExtendedResourceDto {
    private InputStreamResource resourceStream;
    private HttpHeaders headers;
}
