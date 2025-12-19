package faang.school.projectservice.dto;

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
public class ResourceDto {
    private Long id;
    private String name;
    private Long size;
    private ResourceType type;
    private String contentType;
    private String createdBy;
    private LocalDateTime createdAt;
}