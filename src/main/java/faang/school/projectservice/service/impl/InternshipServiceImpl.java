package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.dto.InternshipDto;
import faang.school.projectservice.model.dto.InternshipFilterDto;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.entity.Internship;
import faang.school.projectservice.model.enums.InternshipStatus;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.entity.Task;
import faang.school.projectservice.model.enums.TaskStatus;
import faang.school.projectservice.model.entity.Team;
import faang.school.projectservice.model.entity.TeamMember;
import faang.school.projectservice.model.enums.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class InternshipServiceImpl implements InternshipService {
    private static final int MAX_PAGE_SIZE = 100;

    private final ProjectRepository projectRepository;
    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;
    private final List<InternshipFilter> internshipFilters;
    private final InternshipValidator validator;
    private final InternshipMapper internshipMapper;

    @Override
    @Transactional
    public InternshipDto create(InternshipDto internshipDto) {
        validator.validateInternsPresence(internshipDto.getInternUserIds());
        validator.validateInternshipDuration(internshipDto.getStartDate(), internshipDto.getEndDate());
        validator.validateMentorAssignedToProject(internshipDto.getMentorUserId(), internshipDto.getProjectId());

        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        TeamMember mentor = teamMemberRepository.findByUserIdAndProjectId(internshipDto.getMentorUserId(), internshipDto.getProjectId());
        validator.validateInternshipNotCreated(project.getId(), mentor.getId(), mentor.getUserId());

        List<TeamMember> interns = createInterns(internshipDto.getInternUserIds());

        Team team = mentor.getTeam();
        interns.forEach(team::addMember);
        projectRepository.save(project);

        Internship internship = buildInternship(internshipDto, mentor, interns, project);
        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Override
    @Transactional
    public InternshipDto update(long id, InternshipDto internshipDto) {
        validator.validateIdFromPath(id, internshipDto.getId());
        validator.validateInternsPresence(internshipDto.getInternUserIds());

        Internship internship = internshipRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
                String.format("There is no internship with id = %d", id)));
        validator.validateProjectUnchanged(internship.getProject(), internshipDto.getProjectId());
        validator.validateInternshipStatus(internship.getStatus(), internshipDto.getStatus());
        internship.setUpdatedBy(internshipDto.getCreatorUserId());

        if (internship.getStatus() == InternshipStatus.PREPARATION) {
            validator.validateInternshipDuration(internshipDto.getStartDate(), internshipDto.getEndDate());
            validator.validateMentorAssignedToProject(internshipDto.getMentorUserId(), internshipDto.getProjectId());

            TeamMember mentor = internship.getMentor();
            Team team = mentor.getTeam();
            updateMentorIfNeeded(internshipDto, mentor, team);
            List<TeamMember> interns = internship.getInterns();
            updateInternsIfNeeded(internshipDto, interns, team);

            internship.setMentor(mentor);
            internship.setInterns(interns);
            internship.setStartDate(internshipDto.getStartDate());
            internship.setEndDate(internshipDto.getEndDate());
            internship.setStatus(internshipDto.getStatus());
            internship.setDescription(internshipDto.getDescription());
            internship.setName(internshipDto.getName());
        }

        if (internship.getStatus() == InternshipStatus.IN_PROGRESS && internshipDto.getStatus() == InternshipStatus.IN_PROGRESS) {
            validator.validateInternshipDuration(internship.getStartDate(), internshipDto.getEndDate());
            validator.validateMentorAssignedToProject(internshipDto.getMentorUserId(), internshipDto.getProjectId());
            validator.validateNewTeamRolePresence(internshipDto.getNewTeamRole());

            TeamMember mentor = internship.getMentor();
            Team team = mentor.getTeam();
            updateMentorIfNeeded(internshipDto, mentor, team);
            List<TeamMember> interns = updateInternsAfterStart(internshipDto, internship);

            internship.setMentor(mentor);
            internship.setInterns(interns);
            internship.setEndDate(internshipDto.getEndDate());
            internship.setDescription(internshipDto.getDescription());
            internship.setName(internshipDto.getName());
        }

        if (internship.getStatus() == InternshipStatus.IN_PROGRESS && internshipDto.getStatus() == InternshipStatus.COMPLETED) {
            List<TeamMember> interns = updateInternsAfterStart(internshipDto, internship);
            cancelTasks(internshipDto.getProjectId(), interns);
            removeInternsFromTeamAndRepository(interns);
            interns.clear();
            internship.setInterns(interns);
            internship.setStatus(InternshipStatus.COMPLETED);
        }

        Internship updatedInternship = internshipRepository.save(internship);
        return internshipMapper.toDto(updatedInternship);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternshipDto> getInternshipsByProjectAndFilter(Long projectId, InternshipFilterDto filterDto) {
        validator.validateProjectExists(projectId);

        List<Internship> internships = internshipRepository.findByProjectId(projectId);
        List<Internship> filteredInternships = internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(internships.stream(),
                        (internshipStream, internshipFilter) -> internshipFilter.apply(internshipStream, filterDto),
                        (s1, s2) -> s1)
                .toList();

        return internshipMapper.toDtoList(filteredInternships);
    }

    @Override
    public Page<InternshipDto> getAllInternships(Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            pageable = PageRequest.of(pageable.getPageNumber(), MAX_PAGE_SIZE, pageable.getSort());
        }

        Page<Internship> internshipsPage = internshipRepository.findAll(pageable);
        return internshipsPage.map(internshipMapper::toDto);
    }

    @Override
    public InternshipDto getInternshipById(Long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("Internship with id = %d not found", internshipId)));
        return internshipMapper.toDto(internship);
    }

    private List<TeamMember> createInterns(List<Long> internUserIds) {
        return internUserIds.stream()
                .distinct()
                .map(internUserId -> TeamMember.builder()
                        .roles(Collections.singletonList(TeamRole.INTERN))
                        .userId(internUserId)
                        .build())
                .toList();
    }

    private List<TeamMember> updateInternsAfterStart(InternshipDto internshipDto, Internship internship) {
        List<TeamMember> interns = new ArrayList<>(internship.getInterns());

        Set<Long> internshipInternsUserIds = interns.stream()
                .map(TeamMember::getUserId)
                .collect(Collectors.toSet());
        Set<Long> dtoInternsUserIds = new HashSet<>(internshipDto.getInternUserIds());
        validator.validateNoNewInternsAfterStart(internshipInternsUserIds, dtoInternsUserIds);
        List<TeamMember> internsForRemove = processInternsForRemove(internshipDto.getProjectId(), interns, internshipInternsUserIds, dtoInternsUserIds);
        interns.removeAll(internsForRemove);
        List<TeamMember> graduatedInterns = promoteCompletedInterns(internshipDto, internship, interns);
        interns.removeAll(graduatedInterns);
        return interns;
    }

    private List<TeamMember> promoteCompletedInterns(InternshipDto internshipDto, Internship internship, List<TeamMember> interns) {
        if (interns.isEmpty()) {
            return Collections.emptyList();
        }
        Team team = interns.get(0).getTeam();
        Set<Long> allProjectIds = getAllProjectIds(internship.getProject());
        List<TeamMember> withoutActiveTasksInterns = findInternsWithAllTasksInStatuses(
                interns,
                allProjectIds,
                List.of(TaskStatus.DONE, TaskStatus.CANCELLED));
        withoutActiveTasksInterns
                .forEach(teamMember -> {
                    List<TeamRole> roles = teamMember.getRoles();
                    roles.remove(TeamRole.INTERN);
                    roles.add(internshipDto.getNewTeamRole());
                });
        teamRepository.save(team);
        return withoutActiveTasksInterns;
    }

    private void updateInternsIfNeeded(InternshipDto internshipDto, List<TeamMember> interns, Team team) {
        Set<Long> internshipInternsUserIds = interns.stream()
                .map(TeamMember::getUserId)
                .collect(Collectors.toSet());
        Set<Long> dtoInternsUserIds = new HashSet<>(internshipDto.getInternUserIds());

        List<TeamMember> internsForRemove = processInternsForRemove(internshipDto.getProjectId(), interns, internshipInternsUserIds, dtoInternsUserIds);
        List<TeamMember> internsForAdd = processInternsForAdd(team, dtoInternsUserIds, internshipInternsUserIds);

        interns.removeAll(internsForRemove);
        interns.addAll(internsForAdd);
    }

    private List<TeamMember> processInternsForAdd(Team team, Set<Long> dtoInternsUserIds, Set<Long> internshipInternsUserIds) {
        Set<Long> internsUserIdsForAdd = new HashSet<>(dtoInternsUserIds);
        internsUserIdsForAdd.removeAll(internshipInternsUserIds);

        List<TeamMember> internsForAdd = createInterns(new ArrayList<>(internsUserIdsForAdd));
        internsForAdd.forEach(team::addMember);
        teamRepository.save(team);
        return internsForAdd;
    }

    private List<TeamMember> processInternsForRemove(Long projectId, List<TeamMember> interns, Set<Long> internshipInternsUserIds, Set<Long> dtoInternsUserIds) {
        Set<Long> internsUserIdsForRemove = new HashSet<>(internshipInternsUserIds);
        internsUserIdsForRemove.removeAll(dtoInternsUserIds);
        List<TeamMember> internsForRemove = interns.stream()
                .filter(intern -> internsUserIdsForRemove.contains(intern.getUserId()))
                .toList();
        cancelTasks(projectId, internsForRemove);
        removeInternsFromTeamAndRepository(internsForRemove);
        return internsForRemove;
    }

    private void cancelTasks(Long projectId, List<TeamMember> teamMembers) {
        List<Long> userIds = teamMembers.stream()
                .map(TeamMember::getUserId)
                .toList();
        List<Task> tasksForCancel = taskRepository.findTasksByProjectIdAndPerformerUserIdsAndStatusNotIn(
                projectId,
                userIds,
                List.of(TaskStatus.CANCELLED, TaskStatus.DONE));
        tasksForCancel.forEach(task -> task.setStatus(TaskStatus.CANCELLED));
        taskRepository.saveAll(tasksForCancel);
    }

    private void removeInternsFromTeamAndRepository(List<TeamMember> internsForRemove) {
        if (internsForRemove.isEmpty()) {
            return;
        }
        Team team = internsForRemove.get(0).getTeam();
        if (team != null) {
            internsForRemove.forEach(team::removeMember);
            teamRepository.save(team);
        }
    }

    private void updateMentorIfNeeded(InternshipDto internshipDto, TeamMember mentor, Team team) {
        if (!mentor.getUserId().equals(internshipDto.getMentorUserId())) {
            team.removeMember(mentor);
            TeamMember newMentor = teamMemberRepository.findByUserIdAndProjectId(internshipDto.getMentorUserId(), internshipDto.getProjectId());
            team.addMember(newMentor);
            teamRepository.save(team);
        }
    }

    private Set<Long> getAllProjectIds(Project project) {
        Set<Long> projectIds = new HashSet<>();
        collectProjectIdsRecursively(project, projectIds);
        return projectIds;
    }

    private void collectProjectIdsRecursively(Project project, Set<Long> projectIds) {
        projectIds.add(project.getId());
        if (project.getChildren() != null) {
            for (Project child : project.getChildren()) {
                collectProjectIdsRecursively(child, projectIds);
            }
        }
    }

    private List<TeamMember> findInternsWithAllTasksInStatuses(List<TeamMember> interns, Set<Long> allProjectIds, List<TaskStatus> statuses) {
        List<Long> internUserIds = interns.stream()
                .map(TeamMember::getUserId)
                .collect(Collectors.toList());

        List<Long> stillInternUserIds = taskRepository.findUserIdsWithTasksNotInStatuses(
                allProjectIds,
                internUserIds,
                statuses
        );

        return interns.stream()
                .filter(intern -> !stillInternUserIds.contains(intern.getUserId()))
                .collect(Collectors.toList());
    }

    private Internship buildInternship(InternshipDto internshipDto,
                                       TeamMember mentor,
                                       List<TeamMember> interns,
                                       Project project) {
        return Internship.builder()
                .mentor(mentor)
                .interns(interns)
                .project(project)
                .startDate(internshipDto.getStartDate())
                .endDate(internshipDto.getEndDate())
                .status(InternshipStatus.PREPARATION)
                .description(internshipDto.getDescription())
                .name(internshipDto.getName())
                .createdBy(internshipDto.getCreatorUserId())
                .build();
    }
}
