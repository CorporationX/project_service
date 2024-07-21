package faang.school.projectservice.dto.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import faang.school.projectservice.annotation.DurationOfInternship;
import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@DurationOfInternship
public class InternshipToUpdateDto {
    @NotNull(message = "enter internship ID")
    private Long id;
    @Positive(message = "mentorId cannot be negative")
    private Long mentorId;
    private List<@NotNull Long> internsId;
    @FutureOrPresent(message = "startDate cannot be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    private InternshipStatus status;
    private String description;
    private String name;
}