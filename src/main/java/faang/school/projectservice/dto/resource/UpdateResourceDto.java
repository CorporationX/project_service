package faang.school.projectservice.dto.resource;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdateResourceDto extends ResourceDto {
    private ZonedDateTime updatedAt;
    private Long updatedById;
}
