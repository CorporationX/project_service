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
        momentValidator.momentProjectValidation(momentDto);
        momentValidator.projectNotCancelledValidator(momentDto.getProjectIds());

        return momentMapper.toDto(momentRepository.save(momentMapper.toEntity(momentDto)));
    }

    @Transactional
    public MomentDto update(Long momentId, MomentDto momentDto) {
        Moment oldMoment = findMomentById(momentId);
        momentValidator.projectsUpdateValidator(oldMoment, momentDto);
        momentValidator.membersUpdateValidator(oldMoment, momentDto);

        momentDto.setId(momentId);

        return momentMapper.toDto(momentRepository.save(momentMapper.toEntity(momentDto)));
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getAllMomentsByFilters(Long projectId, MomentFilterDto filters) {
        return momentFilters.stream()
                .flatMap(filter -> filter.apply(projectRepository.getProjectById(projectId).getMoments().stream(), filters))
                .map(momentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getAllMoments() {
        return momentMapper.toListDto(momentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public MomentDto getMomentById(Long momentId) {
        return momentMapper.toDto(findMomentById(momentId));
    }

    private Moment findMomentById(Long id) {
        return momentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Moment not found"));
    }
}
