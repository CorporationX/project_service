package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class UpdateInternshipDto {
    @NotNull
    private Long id;
    private InternshipStatus status;
    @NotBlank
    @Size(max = 128, message = "Internship's name length can't be more than 128 symbols")
    private String name;
    private Long updatedBy;
}
