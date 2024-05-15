package faang.school.projectservice.service.internship;

import faang.school.projectservice.model.Internship;
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

    public void fillEntity(Internship internship, Long projectId, Long mentorId, List<Long> internIds) {
        internship.setProject(projectRepository.getProjectById(projectId));
        internship.setMentorId(teamMemberRepository.findById(mentorId));
        internship.setInterns(teamMemberRepository.findAllByIds(internIds));
    }
}
