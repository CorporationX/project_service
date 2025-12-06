package faang.school.projectservice.dto;

import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {
    private Long id;
    private String name;
    private Long size;
    private ResourceType type;
    private LocalDateTime uploadedAt;
    private ResourceUploadStatus status;
    private String error;

    public static ResourceResponse from(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .size(resource.getSize() != null ? resource.getSize().longValue() : null)
                .type(resource.getType())
                .uploadedAt(resource.getCreatedAt())
                .build();
    }

    public static ResourceResponse from(Resource resource, ResourceUploadStatus status) {
        ResourceResponse response = from(resource);
        response.setStatus(status);
        return response;
    }
}