package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.RoleProcessingException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.filter.InternshipFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

import static faang.school.projectservice.exception.RoleProcessingExceptionMessage.ABSENT_INTERN_ROLE_EXCEPTION;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipServiceValidation internshipServiceValidation;
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> filters;


    public InternshipDto create(InternshipDto internshipDto) {
        internshipServiceValidation.validationCreate(internshipDto);

        Internship internship = toEntity(internshipDto);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    public InternshipDto update(InternshipDto internshipDto) {
        internshipServiceValidation.validationUpdate(internshipDto);

        Internship internship = toEntity(internshipDto);

        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            processCompletedInternship(internship);
        }

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    public List<InternshipDto> getInternshipsOfProject(long projectId, InternshipFilterDto filtersDto) {
        List<Internship> projectInternships = internshipRepository.findAll().stream()
                .filter(internship -> internship.getProject().getId().equals(projectId))
                .toList();

        return filters.stream()
                .filter(filter -> filter.isApplicable(filtersDto))
                .flatMap(filter -> filter.apply(projectInternships, filtersDto))
                .distinct()
                .map(internshipMapper::toDto)
                .toList();
    }

    private void processCompletedInternship(Internship internship) {
        List<TeamMember> interns = internship.getInterns();
        var internsToBeHired = interns.stream()
                .filter(checkCompletionOfTasks())
                .toList();

        interns.removeAll(internsToBeHired);

        interns.forEach(firedIntern -> firedIntern.getTeam().getTeamMembers().remove(firedIntern));
        interns.clear();

        assignRolesToInterns(internsToBeHired);
    }

    private Predicate<TeamMember> checkCompletionOfTasks() {
        return teamMember -> {
            var allTasks = teamMember.getStages().stream()
                    .flatMap(stage -> stage.getTasks().stream())
                    .toList();

            var doneTasks = allTasks.stream()
                    .filter(task -> task.getStatus().equals(TaskStatus.DONE))
                    .toList();

            return doneTasks.size() == allTasks.size();
        };
    }

    private void assignRolesToInterns(List<TeamMember> interns) {
        interns.forEach(intern -> {
            var internRoles = intern.getRoles();

            if (!internRoles.contains(TeamRole.INTERN)) {
                throw new RoleProcessingException(ABSENT_INTERN_ROLE_EXCEPTION.getMessage());
            }

            if (internRoles.size() == 1) {
                internRoles.add(TeamRole.DEVELOPER);
                //TODO: нормально ли делать по умолчанию бывших стажеров разработчиками, если до этого у них не было другой роли?
            }

            internRoles.remove(TeamRole.INTERN);

            teamMemberRepository.save(intern);
        });
    }

    private Internship toEntity(InternshipDto internshipDto) {
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