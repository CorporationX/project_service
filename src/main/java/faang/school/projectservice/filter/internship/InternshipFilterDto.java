package faang.school.projectservice.filter.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternshipFilterDto {
    String name;
    InternshipStatus status;
}
