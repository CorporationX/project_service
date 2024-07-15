package faang.school.projectservice.dto.client.moment;

import faang.school.projectservice.validation.annotation.NullableNotBlank;
import jakarta.validation.constraints.NotBlank;
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
public class MomentRequestDto {
    @NotBlank
    @Size(max = 255)
    private String name;

    @NullableNotBlank
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date = LocalDateTime.now();

    @Size(min = 1)
    private List<@Positive Long> projectIds;

    @Size(min = 1)
    private List<@Positive Long> teamMemberIds;

    @Size(max = 255)
    private String imageId;
}
