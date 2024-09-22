package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.validator.moment.MomentValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<Filter<MomentFilterDto, Moment>> momentFilters;
    private final MomentValidator momentValidator;
    private final ProjectService projectService;

    @Transactional
    public MomentDto createMoment(MomentDto momentDto) {
        Moment moment = createOrUpdateMoment(momentDto);

        return momentMapper.toDto(moment);
    }

    @Transactional
    public MomentDto getMomentDtoById(long id) {
        return momentMapper.toDto(getMomentById(id));
    }

    @Transactional
    public List<MomentDto> getAll() {
        return momentMapper.toDtoList(momentRepository.findAll());
    }

    @Transactional
    public MomentDto updateMoment(Long momentId, MomentDto momentDto) {
        if (momentDto.getId() == null) {
            momentDto.setId(momentId);
        } else if (!momentDto.getId().equals(momentId)) {
            throw new IllegalArgumentException("Moment Id in uri (" + momentId + ") and body (" + momentDto.getId() + ") do not match");
        }

        Moment moment = createOrUpdateMoment(momentDto);

        return momentMapper.toDto(moment);
    }

    @Transactional
    public List<MomentDto> filterBy(Long projectId, MomentFilterDto filterDto) {
        return momentFilters.stream()
                .filter(f -> f.isApplicable(filterDto))
                .reduce(
                        momentRepository.findAllByProjectId(projectId).stream(),
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1
                )
                .map(momentMapper::toDto)
                .toList();
    }

    private Moment createOrUpdateMoment(MomentDto momentDto) {
        Long momentId = momentDto.getId();
        Moment moment;

        if (momentId == null) {
            moment = momentMapper.toEntity(momentDto);
        } else {
            moment = getMomentById(momentId);
            moment.setName(momentDto.getName());
        }

        updateProjects(moment, momentDto.getProjectIds());

        return momentRepository.save(moment);
    }

    private void updateProjects(Moment moment, List<Long> projectIds) {
        List<Project> projects = projectService.getProjectByIds(projectIds);
        momentValidator.validateNoCancelledProjects(projects);
        moment.setProjects(projects);
        moment.setUserIds(getUserIdsByProjects(projects));
    }

    private Moment getMomentById(long id) {
        return momentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moment doesn't exist with this id " + id));
    }

    private List<Long> getUserIdsByProjects(List<Project> projects) {
        return projects.stream()
                .flatMap(project -> project.getTeams().stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .distinct()
                .toList();
    }
}
