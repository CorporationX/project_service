package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.EntityNotFoundException;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomentService {
    private final MomentRepository momentRepository;
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
//        moment.update(momentDto);
        log.info("Moment (id = {}) successfully updated and saved to database", moment.getId());
        return momentMapper.toDto(moment);
    }

    private void updateProjectsList(MomentDto momentDto, Moment moment) {
        List<Long> existingProjectIds = moment.getProjects().stream()
                .map(Project::getId)
                .toList();

        List<Long> addedProjectIds = momentDto.getProjectIds().stream()
                .filter(projectId -> !existingProjectIds.contains(projectId))
                .toList();

        if (addedProjectIds.size() > 0) {
            List<Project> addedProjects = addedProjectIds.stream()
                    .map(projectId -> Project.builder().id(projectId).build())
                    .toList();

            moment.getProjects().addAll(addedProjects);

            moment.getUserIds().addAll(
                    addedProjects.stream()
                            .flatMap(project -> project.getTeams().stream())
                            .flatMap(team -> team.getTeamMembers().stream())
                            .map(TeamMember::getUserId)
                            .toList()
            );
        }
    }
}
