package faang.school.projectservice.dto.meet;

import faang.school.projectservice.model.meet.MeetStatus;
import faang.school.projectservice.validator.meet.CreateMeet;
import faang.school.projectservice.validator.meet.UpdateMeet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class MeetDto {

    @NotNull(groups = {UpdateMeet.class})
    private Long id;

    @NotBlank(message = "Title should not be blank", groups = {CreateMeet.class, UpdateMeet.class})
    @Size(min = 2, max = 255, message = "Meet title should not be blank", groups = {CreateMeet.class, UpdateMeet.class})
    private String title;

    @Size(max = 4096, groups = {CreateMeet.class, UpdateMeet.class})
    private String description;

    @Size(max = 255, groups = {CreateMeet.class, UpdateMeet.class})
    private String location;

    @NotNull(groups = {CreateMeet.class})
    private Long teamId;

    @NotNull(groups = {UpdateMeet.class})
    private MeetStatus status;

    @NotNull(groups = {CreateMeet.class, UpdateMeet.class})
    private LocalDateTime startDate;

    @NotNull(groups = {CreateMeet.class, UpdateMeet.class})
    private LocalDateTime endDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long createdBy;
}
