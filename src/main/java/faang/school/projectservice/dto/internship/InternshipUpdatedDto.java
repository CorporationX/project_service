package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Validated
public class InternshipUpdatedDto {
    private Long id;

    @NotBlank(message = "Name should not be blank")
    @Size(max = 255, message = "Name should not exceed 255 characters")
    private String name;

    private Long projectId;

    @NotNull(message = "Internship status can not be null")
    private InternshipStatus status;

    @NotEmpty(message = "Interns list cannot be empty")
    private List<@Positive(message = "Intern ID must be positive") Long> interns;

    private Long createdBy;

    @Size(max = 255, message = "Pattern should not exceed 255 characters")
    private String requestFilterPattern;
}
