package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.mapper.internship.InternshipDtoMapper;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.bind.ValidationException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class InternshipService {

    private final InternshipValidator internshipValidator;
    private final InternshipMapper internshipMapper;
    private final InternshipRepository internshipRepository;
    private final InternshipDtoMapper internshipDtoMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public InternshipDto createInternship(CreateInternshipDto createInternshipDto) {

        internshipValidator.validateInternshipLength(createInternshipDto);
        validateMentorBelongsToProject(createInternshipDto);

        Internship internship = internshipMapper.toInternship(createInternshipDto);

        internship = internshipRepository.save(internship);

        return internshipDtoMapper.toInternshipDto(internship);
    }

    private void validateMentorBelongsToProject(CreateInternshipDto createInternshipDto) {
        Optional<Project> projectOpt = projectRepository.findById(createInternshipDto.projectId());
        if (projectOpt.isEmpty()) {
            try {
                throw new ValidationException("The project with the specified ID was not found");
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        }
        Project project = projectOpt.get();

        Optional<TeamMember> mentorOpt = teamMemberRepository.findById(createInternshipDto.mentorId());
        if (mentorOpt.isEmpty()) {
            throw new jakarta.validation.ValidationException("The mentor with the specified ID was not found");
        }
        TeamMember mentor = mentorOpt.get();

        boolean belongsToAnyTeam = project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().contains(mentor));
        if (!belongsToAnyTeam) {
            throw new jakarta.validation.ValidationException("The specified mentor is not a member of the project team");
        }
    }
}