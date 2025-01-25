package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipInternStatus;
import lombok.Data;

@Data
public class InternshipUserStatusDto {
    private Long id;
    private boolean aheadOfSchedule;
    private InternshipInternStatus status;
}
