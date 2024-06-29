package faang.school.projectservice.dto.resource;

import java.math.BigInteger;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import lombok.Data;

@Data
public class ResourceDto {
    private Long id;
    private String name;
    private String key;
    private BigInteger size;
    private ResourceType type;
    private ResourceStatus status;
    private Long createdById;
    private Long updatedById;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Long projectId;
}
