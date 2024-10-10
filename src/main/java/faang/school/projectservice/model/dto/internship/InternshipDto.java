package faang.school.projectservice.model.dto.internship;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.entity.InternshipStatus;
import faang.school.projectservice.validator.groups.CreateGroup;
import faang.school.projectservice.validator.groups.UpdateGroup;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record InternshipDto(
        @Null(message = "Id must be null", groups = {CreateGroup.class, UpdateGroup.class})
        Long id,

        @NotNull(message = "Project ID can not be null", groups = {CreateGroup.class})
        @Positive(groups = {CreateGroup.class, UpdateGroup.class})
        Long projectId,

        @NotNull(message = "Mentor ID can not be null", groups = {CreateGroup.class})
        @Positive(groups = {CreateGroup.class, UpdateGroup.class})
        Long mentorId,

        @NotEmpty(message = "Interns list mustn't be empty", groups = {CreateGroup.class, UpdateGroup.class})
        List<Long> internsId,

        @NotBlank(message = "Name mustn't be blank", groups = {CreateGroup.class, UpdateGroup.class})
        String name,

        @NotNull(message = "Description mustn't be null", groups = {CreateGroup.class, UpdateGroup.class})
        String description,

        @NotNull(message = "Status mustn't be null", groups = {CreateGroup.class})
        InternshipStatus status,

        @NotNull(message = "Start date mustn't be null", groups = {CreateGroup.class})
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime startDate,

        @NotNull(message = "End date mustn't be null", groups = {CreateGroup.class})
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime endDate) {
}
