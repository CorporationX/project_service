package faang.school.projectservice.service.internship;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class InternshipServiceImpl implements InternshipService {

    @Value("${internship.duration.max:3}")
    private int maxInternshipDuration;
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;
    private final TeamRepository teamRepository;
    private final List<InternshipFilter> internshipFilters;

    @Override
    public InternshipDto create(CreateInternshipDto createInternshipDto) {

        Project project = projectRepository.getByIdOrThrow(createInternshipDto.projectId());
        TeamMember mentorTeamMember = teamMemberRepository.getByIdOrThrow(createInternshipDto.mentorId());
        List<TeamMember> interns = teamMemberRepository.findAllById(createInternshipDto.internsIds());
        List<Long> internsIds = interns.stream().map(TeamMember::getId).toList();

        if (interns.size() != createInternshipDto.internsIds().size()) {
            List<Long> notFoundInterns = new ArrayList<>(createInternshipDto.internsIds());
            notFoundInterns.removeAll(internsIds);
            final String errorMessage = "Some interns not found. Found: %s Not found: %s".formatted(internsIds,
                    notFoundInterns);
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }

        if (!createInternshipDto.projectId().equals(mentorTeamMember.getTeam().getProject().getId())) {
            final String errorMessage = "Mentor %d is not from current project %d".formatted(
                    createInternshipDto.mentorId(), createInternshipDto.projectId());
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }

        if (createInternshipDto.endDate().minusMonths(maxInternshipDuration).isAfter(createInternshipDto.startDate())) {
            final String errorMessage = "Duration of internship is more than %d months. Start date %s, end date %s"
                    .formatted(maxInternshipDuration, createInternshipDto.startDate(), createInternshipDto.endDate());
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }

        boolean areInternsFromCurrentProject = interns.stream().allMatch(intern
                -> intern.getTeam().getProject().getId().equals(createInternshipDto.projectId()));

        if (!areInternsFromCurrentProject) {
            final String errorMessage = "Some interns are not from current project. Interns: " + internsIds;
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }

        Internship internship = internshipMapper.toInternship(createInternshipDto);
        internship.setProject(project);
        internship.setMentorId(mentorTeamMember);
        internship.setInterns(interns);
        internship.setStatus(createInternshipDto.startDate().isAfter(LocalDateTime.now())
                ? InternshipStatus.CREATED
                : InternshipStatus.IN_PROGRESS);
        internship.setCreatedBy(userContext.getUserId());
        internship.setCreatedAt(LocalDateTime.now());

        internship = internshipRepository.save(internship);
        log.info("Internship {} created", internship.getId());
        return internshipMapper.toInternshipDto(internship);
    }

    @Override
    public InternshipDto update(long internshipId, UpdateInternshipDto updateInternshipDto) {
        Internship internship = internshipRepository.getByIdOrThrow(internshipId);
        List<Long> internsIds = internship.getInterns().stream().map(TeamMember::getId).toList();

        if (LocalDateTime.now().isAfter(internship.getStartDate())) {

            if (updateInternshipDto.internsIds() != null) {
                boolean hasNewInterns = updateInternshipDto.internsIds().stream()
                        .anyMatch(internId -> !internsIds.contains(internId));

                if (hasNewInterns) {
                    String errorMessage = "Cant add new interns after internship started";
                    log.error(errorMessage);
                    throw new DataValidationException(errorMessage);
                }
            }

            if (internship.getStatus().equals(InternshipStatus.CREATED)) {
                internship.setStatus(InternshipStatus.IN_PROGRESS);
            }
        }

        if (updateInternshipDto.mentorId() != null
                && !updateInternshipDto.mentorId().equals(internship.getMentorId().getId())) {
            TeamMember mentorTeamMember = teamMemberRepository.getByIdOrThrow(updateInternshipDto.mentorId());
            if (!mentorTeamMember.getTeam().getProject().getId().equals(internship.getProject().getId())) {
                String errorMessage = "Mentor %d not from current project %d"
                        .formatted(updateInternshipDto.mentorId(), internship.getProject().getId());
                log.error(errorMessage);
                throw new DataValidationException(errorMessage);
            }
        }

        if (updateInternshipDto.endDate() != null && updateInternshipDto.endDate()
                .minusMonths(maxInternshipDuration).isAfter(internship.getStartDate())) {
            final String errorMessage = "Duration of internship is more than %d months. Start date %s, end date %s"
                    .formatted(maxInternshipDuration, internship.getStartDate(), updateInternshipDto.endDate());
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }

        internshipMapper.update(updateInternshipDto, internship);

        if (LocalDateTime.now().isAfter(internship.getEndDate()) &&
                !internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            internship.setStatus(InternshipStatus.COMPLETED);
        }

        if (internship.getStatus().equals(InternshipStatus.COMPLETED)) {
            List<TeamMember> internsToRemove = new ArrayList<>();
            for (TeamMember intern : internship.getInterns()) {
                boolean allTasksCompleted = isAllInternshipTasksCompleted(intern, internship);
                if (allTasksCompleted) {
                    List<TeamRole> internRoles = intern.getRoles();

                    if (internRoles == null) {
                        intern.setRoles(new ArrayList<>(List.of(internship.getRole())));
                    } else {
                        intern.getRoles().add(internship.getRole());
                    }

                    log.info("Intern {} passed internship", intern.getId());
                    teamMemberRepository.save(intern);
                } else {
                    log.info("Intern {} failed - not all tasks completed", intern.getId());
                    internsToRemove.add(intern);
                }
            }

            if (!internsToRemove.isEmpty()){
                internship.getInterns().removeAll(internsToRemove);
                removeTeamMembersFromProject(internsToRemove);
            }
        }

        internship.setUpdatedBy(userContext.getUserId());
        internship.setUpdatedAt(LocalDateTime.now());

        internship = internshipRepository.save(internship);
        log.info("Internship {} updated", internship.getId());
        return internshipMapper.toInternshipDto(internship);
    }

    @Override
    public List<InternshipDto> getByFilters(InternshipFilterDto internshipFilterDto) {
        Stream<Internship> internshipStream = internshipRepository.findAll().stream();

        if (internshipFilterDto == null) {
            return internshipStream.map(internshipMapper::toInternshipDto).toList();
        }

        for (InternshipFilter internshipFilter : internshipFilters) {
            if (internshipFilter.isApplicable(internshipFilterDto)) {
                internshipStream = internshipFilter.apply(internshipStream, internshipFilterDto);
            }
        }
        return internshipStream
                .map(internshipMapper::toInternshipDto)
                .toList();
    }

    @Override
    public List<InternshipDto> getAll() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toInternshipDto)
                .toList();
    }

    @Override
    public InternshipDto getById(long internshipId) {
        log.debug("Fetching internship request by id={}", internshipId);
        InternshipDto internshipDto = internshipMapper.toInternshipDto(internshipRepository.getByIdOrThrow(internshipId));
        log.debug("Internship request found: id={}, status={}, name={}, role={}",
                internshipDto.id(), internshipDto.status(), internshipDto.name(), internshipDto.role());
        return internshipDto;
    }

    private boolean isAllInternshipTasksCompleted(TeamMember intern, Internship internship) {
        List<Stage> projectStages = internship.getProject().getStages();

        List<Stage> internStages = projectStages.stream()
                .filter(stage -> stage.getExecutors().contains(intern))
                .toList();

        return internStages.stream()
                .flatMap(stage -> stage.getTasks().stream())
                .allMatch(task -> task.getStatus() == TaskStatus.DONE);
    }

    private void removeTeamMembersFromProject(List<TeamMember> teamMembersListToRemove) {
        teamMembersListToRemove.forEach(teamMember -> {
            Team team = teamMember.getTeam();
            if (team != null) {
                team.getTeamMembers().remove(teamMember);
                teamRepository.save(team);
            }
        });
    }
}