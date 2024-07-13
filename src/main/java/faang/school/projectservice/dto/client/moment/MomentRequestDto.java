package faang.school.projectservice.dto.client.moment;

import faang.school.projectservice.validation.annotation.NullableNotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MomentRequestDto {
    @NotNull
    @Size(max = 255)
    private String name;

    @NullableNotBlank
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date = LocalDateTime.now(); //todo check default value

    @Size(min = 1)
    private List<@Positive Long> projectIds;

    @Size(min = 1)
    private List<@Positive Long> teamMemberIds;

    @Size(max = 255)
    private String imageId;
}
