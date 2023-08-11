package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final TeamRepository teamRepository;
    private final MomentValidator momentValidator;
    private final MomentMapper momentMapper;

    @Transactional
    public MomentDto create(MomentDto momentDto) {
        momentValidator.validateMomentProjects(momentDto);

        Moment moment = momentMapper.toEntity(momentDto);
        moment.setTeamMembers(getTeamMembersFromProjects(moment.getProjects()));

        log.info("Moment (id = {}) successfully created and saved to database", moment.getId());
        return momentMapper.toDto(momentRepository.save(moment));
    }

    @Transactional
    public MomentDto update(Long id, MomentDto momentDto) {
        momentValidator.validateMomentProjects(momentDto);
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moment with id = " + id + " does not exist"));

        moment.setName(momentDto.getName());
        if (Objects.nonNull(momentDto.getDescription())) {
            moment.setDescription(momentDto.getDescription());
        }
        moment.setDate(momentDto.getDate());
        if (hasDifferentProjects(moment, momentDto)) {
            updateProjects(momentDto.getProjectIds(), moment);
        }
        if (hasDifferentTeamMembers(moment, momentDto.getTeamMemberIds())) {
            updateTeamMembers(momentDto.getTeamMemberIds(), moment);
        }

        log.info("Moment (id = {}) successfully updated and saved to database", moment.getId());
        return momentMapper.toDto(moment);
    }

    private boolean hasDifferentProjects(Moment moment, MomentDto momentDto) {
        return !getProjectIds(moment.getProjects()).equals(momentDto.getProjectIds());
    }

    private boolean hasDifferentTeamMembers(Moment moment, List<Long> teamMemberIds) {
        return Objects.nonNull(teamMemberIds)
                && !teamMemberIds.isEmpty()
                && !getTeamMemberIds(moment.getTeamMembers()).equals(teamMemberIds);
    }

    private void updateProjects(List<Long> projectIds, Moment moment) {
        List<Long> existingProjectIds = getProjectIds(moment.getProjects());

        List<Project> addedProjects = projectIds.stream()
                .filter(projectId -> !existingProjectIds.contains(projectId))
                .map(projectRepository::getProjectById)
                .toList();

        moment.getProjects().addAll(addedProjects);
        moment.getTeamMembers().addAll(getTeamMembersFromProjects(addedProjects));

        List<Project> removedProjects = moment.getProjects().stream()
                .filter(project -> !projectIds.contains(project.getId()))
                .toList();

        moment.getProjects().removeAll(removedProjects);
        moment.getTeamMembers().removeAll(getTeamMembersFromProjects(removedProjects));
    }

    private void updateTeamMembers(List<Long> teamMemberIds, Moment moment) {
        List<Long> existingTeamMemberIds = getTeamMemberIds(moment.getTeamMembers());

        List<TeamMember> addedTeamMembers = teamMemberIds.stream()
                .filter(teamMemberId -> !existingTeamMemberIds.contains(teamMemberId))
                .map(teamMemberJpaRepository::getTeamMemberById)
                .toList();

        moment.getTeamMembers().addAll(addedTeamMembers);
        moment.getProjects().addAll(getProjectsFromTeamMembers(addedTeamMembers));

//        List<TeamMember> removedTeamMembers = teamMemberIds.stream()
//                .filter(userId -> !getTeamMemberIds(moment.getTeamMembers()).contains(userId))
//                .map(teamMemberJpaRepository::getTeamMemberById)
//                .toList();

        List<TeamMember> removedTeamMembers = moment.getTeamMembers().stream()
                .filter(teamMember -> !teamMemberIds.contains(teamMember.getId()))
                .toList();

        moment.getTeamMembers().removeAll(removedTeamMembers);
        moment.getProjects().removeAll(getProjectsFromTeamMembers(removedTeamMembers));
    }

    private List<Project> getProjectsFromTeamMembers(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(teamMember -> teamMember.getTeam().getProject())
                .distinct()
                .toList();
    }

    private List<TeamMember> getTeamMembersFromProjects(List<Project> projects) {
        return projects.stream()
                .flatMap(project -> teamRepository.findTeamsByProjectId(project.getId()).stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .toList();
    }

    private List<Long> getProjectIds(List<Project> projects) {
        return projects.stream()
                .map(Project::getId)
                .toList();
    }

    private List<Long> getTeamMemberIds(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
