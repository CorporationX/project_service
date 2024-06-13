package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import faang.school.projectservice.validation.moment.MomentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final MomentMapper momentMapper;
    private final MomentValidator momentValidator;
    private final List<MomentFilter> momentFilters;


    @Transactional
    public MomentDto create(MomentDto momentDto) {
        momentValidator.momentHasProjectValidation(momentDto);
        momentValidator.projectNotCancelledValidator(momentDto.getProjects());

        return momentMapper.toDto(momentRepository.save(momentMapper.toEntity(momentDto)));
    }

    @Transactional
    public MomentDto update(Long momentId, MomentDto momentDto) {
        momentValidator.projectsUpdateValidator(findMomentById(momentId), momentDto);
        momentValidator.membersUpdateValidator(findMomentById(momentId), momentDto);

        momentDto.setId(momentId);

        return momentMapper.toDto(momentRepository.save(momentMapper.toEntity(momentDto)));
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getAllMomentsByFilters(Long projectId, MomentFilterDto filters) {
        Stream<Moment> projectMomentStream = projectRepository.getProjectById(projectId).getMoments().stream();
        return momentFilters.stream()
                .filter(momentFilter -> momentFilter.isApplicable(filters))
                .flatMap(filter -> filter.apply(projectMomentStream, filters))
                .map(momentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getAllMoments() {
        return momentMapper.toDtoList(momentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public MomentDto getMomentById(Long momentId) {
        return momentMapper.toDto(findMomentById(momentId));
    }

    public MomentDto createMoment(MomentDto momentDto) {
        Moment momentEntity = momentMapper.toEntity(momentDto);
        fillMoment(momentEntity, momentDto.getUserIds());

        return momentMapper.toDto(momentRepository.save(momentEntity));
    }

    private void fillMoment(Moment moment, List<Long> projectsIds) {
        moment.setProjects(projectRepository.findAllByIds(projectsIds));
    }

    private Moment findMomentById(Long id) {
        return momentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Moment not found"));
    }
}
