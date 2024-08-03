package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.MomentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper mapper = Mappers.getMapper(MomentMapper.class);
    private final ProjectRepository projectRepository;
    private final MomentValidator momentValidator;
    private final List<MomentFilter> momentFilters;

    public MomentDto createMoment(MomentDto momentDto) {
        Moment moment = mapper.toEntity(momentDto);
        processProjects(moment);
        moment.setDate(LocalDateTime.now());
        momentRepository.save(moment);
        return mapper.toDto(moment);
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        Moment momentToUpdate = momentRepository.findById(momentDto.getId()).orElseThrow(() -> new EntityNotFoundException("no moment with such Id"));
        mapper.update(momentDto, momentToUpdate);
        processProjects(momentToUpdate);
        momentRepository.save(momentToUpdate);
        return mapper.toDto(momentToUpdate);
    }

    public List<MomentDto> getMomentsFilteredByDateFromProjects(Long ProjectId, MomentFilterDto filters) {
        List<Moment> moments = momentRepository.findAllByProjectId(ProjectId);
        if (moments.isEmpty()) {
            throw new DataValidationException("zero moments with such id");
        }
        return momentFilters.stream().filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(moments, filters))
                .map(mapper::toDto).toList();
    }

    public List<MomentDto> getAllMoments(Long projectId) {
        List<Moment> moments = momentRepository.findAllByProjectId(projectId);
        if (moments.isEmpty()) {
            throw new DataValidationException("zero moments with such id");
        }
        return moments.stream().map(mapper::toDto).toList();
    }

    public MomentDto getMomentById(Long momentId) {
        Moment moment = momentRepository.findById(momentId).orElseThrow(() -> new DataValidationException("No moment with such id" + momentId));
        return mapper.toDto(moment);
    }

    private void processProjects(Moment moment) {
        momentValidator.validateMoment(moment);
        Set<Project> projects = new HashSet<>(moment.getProjects());
        Set<Long> userIds = new HashSet<>();
        if (moment.getUserIds() != null) {userIds.addAll(moment.getUserIds());}
        Set<Long> projectIds = moment.getProjects().stream().map(Project::getId).collect(Collectors.toSet());
        for (Long projectId : projectIds) {
            Project project = projectRepository.getProjectById(projectId);
            projects.add(project);
            if (project.getTeams() != null) {
                Set<Long> newUserIds = project.getTeams().stream().flatMap(team -> team.getTeamMembers().stream())
                        .map(TeamMember::getUserId).collect(Collectors.toSet());
                userIds.addAll(newUserIds);
            }
        }
        moment.setProjects(projects.stream().toList());
        moment.setUserIds(userIds.stream().toList());
    }

}
