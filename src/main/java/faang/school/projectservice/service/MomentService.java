package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final TeamMemberRepository teamMemberJpaRepository;
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
        Moment moment = momentMapper.toEntity(momentDto);
        moment.getProjects().forEach(project -> {
            if (project.getStatus() == ProjectStatus.CANCELLED || project.getStatus() == ProjectStatus.COMPLETED) {
                throw new DataValidationException("can not create a moment for a closed project");
            }
        });
        return momentMapper.toDto(momentRepository.save(moment));
    }

    public MomentDto update(Long id, MomentDto momentDto) {
        Moment oldMoment = findById(id);
        Moment newMoment = momentMapper.toEntity(momentDto);

        Set<Project> oldProjects = new HashSet<>(oldMoment.getProjects());
        Set<Project> newProjects = new HashSet<>(newMoment.getProjects());
        newProjects.removeAll(oldProjects);

        if (newProjects.size() > 0) {
            List<Long> newUserIdList = newProjects.stream()
                    .flatMap(project -> project.getTeams().stream())
                    .flatMap(team -> team.getTeamMembers().stream())
                    .map(TeamMember::getId)
                    .distinct()
                    .toList();
            newMoment.getUserIds().addAll(newUserIdList);
        }

        Set<Long> oldUserIds = new HashSet<>(oldMoment.getUserIds());
        Set<Long> newUserIds = new HashSet<>(newMoment.getUserIds());
        newUserIds.removeAll(oldUserIds);

        if (newUserIds.size() > 0) {
            newUserIds.forEach(userId -> {
                Project userProject = teamMemberJpaRepository.findById(userId)
                        .getTeam()
                        .getProject();
                if (!newMoment.getProjects().contains(userProject)) {
                    newMoment.getProjects().add(userProject);
                }
            });
        }
        return momentMapper.toDto(momentRepository.save(newMoment));
    }

    private Moment findById(Long id) {
        return momentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("moment by id: " + id + " not found"));
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
