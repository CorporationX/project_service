package faang.school.projectservice.dto.moment;

import faang.school.projectservice.validation.annotation.NullableNotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomentUpdateDto {
    @NotNull
    private Long id;

    @NullableNotBlank
    @Size(max = 255)
    private String name;

    @NullableNotBlank
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;

    @Size(min = 1)
    private List<@Positive Long> projectIds;

    @Size(min = 1)
    private List<@Positive Long> teamMemberIds;

    @Size(max = 255)
    private String imageId;
}