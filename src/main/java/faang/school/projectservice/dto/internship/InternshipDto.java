package faang.school.projectservice.dto.internship;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternshipDto {
    @NotNull
    private Long id;
    @NotNull
    private Long projectId;
    @NotNull
    private Long mentorId;
    @NotNull
    @NotEmpty
    private List<Long> internsIds;


    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    private InternshipStatus status;
}
