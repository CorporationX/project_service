package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InternshipUpdateDto {
    @Min(1)
    private long id;
    private List<Long> internsId;
    private InternshipStatus status;
}
