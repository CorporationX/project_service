package faang.school.projectservice.dto;

import faang.school.projectservice.model.InternshipStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternshipFilterDto {

    @Size(max = 255)
    private String name;
    private InternshipStatus status;
}
