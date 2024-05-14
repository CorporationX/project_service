package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternshipFiller {
    private final InternshipMapper internshipMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public Internship fillEntity(InternshipDto internshipDto) {
        Internship internship = internshipMapper.toEntity(internshipDto);

        internship.setProject(projectRepository.getProjectById(internshipDto.getProjectId()));
        internship.setMentorId(teamMemberRepository.findById(internshipDto.getMentorId()));

        var interns = internshipDto.getInternsIds().stream()
                .map(teamMemberRepository::findById)
                .toList();

        internship.setInterns(interns);

        return internship;
    }
}
