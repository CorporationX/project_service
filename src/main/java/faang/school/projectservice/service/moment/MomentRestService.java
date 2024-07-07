package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentRestDto;
import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.mapper.MomentRestMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import faang.school.projectservice.validation.moment.MomentRestValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentRestService {
    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final MomentRestMapper momentMapper;
    private final MomentRestValidator momentValidator;
    private final List<MomentFilter> momentFilters;


    @Transactional
    public MomentRestDto create(MomentRestDto momentDto) {
        momentValidator.momentHasProjectValidation(momentDto);
        momentValidator.projectNotCancelledValidator(momentDto.getProjects());

        return momentMapper.toDto(momentRepository.save(momentMapper.toEntity(momentDto)));
    }

    @Transactional
    public MomentRestDto update(Long momentId, MomentRestDto momentDto) {
        Moment findMomentById = momentRepository.findById(momentId)
                .orElseThrow(() -> new EntityNotFoundException("Moment not found"));

        momentValidator.projectsUpdateValidator(findMomentById, momentDto);
        momentValidator.membersUpdateValidator(findMomentById, momentDto);

        momentDto.setId(momentId);

        return momentMapper.toDto(momentRepository.save(momentMapper.toEntity(momentDto)));
    }

    @Transactional(readOnly = true)
    public List<MomentRestDto> getAllMomentsByFilters(Long projectId, MomentFilterDto filters) {
        var projectMomentList = projectRepository.getProjectById(projectId).getMoments();
        return momentFilters.stream()
                .filter(momentFilter -> momentFilter.isApplicable(filters))
                .flatMap(momentFilter -> momentFilter.apply(projectMomentList, filters))
                .distinct()
                .map(momentMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MomentRestDto> getAllMoments() {
        return momentMapper.toDtoList(momentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public MomentRestDto getMomentById(Long momentId) {
        Moment findMomentById = momentRepository.findById(momentId)
                .orElseThrow(() -> new EntityNotFoundException("Moment not found"));
        return momentMapper.toDto(findMomentById);
    }
}
