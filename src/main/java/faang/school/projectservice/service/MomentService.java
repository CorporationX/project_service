package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MomentService {

    private final MomentRepository momentRepository;

    public MomentDto createMoment(MomentDto momentDto, ProjectDto projectDto) {
        if (projectDto.getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot add a moment to a completed project.");
        }

        var projectDtoMoments = projectDto.getMoments();
        projectDtoMoments.add(momentDto);

        var momentDtoProjects = momentDto.getProjects();
        momentDtoProjects.add(projectDto);

        var moment = MomentMapper.INSTANCE.toEntity(momentDto);

        var savedMoment = momentRepository.save(moment);

        return MomentMapper.INSTANCE.toDto(savedMoment);
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        var maybeMoment = momentRepository.findById(momentDto.getId())
                    .orElseThrow(() -> new IllegalStateException("Cannot find moment"));

        updateUserIds(maybeMoment, momentDto.getUserIds());
        updateProject(maybeMoment, momentDto.getProjects());

        var savedMoment = momentRepository.save(maybeMoment);
        return MomentMapper.INSTANCE.toDto(savedMoment);
    }

    private void updateUserIds(Moment moment, List<Long> newUserIds) {
        var existingUserIds = moment.getUserIds();
        for (var userId : newUserIds) {
            if (!existingUserIds.contains(userId)) {
                existingUserIds.add(userId);
            }
        }
    }

    private void updateProject(Moment moment, List<ProjectDto> newProjectDtos) {
        var newProjects = newProjectDtos.stream()
                .map(ProjectMapper.INSTANCE::toEntity)
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
                .map(MomentMapper.INSTANCE::toDto)
                .toList();
    }


    public List<MomentDto> getMoments(MomentFilterDto filter) {
        return getAllMoments().stream()
                .filter(momentDto -> matchesFilter(momentDto, filter))
                .toList();
    }


    public MomentDto getMoment(long id) {
        return momentRepository.findById(id)
                .map(MomentMapper.INSTANCE::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Moment is not found"));
    }

    private boolean matchesFilter(MomentDto momentDto, MomentFilterDto filter) {
        if (filter.getProjects() != null && !filter.getProjects().isEmpty()) {
            if (momentDto.getProjects().stream().noneMatch(filter.getProjects()::contains)) {
                return false;
            }
        }
        if (filter.getUserIds() != null && !filter.getUserIds().isEmpty()) {
            if (momentDto.getUserIds().stream().noneMatch(filter.getUserIds()::contains)) {
                return false;
            }
        }
        if (filter.getId() != null && !filter.getId().equals(momentDto.getId())) return false;
        if (filter.getName() != null && !filter.getName().equals(momentDto.getName())) return false;
        if (filter.getCreatedAt() != null && !filter.getCreatedAt().equals(momentDto.getCreatedAt())) return false;
        if (filter.getUpdatedAt() != null && !filter.getUpdatedAt().equals(momentDto.getUpdatedAt())) return false;
        if (filter.getCreatedBy() != null && !filter.getCreatedBy().equals(momentDto.getCreatedBy())) return false;
        return filter.getUpdatedBy() == null || filter.getUpdatedBy().equals(momentDto.getUpdatedBy());
    }
}
