package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomentService {
    private final MomentRepository momentRepository;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final MomentValidator momentValidator;
    private final MomentMapper momentMapper;

    @Transactional
    public MomentDto create(MomentDto momentDto) {
        momentValidator.validateMomentProjects(momentDto);
        Moment moment = momentRepository.save(momentMapper.toEntity(momentDto));
        log.info("Moment (id = {}) successfully created and saved to database", moment.getId());
        return momentMapper.toDto(moment);
    }

    @Transactional
    public MomentDto update(Long id, MomentDto momentDto) {
        momentValidator.validateMomentProjects(momentDto);
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moment with id = " + id + " does not exist"));

        updateMoment(momentDto, moment);
        updateProjects(momentDto, moment);
        updateUsers(momentDto, moment);

        log.info("Moment (id = {}) successfully updated and saved to database", moment.getId());
        return momentMapper.toDto(moment);
    }

    private void updateMoment(MomentDto momentDto, Moment moment) {
        if (!Objects.equals(momentDto.getName(), moment.getName())) {
            moment.setName(momentDto.getName());
        }
        if (Objects.nonNull(momentDto.getDescription())) {
            moment.setDescription(momentDto.getDescription());
        }
        if (!Objects.equals(momentDto.getDate(), moment.getDate())) {
            moment.setDate(momentDto.getDate());
        }
    }

    private void updateProjects(MomentDto momentDto, Moment moment) {
        List<Long> newProjectIds = getNewProjectIds(momentDto, moment);

        if (!newProjectIds.isEmpty()) {
            List<Project> newProjects = newProjectIds.stream()
                    .map(projectId -> Project.builder().id(projectId).build())
                    .toList();

            moment.getProjects().addAll(newProjects);

            List<Long> newUserIds = newProjects.stream()
                    .flatMap(project -> project.getTeams().stream())
                    .flatMap(team -> team.getTeamMembers().stream())
                    .map(TeamMember::getUserId)
                    .filter(userId -> !moment.getUserIds().contains(userId))
                    .toList();

            moment.getUserIds().addAll(newUserIds);
        }
    }

    private void updateUsers(MomentDto momentDto, Moment moment) {
        List<Long> newUserIds = getNewUserIds(momentDto, moment);

        if (!newUserIds.isEmpty()) {
            moment.getUserIds().addAll(newUserIds);

            List<Project> newProjects = newUserIds.stream()
                    .flatMap(userId -> teamMemberJpaRepository.findById(userId).stream())
                    .map(teamMember -> teamMember.getTeam().getProject())
                    .toList();

            moment.getProjects().addAll(newProjects);
        }
    }

    private List<Long> getNewProjectIds(MomentDto momentDto, Moment moment) {
        if (Objects.nonNull(momentDto.getProjectIds())) {
            return momentDto.getProjectIds().stream()
                    .filter(projectId -> !moment.getProjects().stream()
                            .map(Project::getId)
                            .toList()
                            .contains(projectId))
                    .toList();
        }
        return Collections.emptyList();
    }

    private List<Long> getNewUserIds(MomentDto momentDto, Moment moment) {
        if (Objects.nonNull(momentDto.getUserIds())) {
            return momentDto.getUserIds().stream()
                    .filter(userId -> !moment.getUserIds().contains(userId))
                    .toList();
        }
        return Collections.emptyList();
    }
}
