package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final TeamMemberRepository teamMemberJpaRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final MomentMapper momentMapper;

    public Moment createMomentCompletedForSubProject(Project subProject) {
        Moment moment = new Moment();
        moment.setName(subProject.getName());
        moment.setDescription("All subprojects completed");

        Set<Long> allProjectMembers = new HashSet<>();
        getProjectMembers(subProject, allProjectMembers);

        moment.setUserIds(new ArrayList<>(allProjectMembers));
        return momentRepository.save(moment);
    }

    public MomentDto create(MomentDto momentDto) {
        isProjectClosed(momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        return momentMapper.toDto(momentRepository.save(moment));
    }

    public MomentDto update(Long id, MomentDto momentDto) {
        Moment oldMoment = findById(id);
        MomentDto dto = checkNewProjects(oldMoment, momentDto);
        Moment newMoment = checkNewMembers(oldMoment, dto);
        return momentMapper.toDto(momentRepository.save(newMoment));
    }

    public Page<MomentDto> getAllMoments(int page, int pageSize) {
        Page<Moment> moments = momentRepository.findAll(PageRequest.of(page, pageSize));
        return moments.map(momentMapper::toDto);
    }

    public MomentDto getById(Long id) {
        return momentMapper.toDto(findById(id));
    }

    private Moment findById(Long id) {
        return momentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("moment by id: " + id + " not found"));
    }

    private void isProjectClosed(MomentDto momentDto) {
        momentDto.getProjectIds().forEach(projectId -> {
            Project project = projectRepository.getProjectById(projectId);
            if (project.getStatus() == ProjectStatus.CANCELLED || project.getStatus() == ProjectStatus.COMPLETED) {
                throw new DataValidationException("can not create a moment for a closed project");
            }
        });
    }

    private MomentDto checkNewProjects(Moment oldMoment, MomentDto momentDto) {
        Set<Long> newProjectIds = new HashSet<>(momentDto.getProjectIds());
        Set<Long> oldProjectIds = oldMoment.getProjects()
                .stream()
                .map(Project::getId)
                .collect(Collectors.toSet());

        newProjectIds.removeAll(oldProjectIds);
        if (newProjectIds.size() > 0) {
            Set<Long> momentUserIds = new HashSet<>(momentDto.getUserIds());
            newProjectIds.forEach(projectId -> {
                List<Long> userIds = projectRepository.getProjectById(projectId)
                        .getTeams()
                        .stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .map(TeamMember::getId)
                        .distinct()
                        .toList();
                momentUserIds.addAll(userIds);
            });
            momentDto.setUserIds(new ArrayList<>(momentUserIds));
        }
        return momentDto;
    }

    private Moment checkNewMembers(Moment oldMoment, MomentDto momentDto) {
        Set<Long> oldUserIds = new HashSet<>(oldMoment.getUserIds());
        Set<Long> newUserIds = new HashSet<>(momentDto.getUserIds());
        newUserIds.removeAll(oldUserIds);
        if (newUserIds.size() > 0) {
            Set<Long> projectIds = new HashSet<>(momentDto.getProjectIds());
            newUserIds.forEach(userId -> {
                Long userProjectId = teamMemberJpaRepository.findById(userId)
                        .getTeam()
                        .getProject()
                        .getId();
                projectIds.add(userProjectId);
            });
            momentDto.setProjectIds(new ArrayList<>(projectIds));
        }
        return momentMapper.toEntity(momentDto);
    }

    private void getProjectMembers(Project project, Set<Long> allProjectMembers) {
        List<Team> teams = Optional.ofNullable(project.getTeams()).orElse(Collections.emptyList());
        teams.stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .forEach(allProjectMembers::add);

        List<Project> children = project.getChildren();
        if (children != null) {
            children.forEach(child -> getProjectMembers(child, allProjectMembers));
        }
    }
}
