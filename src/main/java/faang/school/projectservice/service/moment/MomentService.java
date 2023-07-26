package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidException;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final ProjectRepository projectRepository;

    @Transactional
    public MomentDto create(MomentDto momentDto) {
        validateMomentDto(momentDto);
        Moment moment = momentRepository.save(momentMapper.toEntity(momentDto));
        return momentMapper.toDto(moment);
    }

    @Transactional(readOnly = true)
    public MomentDto getMomentById(long momentId) {
        Moment moment = momentRepository.findById(momentId)
                .orElseThrow(() -> new IllegalArgumentException("Moment not found. Id: " + momentId));
        return momentMapper.toDto(moment);
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getAllMoments() {
        List<Moment> moments = momentRepository.findAll();
        return moments.stream().map(momentMapper::toDto).toList();
    }

    @Transactional
    public MomentDto update(MomentDto source) {
        validateMomentDto(source);

        Moment moment = momentRepository.findById(source.getId())
                .orElseThrow(() -> new IllegalArgumentException("Moment not found. Id: " + source.getId()));
        MomentDto target = momentMapper.toDto(moment);

        BeanUtils.copyProperties(source, target, "id");

        momentRepository.save(momentMapper.toEntity(target));
        return target;
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getMomentsByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return momentMapper.toListDto(momentRepository.findAllByDateRange(startDate, endDate));
    }

    @Transactional(readOnly = true)
    public List<MomentDto> getMomentsByProjects(List<Long> projectIds) {
        return momentMapper.toListDto(momentRepository.findAllByProjectIds(projectIds));
    }

    private void validateMomentDto(MomentDto momentDto) {
        if (momentDto.getId() == null || momentDto.getId() < 1) {
            throw new DataValidException("Illegal Id: " + momentDto.getId());
        }
        if (momentDto.getName().isBlank()) {
            throw new DataValidException("Moment must have name. Id: " + momentDto.getId());
        }
        if (momentDto.getProjects() == null || momentDto.getProjects().size() < 1) {
            throw new DataValidException("Moment must have project. Id: " + momentDto.getId());
        }
        if (momentDto.getCreatedBy() == null || momentDto.getCreatedBy() < 1) {
            throw new DataValidException("Moment must have a creator. Id: " + momentDto.getId());
        }
        if (momentDto.getDate() == null) {
            throw new DataValidException("Moment must have a date. Id: " + momentDto.getId());
        }
        if (momentDto.getProjects().stream().anyMatch(projectDto -> projectDto.getStatus().equals(ProjectStatus.CANCELLED)
                || projectDto.getStatus().equals(ProjectStatus.COMPLETED))) {
            throw new DataValidException("Unable to create moment with closed project. Id: " + momentDto.getId());
        }
        if (!checkMembersOfProjectsTeam(momentDto)) {
            throw new DataValidException("Some users are not in projects team. Id: " + momentDto.getId());
        }
    }

    private boolean checkMembersOfProjectsTeam(MomentDto momentDto) {
        return getMembersOfProjectsTeam(momentDto).equals(momentDto.getUserIds());
    }

    @Transactional(readOnly = true)
    private List<Long> getMembersOfProjectsTeam(MomentDto momentDto) {
        return momentDto.getProjects().stream()
                .map(ProjectDto::getId)
                .map(projectRepository::getProjectById)
                .map(Project::getTeam)
                .map(Team::getTeamMembers)
                .flatMap(List::stream)
                .map(TeamMember::getUserId)
                .toList();
    }
}
