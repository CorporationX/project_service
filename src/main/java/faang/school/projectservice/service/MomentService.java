package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.MomentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final MomentServiceValidator momentServiceValidator;
    private final List<MomentFilter> momentFilters;
    private final UserService userService;
    private final ProjectService projectService;


    @Transactional
    public MomentDto createMoment(MomentDto momentDto) {
        momentServiceValidator.validateCreateMoment(momentDto);

        momentRepository.save(momentMapper.toEntity(momentDto));
        return momentDto;
    }

    @Transactional
    public MomentDto updateMoment(MomentDto momentDto) {
        Moment moment = findMomentById(momentDto.getId());
        Moment updatedMomentWithProjects = updateProjects(moment, momentDto.getProjectsIds());
        Moment updatedMomentWithUsers = updateUserIds(updatedMomentWithProjects, momentDto.getUserIds());
        Moment finalUpdatedMoment = updateDescription(updatedMomentWithUsers, momentDto.getDescription());
        momentRepository.save(finalUpdatedMoment);
        return momentMapper.toDto(finalUpdatedMoment);
    }

    public List<MomentDto> getFilteredMoments(MomentFilterDto momentFilterDto) {
        List<Moment> moments = momentRepository.findAll();

        List<Moment> filteredMoments = momentFilters.stream()
                .filter(filter -> filter.isApplicable(momentFilterDto))
                .reduce(moments, (filtered, filter) ->
                        filter.apply(filtered.stream(), momentFilterDto).toList(), (a, b) -> b);

        return momentMapper.toDto(filteredMoments);
    }

    public List<MomentDto> getAllMoments() {
        return momentMapper.toDto(momentRepository.findAll());
    }

    public MomentDto getMomentById(Long id) {
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Moment not found"));


        return momentMapper.toDto(moment);
    }

    private Moment findMomentById(Long id) {
        return momentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Moment not found"));
    }

    private Moment updateProjects(Moment moment, List<Long> newProjectIds) {
        List<Project> differentProjects = projectService.findDifferentProjects(moment.getProjects(), newProjectIds);
        List<Long> updatedUserIds = new ArrayList<>(moment.getUserIds());
        if (!differentProjects.isEmpty()) {
            updatedUserIds.addAll(userService.getNewMemberIds(differentProjects));
        }
        return Moment.builder().id(moment.getId())
                .projects(moment.getProjects())
                .description(moment.getDescription())
                .userIds(updatedUserIds)
                .build();
    }

    private Moment updateUserIds(Moment moment, List<Long> newUserIds) {
        List<Long> differentMemberIds = userService.findDifferentMemberIds(moment.getUserIds(), newUserIds);
        List<Project> updatedProjects = new ArrayList<>(moment.getProjects());
        if (!differentMemberIds.isEmpty()) {
            updatedProjects.addAll(projectService.getNewProjects(differentMemberIds));
        }
        return Moment.builder()
                .id(moment.getId())
                .projects(updatedProjects)
                .description(moment.getDescription())
                .userIds(moment.getUserIds())
                .build();
    }

    private Moment updateDescription(Moment moment, String newDescription) {
        if (!moment.getDescription().equals(newDescription)) {
            return Moment.builder()
                    .id(moment.getId())
                    .projects(moment.getProjects())
                    .description(newDescription)
                    .userIds(moment.getUserIds())
                    .build();
        }
        return moment;
    }
}

