package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;

    @Transactional
    public MomentDto createMoment(MomentDto momentDto, Long projectId) {
        var project = projectRepository.getProjectById(projectId);

        if (project == null) {
            throw new EntityNotFoundException("Project not found");
        }else if (project.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot add a moment to a completed project.");
        }

        var moment = momentMapper.toEntity(momentDto);

        moment.getProjects().add(project);
        var savedMoment = momentRepository.save(moment);

        project.getMoments().add(savedMoment);
        projectRepository.save(project);

            return momentMapper.toDto(savedMoment);
    }

    @Transactional
    public MomentDto updateMoment(MomentDto momentDto) {
        var maybeMoment = momentRepository.findById(momentDto.getId())
                    .orElseThrow(() -> new IllegalStateException("Cannot find moment"));

        updateUserIds(maybeMoment, momentDto.getUserIds());
        updateProject(maybeMoment, momentDto.getProjects());

        var savedMoment = momentRepository.save(maybeMoment);
        return momentMapper.toDto(savedMoment);
    }

    private void updateUserIds(Moment moment, List<Long> newUserIds) {
        var existingUserIds = moment.getUserIds();
        for (var userId : newUserIds) {
            if (!existingUserIds.contains(userId)) {
                existingUserIds.add(userId);
            }
        }
    }

    private void updateProject(Moment moment, List<Long> newProjectsIds) {
        var newProjects = newProjectsIds.stream()
                .map(projectRepository::getProjectById)
                .toList();
        var existingProjects = moment.getProjects();
        for (var project : newProjects) {
            if (!existingProjects.contains(project)) {
                existingProjects.add(project);
            }
        }
    }

    public List<MomentDto> getAllMoments() {
        return momentRepository.findAll().stream()
                .map(momentMapper::toDto)
                .toList();
    }

    public List<MomentDto> getMoments(MomentFilterDto filters) {
        var moments = momentRepository.findAll().stream();

        return momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.getApplicableFilters(moments, filters))
                .map(momentMapper::toDto)
                .toList();
    }

    public MomentDto getMoment(long id) {
        return momentRepository.findById(id)
                .map(momentMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Moment is not found"));
    }
}
