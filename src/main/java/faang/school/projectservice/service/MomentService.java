package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.MomentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class MomentService {
    private final MomentMapper mapper = Mappers.getMapper(MomentMapper.class);
    private final ProjectService projectService;
    private final MomentRepository momentRepository;
    private final List<MomentFilter> momentFilters;
    private final MomentValidator momentValidator;

    public MomentDto create(MomentDto momentDto) {
        Moment moment = mapper.toEntity(momentDto);
        moment.setProjects(getProjectsByIds(momentDto));
        moment.setUserIds(getMembers(momentDto, moment.getProjects()));
        return mapper.toDto(momentRepository.save(moment));
    }

    public MomentDto update(MomentDto momentDto) {
        Moment momentToUpdate = momentRepository.findById(momentDto.getId()).orElseThrow(() -> new EntityNotFoundException("no moment with such Id"));
        Moment updatedMoment = mapper.update(momentToUpdate, momentDto);
        updatedMoment.setProjects(getProjectsByIds(momentDto));
        updatedMoment.setUserIds(getMembers(momentDto, updatedMoment.getProjects()));
        return mapper.toDto(momentRepository.save(updatedMoment));
    }

    public List<MomentDto> getMomentsFiltered(Long projectId, MomentFilterDto filters) {
        List<Moment> moments = momentValidator.getMomentsAttachedToProject(projectId);
        return momentFilters.stream().filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(moments, filters))
                .map(mapper::toDto).toList();
    }

    public List<MomentDto> getAllMoments(Long projectId) {
        List<Moment> moments = momentValidator.getMomentsAttachedToProject(projectId);
        return moments.stream().map(mapper::toDto).toList();
    }

    public MomentDto getMomentById(Long momentId) {
        Moment moment = momentRepository.findById(momentId).orElseThrow(() -> new DataValidationException("No moment with such id" + momentId));
        return mapper.toDto(moment);
    }

    private List<Long> getMembers(MomentDto momentDto, List<Project> projects) {
        Set<Long> users;
        if (momentDto.getUserIds() == null) {users = new HashSet<>();}
        else {users = new HashSet<>(momentDto.getUserIds());}
        for (Project project : projects) {
            if (project.getTeams() != null) {
                Set<Long> newUserIds = project.getTeams().stream().flatMap(team -> team.getTeamMembers().stream())
                        .map(TeamMember::getUserId).collect(Collectors.toSet());
                users.addAll(newUserIds);
            }
        }
        return new ArrayList<>(users);
    }

    private List<Project> getProjectsByIds(MomentDto momentDto) {
        List<Long> projectIds = momentDto.getProjectIds();
        Set<Project> obtainedProjects = projectIds.stream().map(projectService::getProjectById).collect(Collectors.toSet());
        for (Project project : obtainedProjects) {
            momentValidator.validateProject(project);
        }
        return new ArrayList<>(obtainedProjects);

    }
}
