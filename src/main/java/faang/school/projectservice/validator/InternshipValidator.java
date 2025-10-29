package faang.school.projectservice.validator;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class InternshipValidator {

    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Autowired
    public InternshipValidator(ProjectRepository projectRepository, TeamMemberRepository teamMemberRepository) {
        this.projectRepository = projectRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public static void validateInternshipLength(CreateInternshipDto createInternshipDto) {
        LocalDate start = LocalDate.from(createInternshipDto.startDate());
        LocalDate end = createInternshipDto.endDate() != null ?
                LocalDate.from(createInternshipDto.endDate()) :
                start.plusMonths(3);
        if (end.isAfter(start.plusMonths(3))) {
            throw new DateTimeException("Internship can't be longer than three months");
        }
    }

    public void validateMentorBelongsToProject(CreateInternshipDto createInternshipDto) {
        Optional<Project> projectOpt = projectRepository.findById(createInternshipDto.projectId());
        if (projectOpt.isEmpty()) {
            throw new ValidationException("The project with the specified ID was not found");
        }

        Project project = projectOpt.get();

        Optional<TeamMember> mentorOpt = teamMemberRepository.findById(createInternshipDto.mentorId());
        if (mentorOpt.isEmpty()) {
            throw new ValidationException("The mentor with the specified ID was not found");
        }

        TeamMember mentor = mentorOpt.get();

        boolean belongsToAnyTeam = project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().contains(mentor));

        if (!belongsToAnyTeam) {
            throw new ValidationException("The specified mentor is not a member of the project team");
        }
    }
}