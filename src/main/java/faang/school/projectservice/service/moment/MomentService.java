package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.filters.MomentFilter;
import faang.school.projectservice.validation.project.ProjectValidator;
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
    private final MomentValidator momentValidator;
    private final ProjectValidator projectValidator;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;

    @Transactional
    public MomentDto create(MomentDto momentDto) {
        projectValidator.validatorOpenProject(momentDto.getProjectIds());
        momentValidator.validateProjectOfMoment(momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        return momentMapper.toDto(momentRepository.save(moment));
    }

    @Transactional
    public MomentDto update(MomentDto momentDto, Long momentId) {
        if (!momentRepository.existsById(momentId)) {
            throw new EntityNotFoundException("Moment for this " + momentId + " not found");
        }
        momentDto.setId(momentId);
        Moment moment = momentMapper.toEntity(momentDto);
        return momentMapper.toDto(momentRepository.save(moment));
    }

    public List<MomentDto> getAllMomentsByFilters(Long projectId, MomentFilterDto filters) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Moment> momentStream = project.getMoments().stream();
        return momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(momentStream, filters))
                .map(momentMapper::toDto)
                .toList();
    }

    public List<MomentDto> getAllMoments() {
        return momentMapper.toListDto(momentRepository.findAll());
    }

    public MomentDto getMomentById(Long momentId) {
        Moment moment = momentRepository.findById(momentId).orElseThrow(() -> new EntityNotFoundException("Moment for this" + momentId + "not found"));
        return momentMapper.toDto(moment);
    }
}


