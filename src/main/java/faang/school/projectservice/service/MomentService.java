package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.jpa.MomentJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.filters.MomentFilter;
import faang.school.projectservice.validation.MomentValidation;
import faang.school.projectservice.validation.ProjectValidation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class MomentService {

    private final MomentJpaRepository momentRepository;
    private final MomentValidation momentValidation;
    private final ProjectRepository projectRepository;
    private final ProjectValidation projectValidation;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;

    @Transactional
    public Moment create(long projectId, MomentDto momentDto) {
        projectValidation.checkProjectExists(projectId);
        momentValidation.nameIsFilled(momentDto.getName());
        projectValidation.checkProjectStatuses(projectId);
        Moment moment = momentRepository.save(momentMapper.dtoToMoment(momentDto));
        projectRepository.getProjectById(projectId).getMoments().add(moment);
        return moment;
    }

    @Transactional
    public Moment update(long momentId, MomentDto momentDto) {
        momentValidation.existsMoment(momentId);
        Moment moment = momentRepository.getReferenceById(momentId);
        momentMapper.updateMomentFromDto(momentDto, moment);
        momentRepository.save(moment);
        return moment;
    }

    @Transactional(readOnly = true)
    public List<Moment> getFilteredMomentsOfProject(long projectId, MomentFilterDto filters) {
        projectValidation.checkProjectExists(projectId);
        Supplier<Stream<Moment>> streamSupplier = () -> momentRepository.findAllByProjectId(projectId).stream();
        return momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(streamSupplier, filters))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Moment> getAllMoments(long projectId) {
        projectValidation.checkProjectExists(projectId);
        return momentRepository.findAllByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public Moment getMoment(long momentId) {
        momentValidation.existsMoment(momentId);
        return momentRepository.getReferenceById(momentId);
    }

    @Transactional
    public void delete(long momentId) {
        momentValidation.existsMoment(momentId);
        momentRepository.deleteById(momentId);
    }
}
