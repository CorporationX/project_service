package faang.school.projectservice.dto;

import faang.school.projectservice.model.InternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternshipFilterDto {
    private String name;
    private InternshipStatus status;
}
