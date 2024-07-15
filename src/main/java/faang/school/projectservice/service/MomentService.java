package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.jpa.MomentJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.MomentValidator;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class MomentService {
    private final MomentJpaRepository momentRepository;
    private final MomentValidator momentValidator;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;

    @Transactional
    public MomentDto create(MomentDto momentDto) {
        momentValidator.validateMoment(momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        populateMomentProject(moment, momentDto);
        populateMomentTeamMembers(moment);
        momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }

    @Transactional
    public MomentDto update(MomentDto momentDto) {
        momentValidator.existsMoment(momentDto.getId());
        momentValidator.validateMoment(momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        populateMomentProject(moment, momentDto);
        populateMomentTeamMembers(moment);
        momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getFilteredMomentsOfProject(MomentFilterDto filters) {

        Supplier<Stream<Moment>> streamSupplier = () -> momentRepository.findAll().stream();
        return momentMapper.toDtoList(momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(streamSupplier, filters))
                .toList());
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getAllMoments() {
        return momentMapper.toDtoList(momentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public MomentDto getMoment(@Min(1) long momentId) {
        momentValidator.existsMoment(momentId);
        return momentMapper.toDto(momentRepository.getReferenceById(momentId));
    }

    @Transactional
    public void delete(long momentId) {
        momentValidator.existsMoment(momentId);
        momentRepository.deleteById(momentId);
    }

    private void populateMomentProject(Moment moment, MomentDto momentDto) {
        Set<Project> projects = new HashSet<>();
        if (momentDto.getProjectsId() != null && !momentDto.getProjectsId().isEmpty()) {
            for (long projectId : momentDto.getProjectsId()) {
                projects.add(projectRepository.getProjectById(projectId));
            }
        }
        if (momentDto.getUsersId() != null && !momentDto.getUsersId().isEmpty()) {
            for (long teamMemberId : momentDto.getUsersId()) {
                projects.addAll(projectRepository.findProjectByTeamMember(teamMemberId));
            }
        }
        moment.setProjects(projects.stream().toList());
    }

    private void populateMomentTeamMembers(Moment moment) {
        Set<Long> teamMemberIds = new HashSet<>();
        List<Long> projectIds = moment.getProjects().stream().map(Project::getId).toList();
        for (long projectId : projectIds) {
            teamMemberIds.addAll(teamMemberRepository.findAllByProjectId(projectId).stream()
                    .map(TeamMember::getId).toList());
        }
        moment.setUserIds(teamMemberIds.stream().toList());
    }
}
