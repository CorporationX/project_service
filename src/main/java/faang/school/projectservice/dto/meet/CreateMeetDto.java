package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.MeetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateMeetDto {
    @NotBlank
    @NotNull
    @Length(max = 128, message = "Length must be less than 128")
    private String title;
    @NotBlank
    @NotNull
    @Length(max = 512, message = "Length must be less than 512")
    private String description;
    @NotNull
    private Long projectId;
    private final MeetStatus status = MeetStatus.PENDING;
    @NotNull
    private List<Long> userIds;
}
