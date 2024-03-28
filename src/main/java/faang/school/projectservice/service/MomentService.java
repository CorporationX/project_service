package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.MomentFilterValidator;
import faang.school.projectservice.validator.MomentValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;

    private final MomentMapper momentMapper;
    private final MomentValidator momentValidator;
    private final MomentFilterValidator momentFilterValidator;
    private final List<MomentFilter> momentFilters;

    @Transactional
    public MomentDto create(@Valid MomentDto momentDto) {
        momentValidator.validateMoment(momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        addProjects(moment, momentDto);
        moment = momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }

    public MomentDto get(long momentId) {
        return momentMapper.toDto(momentRepository.findById(momentId).orElseThrow(() -> new DataValidationException("Invalid moment id")));
    }

    public List<MomentDto> getAll() {
        return momentMapper.toDto(momentRepository.findAll());
    }

    public List<MomentDto> filter(MomentFilterDto momentFilterDto) {
        momentFilterValidator.validateFilter(momentFilterDto);
        Stream<Moment> momentStream = momentRepository.findAll().stream();
        for (MomentFilter momentFilter : momentFilters) {
            if (momentFilter.isApplicable(momentFilterDto)) {
                momentStream = momentFilter.apply(momentStream, momentFilterDto);
            }
        }
        return momentMapper.toDto(momentStream.toList());
    }

    @Transactional
    public MomentDto update(@Valid MomentDto momentDto) {
        momentValidator.validateMoment(momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        addProjects(moment, momentDto);
        moment = momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }

    private void addProjects(Moment moment, MomentDto momentDto) {
        List<Project> projects = new ArrayList<>();
        for (long projectId : momentDto.getProjectIds()) {
            projects.add(projectRepository.getProjectById(projectId));
        }
        moment.setProjects(projects);
    }

}
