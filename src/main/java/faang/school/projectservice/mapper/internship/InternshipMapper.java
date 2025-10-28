package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InternshipMapper {

    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Autowired
    public InternshipMapper(ProjectRepository projectRepository, TeamMemberRepository teamMemberRepository) {
        this.projectRepository = projectRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public Internship toInternship(CreateInternshipDto createInternshipDto) {
        return Internship.builder()
                .name(createInternshipDto.name())
                .description(createInternshipDto.description())
                .status(createInternshipDto.status())
                .role(createInternshipDto.role())
                .startDate(createInternshipDto.startDate())
                .endDate(createInternshipDto.endDate())
                .project(projectRepository.findById(createInternshipDto.projectId()).orElseThrow(() ->
                        new RuntimeException("Project with ID " + createInternshipDto.projectId() + " was not found")))
                .mentorId(teamMemberRepository.findById(createInternshipDto.mentorId()).orElseThrow(() ->
                        new RuntimeException("Mentor with ID " + createInternshipDto.mentorId() + " was not found")))
                .interns(fetchInterns(createInternshipDto.internsIds()))
                .build();
    }

    private List<TeamMember> fetchInterns(List<Long> internIds) {
        return teamMemberRepository.findAllById(internIds);
    }
}