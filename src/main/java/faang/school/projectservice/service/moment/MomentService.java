package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.dto.moment.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MomentService {

    private final ProjectRepository projectRepository;
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> filters;

    public MomentDto createMoment(MomentDto momentDto) {
        for (ProjectDto projectDto : momentDto.getProjectDtos()) {
            if (projectDto.getStatus() == ProjectStatus.CANCELLED) {
                throw new DataValidationException(String.format(
                        "Project with id %s is canceled",
                        projectDto.getId()
                ));
            }

            if (projectDto.getId() == null) {
                throw new DataValidationException("Project doesn't have an id");
            }
        }

        return momentMapper.toDto(momentRepository
                .save(momentMapper.toEntity(momentDto)));
    }

    public List<MomentDto> getProjectMoments(ProjectDto projectDto, MomentFilterDto userMomentFilters) {
        if (!projectRepository.existsById(projectDto.getId())) {
            throw new DataValidationException(String.format(
                    "A project with id %s doesn't exist",
                    projectDto.getId()
            ));
        }

        Stream<Moment> projectMomentsStream = momentRepository.findAllByProjectId(projectDto.getId()).stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(userMomentFilters))
                .flatMap(filter -> filter.apply(userMomentFilters, projectMomentsStream))
                .map(momentMapper::toDto)
                .toList();
    }

    public List<MomentDto> getProjectMoments(ProjectDto projectDto) {
        if (!projectRepository.existsById(projectDto.getId())) {
            throw new DataValidationException(String.format(
                    "A project with id %s doesn't exist",
                    projectDto.getId()
            ));
        }

        return momentRepository.findAllByProjectId(projectDto.getId()).stream()
                .map(momentMapper::toDto)
                .toList();
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        // checks if the moment exist
        if (!momentRepository.existsById(momentDto.getId())) {
            throw new DataValidationException(String.format(
                    "A moment with id %s doesn't exist",
                    momentDto.getId()
            ));
        }

        Moment moment = momentRepository.findById(momentDto.getId()).get();

        Moment updatedMoment = momentMapper.updateEntityFromDto(momentDto, moment);

        return momentMapper.toDto(momentRepository.save(updatedMoment));
    }

    public MomentDto getMomentById(Long momentId) {
        if (!momentRepository.existsById(momentId)) {
            throw new DataValidationException(String.format(
                    "A moment with id %s doesn't exist",
                    momentId
            ));
        }

        return momentMapper.toDto(momentRepository.findById(momentId).get());
    }
}
