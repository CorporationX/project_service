package faang.school.projectservice.dto.resource;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResourceDto {
    private Long id;
    private String key;
    private long size;
    private LocalDateTime createdAt;
    private String name;
    private String type;
    @NotNull
    @Min(1)
    private Long postId;
}