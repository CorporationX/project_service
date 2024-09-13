package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilter;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.TeamRoleDto;
import faang.school.projectservice.exception.internship.EntityNotFoundException;
import faang.school.projectservice.exception.internship.IncorrectInternshipDateTimeException;
import faang.school.projectservice.exception.internship.InternshipUpdateException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipServiceImpl implements InternshipService {
    private final static int maxInternshipDurationMonth = 3;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;

    @Override
    public InternshipDto createInternship(CreateInternshipDto internship) {
        if (!isProjectHaveIntern(internship.getProjectId())) {
            throw new EntityNotFoundException("No interns");
        }
        long internshipDuration = ChronoUnit.MONTHS.between(internship.getStartDate(), internship.getEndDate());
        if (internshipDuration > maxInternshipDurationMonth) {
            throw new IncorrectInternshipDateTimeException("The internship period cannot be longer than %d months"
                    .formatted(maxInternshipDurationMonth));
        }
        if (!isMentorInProject(internship.getProjectId(), internship.getMentorId())) {
            throw new EntityNotFoundException("This mentor is not in this project");
        }
        Internship newInternship = internshipMapper.createInternshipDtoToInternship(internship);
        internshipRepository.save(newInternship);
        log.info("New internship created: {}", newInternship);
        return internshipMapper.internshipToInternshipDto(newInternship);
    }

    @Override
    public void updateInternship(Long internshipId, TeamRoleDto teamRole) {
        Internship internshipForUpdate = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found"));
        if (internshipForUpdate.getStatus().equals(InternshipStatus.IN_PROGRESS)) {
            throw new InternshipUpdateException("This internship is in progress");
        }
        if (internshipForUpdate.getStatus().equals(InternshipStatus.COMPLETED)) {
            List<TeamMember> interns = internshipForUpdate.getInterns();
            interns.forEach(intern -> {
                boolean passed = intern.getStages().stream()
                        .flatMap(stage -> stage.getTasks().stream())
                        .allMatch(task -> task.getStatus().equals(TaskStatus.DONE));
                if (passed) {
                    addInternNewRole(internshipId, intern.getId(), teamRole);
                } else {
                    intern.setTeam(null);
                    intern.setRoles(null);
                }
            });
        }
        internshipRepository.save(internshipForUpdate);
        log.info("Internship updated: {}", internshipId);
    }

    @Override
    public List<InternshipDto> getAllInternships(InternshipFilterDto filters) {
        if (filters == null) {
            filters = new InternshipFilterDto();
        }
        List<Internship> internships = internshipRepository.findAll();
        if (internships.isEmpty()) {
            throw new EntityNotFoundException("No internships");
        } else {
            Stream<Internship> filteredInternships = internships.stream();
            for (InternshipFilter filter : internshipFilters) {
                if (filter.isApplicable(filters)) {
                    filteredInternships = filter.apply(filteredInternships, filters);
                }
            }
            List<InternshipDto> result = filteredInternships.map(internshipMapper::internshipToInternshipDto)
                    .toList();
            log.info("Returned {} filtered internships", result.size());
            return result;
        }
    }

    @Override
    public List<InternshipDto> getAllInternshipsOnProject(Long projectId) {
        List<Internship> internships = internshipRepository.findAll();
        if (internships.isEmpty()) {
            throw new EntityNotFoundException("No internships");
        } else {
            log.info("Returned {} internships on project {}", internships.size(), projectId);
            return internships.stream()
                    .filter(internship -> internship.getProject().getId().equals(projectId))
                    .map(internshipMapper::internshipToInternshipDto)
                    .toList();
        }
    }

    @Override
    public InternshipDto getInternshipById(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found"));
        log.info("Returned internship by ID: {}", internshipId);
        return internshipMapper.internshipToInternshipDto(internship);
    }

    private boolean isMentorInProject(Long projectId, TeamMember mentor) {
        Project project = projectRepository.getProjectById(projectId);
        if (project == null) {
            throw new EntityNotFoundException("Project not found");
        } else {
            return project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .filter(teamMember -> !teamMember.getRoles().contains(TeamRole.INTERN))
                    .anyMatch(teamMember -> teamMember.getId().equals(mentor.getId()));
        }
    }

    private boolean isProjectHaveIntern(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        if (project == null) {
            throw new EntityNotFoundException("Project not found");
        } else {
            return project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .anyMatch(teamMember -> teamMember.getRoles().contains(TeamRole.INTERN));
        }
    }

    private void addInternNewRole(Long internshipId, Long teamMemberId, TeamRoleDto teamRoleDto) {
        Internship thisInternship = internshipRepository.findById(internshipId).orElse(null);
        TeamRole newInternRole = internshipMapper.teamRoleDtoToTeamRole(teamRoleDto);
        if (thisInternship == null) {
            throw new EntityNotFoundException("No internship");
        } else {
            TeamMember intern = thisInternship.getInterns().stream()
                    .filter(teamMember -> teamMember.getId().equals(teamMemberId))
                    .findFirst().orElse(null);
            intern.getRoles().add(newInternRole);
            intern.getRoles().removeIf(role -> role.equals(TeamRole.INTERN));
        }
    }
}