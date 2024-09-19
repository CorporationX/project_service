package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.project.ProjectService;
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
    private final List<MomentFilter> momentFilters;
    private final ProjectService projectService;

    @Transactional
    public MomentDto createMoment(MomentDto momentDto) {
        Moment moment = prepareMoment(momentMapper.toEntity(momentDto), momentDto);

        momentRepository.save(moment);

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
        Moment moment = prepareMoment(getMomentById(momentId), momentDto);

        momentRepository.save(moment);

        return momentMapper.toDto(moment);
    }

    @Transactional
    public List<MomentDto> filterBy(Long projectId, MomentFilterDto filterDto) {
        List<Moment> moments = momentRepository.findAllByProjectId(projectId);

        return momentFilters.stream()
                .filter(f -> f.isApplicable(filterDto))
                .reduce(moments.stream(), (stream, filter) -> filter.apply(stream, filterDto), (s1, s2) -> s1)
                .map(momentMapper::toDto)
                .toList();
    }

    private Moment prepareMoment(Moment moment, MomentDto momentDto) {
        List<Project> projects = projectService.getProjectByIds(momentDto.getProjectIds());

        moment.setProjects(projects);
        moment.setUserIds(getUserIdsByProjects(projects));
        moment.setName(momentDto.getName());

        return moment;
    }

    private Moment getMomentById(long id) {
        return momentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Moment doesn't exist with this id"));
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
