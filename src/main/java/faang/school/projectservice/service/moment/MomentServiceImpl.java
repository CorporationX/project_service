package faang.school.projectservice.service.moment;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {

    private final MomentRepository momentRepository;
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final MomentMapper momentMapper;
    private final TeamMemberRepository teamMemberRepository;
    private static final int FIRST_MONTH_NUMBER = 1;
    private static final int LAST_MONTH_NUMBER = 12;

    @Override
    @Transactional
    public MomentDto createMoment(CreateMomentDto dto) {
        Long currentUserId = userContext.getUserId();
        List<Project> projects = projectRepository.findAllById(dto.projectIds());
        validateProjectsExistence(projects, dto.projectIds());
        validateProjectsAreActive(projects);

        Moment moment = momentMapper.toMoment(dto);
        moment.setProjects(projects);
        moment.setCreatedBy(currentUserId);
        moment.setUpdatedBy(currentUserId);
        moment.setDate(dto.date() != null ? dto.date() : LocalDateTime.now());

        momentRepository.save(moment);

        return momentMapper.toMomentDto(moment);
    }

    @Override
    @Transactional
    public MomentDto updateMoment(Long momentId, UpdateMomentDto dto) {
        Long currentUserId = userContext.getUserId();
        Moment moment = momentRepository.findById(momentId)
                .orElseThrow(() -> new EntityNotFoundException("Moment id: " + momentId + " not found"));

        moment.setName(dto.name());
        moment.setUpdatedBy(currentUserId);

        if (dto.description() != null) {
            moment.setDescription(dto.description());
        }

        if (dto.date() != null) {
            moment.setDate(dto.date());
        }

        List<Project> newProjects = projectRepository.findAllById(dto.projectIds());
        validateProjectsExistence(newProjects, dto.projectIds());
        validateProjectsAreActive(newProjects);

        LinkedHashSet<Project> mergedProjects = new LinkedHashSet<>(moment.getProjects());
        mergedProjects.addAll(newProjects);
        moment.setProjects(new ArrayList<>(mergedProjects));

        if (dto.memberIds() != null && !dto.memberIds().isEmpty()) {
            List<Long> validMemberIds = teamMemberRepository.findAllById(dto.memberIds())
                    .stream()
                    .map(TeamMember::getId)
                    .toList();

            if (validMemberIds.size() != dto.memberIds().size()) {
                throw new IllegalArgumentException("One or more team members not found");
            }

            LinkedHashSet<Long> mergedMembers = new LinkedHashSet<>(moment.getUserIds());
            mergedMembers.addAll(validMemberIds);
            moment.setUserIds(new ArrayList<>(mergedMembers));
        }

        momentRepository.save(moment);
        return momentMapper.toMomentDto(moment);
    }

    @Override
    public Page<MomentDto> getAllMoments(Pageable pageable) {
        return momentRepository.findAll(pageable)
                .map(momentMapper::toMomentDto);
    }

    @Override
    public MomentDto getMomentById(Long momentId) {
        Moment moment = momentRepository.findById(momentId)
                .orElseThrow(() -> new EntityNotFoundException("Moment id " + momentId + " not found"));

        return momentMapper.toMomentDto(moment);
    }

    @Override
    public Page<MomentDto> getMomentsByProject(
            Long projectId,
            Integer month,
            List<Long> partnerProjectIds,
            Pageable pageable
    ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project id " + projectId + " not found"));

        if (month != null && (month < FIRST_MONTH_NUMBER || month > LAST_MONTH_NUMBER)) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        List<Long> partnerIds = partnerProjectIds == null ? Collections.emptyList()
                : partnerProjectIds.stream().distinct().toList();

        if (!partnerIds.isEmpty()) {
            List<Project> foundPartners = projectRepository.findAllById(partnerIds);
            if (foundPartners.size() != partnerIds.size()) {
                throw new IllegalArgumentException("One or more partner projects were not found.");
            }
        }

        List<Moment> moments = momentRepository.findAllByProjectId(projectId);

        List<Moment> filtered = moments.stream()
                .filter(m -> {
                    if (month != null && (m.getDate() == null || m.getDate().getMonthValue() != month)) {
                        return false;
                    }
                    if (!partnerIds.isEmpty()) {
                        return m.getProjects().stream().anyMatch(p -> partnerIds.contains(p.getId()));
                    }
                    return true;
                })
                .toList();

        List<MomentDto> dtoMoments = filtered.stream()
                .map(momentMapper::toMomentDto)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtoMoments.size());
        List<MomentDto> content = dtoMoments.subList(Math.min(start, end), end);

        return new PageImpl<>(content, pageable, dtoMoments.size());
    }

    private void validateProjectsExistence(List<Project> projects, List<Long> projectIds) {
        if (projects.size() != projectIds.size()) {
            throw new IllegalArgumentException("One or more projects were not found.");
        }
    }

    private void validateProjectsAreActive(List<Project> projects) {
        projects.forEach(project -> {
            if (project.getStatus() == ProjectStatus.COMPLETED || project.getStatus() == ProjectStatus.CANCELLED) {
                throw new IllegalArgumentException("You cannot create a moment for a closed project: " +
                        project.getName());
            }
        });
    }
}
