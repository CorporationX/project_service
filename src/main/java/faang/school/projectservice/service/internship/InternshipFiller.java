package faang.school.projectservice.service.internship;

import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class InternshipFiller {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public void fillEntity(Internship internship, Project project, TeamMember mentor, List<TeamMember> interns) {
        internship.setProject(project);
        internship.setMentorId(mentor);
        internship.setInterns(interns);
    }
}
