package faang.school.projectservice.dto.internship;

import faang.school.projectservice.model.TeamMember;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CreateInternshipDto {

    private String internshipName;

    private String description;

    private Long projectId;

    private List<TeamMember> interns;

    private TeamMember mentorId;

    private final LocalDateTime startDate = LocalDateTime.now();

    private LocalDateTime endDate;
}
