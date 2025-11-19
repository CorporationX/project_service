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
public class ResourceResponse {
    private Long id;
    private String name;
    private Long size;
    private ResourceType type;
    private LocalDateTime uploadedAt;
    private String status;
    private String error;
}