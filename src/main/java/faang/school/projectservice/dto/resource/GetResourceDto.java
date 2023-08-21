package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.resource.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetResourceDto {
    private String name;
    private String type;
    private InputStream inputStream;
    private long size;
}
