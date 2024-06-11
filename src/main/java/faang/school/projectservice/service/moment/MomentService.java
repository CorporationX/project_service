package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
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
    private final MomentMapper momentMapper;
    private final MomentValidator momentValidator;
    private final List<MomentFilter> momentFilters;
    private final ProjectRepository projectRepository;


    @Transactional
    public MomentDto create(MomentDto momentDto) {
        momentValidator.momentHasProjectValidation(momentDto);
        momentValidator.projectNotCancelledValidator(momentDto.getProjectIds());

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

    private Moment findMomentById(Long id) {
        return momentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Moment not found"));
    }
}
