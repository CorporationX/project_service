package faang.school.projectservice.testData.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.testData.teamMember.TeamMemberTestData;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;

@Getter
public class InternshipTestData {
    private final InternshipDto internshipDto;
    private final Internship internship;
    private final TeamMemberTestData teamMemberTestData;

    public InternshipTestData() {
        teamMemberTestData = new TeamMemberTestData();

        var project = new Project();
        project.setId(1L);

        var mentor = new TeamMember();
        mentor.setId(1L);
        mentor.setTeam(new Team());
        mentor.getTeam().setProject(project);


        internshipDto = InternshipDto.builder()
                .name("Faang internship")
                .description("The coolest internship ever")
                .projectId(1L)
                .mentorId(1L)
                .internsIds(List.of(1L, 2L, 3L))
                .startDate(LocalDateTime.of(3024, 6, 1, 8, 0))
                .endDate(LocalDateTime.of(3024, 8, 31, 16, 0))
                .status(IN_PROGRESS)
                .createdBy(1L)
                .build();

        internship = new Internship();
        internship.setName("Faang internship");
        internship.setDescription("The coolest internship ever");
        internship.setProject(project);
        internship.setMentorId(mentor);
        internship.setInterns(teamMemberTestData.getInterns());
        internship.setStartDate(LocalDateTime.of(3024, 6, 1, 8, 0));
        internship.setEndDate(LocalDateTime.of(3024, 8, 31, 16, 0));
        internship.setStatus(IN_PROGRESS);
        internship.setCreatedBy(1L);
    }
}
