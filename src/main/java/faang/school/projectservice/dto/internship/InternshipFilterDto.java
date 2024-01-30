package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InternshipFilterDto {
    private InternshipStatus status;

    //еще было сказано, получить с фильтром по роли, но роли в стажировке нет
}
