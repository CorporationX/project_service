package faang.school.projectservice.dto;

import faang.school.projectservice.model.InternshipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Internship filter entity")
public class InternshipFilterDto {

    @Size(max = 255)
    private String name;
    private InternshipStatus status;
}
