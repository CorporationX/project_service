package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.filters.MomentFilter;
import faang.school.projectservice.validator.moment.MomentValidator;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
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
        momentValidator.MomentValidatorName(momentDto);
        projectValidator.ValidatorOpenProject(momentDto.getProjectIds());
        momentValidator.MomentValidatorProject(momentDto);
        Moment moment = momentMapper.toEntity(momentDto);
        return momentMapper.toDto(momentRepository.save(moment));
    }

    @Transactional
    public MomentDto update(MomentDto momentDto, Long momentId) {
        Moment moment = momentRepository.findById(momentId).orElseThrow();
        updateProjects(momentDto, moment);
        updateUsers(momentDto,moment);
        momentRepository.save(moment);
        return momentMapper.toDto(moment);
    }

    private void updateProjects(MomentDto momentDto, Moment moment){
        List<Long> oldUserIds = moment.getUserIds();
        List<Long> newUserIds = momentDto.getUserIds();
        List<Long> newProjectIds = momentDto.getProjectIds();
        List<Project> newProjects = projectRepository.findAllByIds(newProjectIds);
        if (!oldUserIds.equals(newUserIds)) {
            moment.setProjects(newProjects);
        }
    }

    private void updateUsers(MomentDto momentDto, Moment moment){
        List<Long> oldProjectIds = moment.getProjects().stream()
                .map(Project::getId)
                .toList();
        List<Long> newProjectIds = momentDto.getProjectIds();
        if (!oldProjectIds.equals(newProjectIds)) {
            moment.setUserIds(momentDto.getUserIds().stream()
                    .distinct()
                    .toList());
        }
    }

    public List<MomentDto> getAllMomentsByFilters(Long projectId, MomentFilterDto filters){
        Project project = projectRepository.getProjectById(projectId);
        Stream<Moment> momentStream = project.getMoments().stream();
        return momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(momentStream,filters))
                .map(momentMapper::toDto)
                .toList();
    }

    public List<MomentDto> getAllMoments(){
        return momentRepository.findAll()
                .stream()
                .map(momentMapper::toDto)
                .toList();
    }

    public MomentDto getMomentById(Long momentId){
        Moment moment = momentRepository.findById(momentId).orElseThrow(()-> new NoSuchElementException("Id is not found"));
        return momentMapper.toDto(moment);
    }
}


