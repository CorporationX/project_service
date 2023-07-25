package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.InternshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class InternshipDto {
    private Long id;
    private Long projectId;
    private Long mentorId;
    private List<Long> interns;
    private InternshipStatus status;
    private String description;
    private String name;
    private Long schedule;
}
