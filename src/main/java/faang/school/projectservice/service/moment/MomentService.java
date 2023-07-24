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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final ProjectRepository projectRepository;

    public MomentDto create(MomentDto momentDto) {
        validateMomentDto(momentDto);
        Moment moment = momentRepository.save(momentMapper.toEntity(momentDto));
        return momentMapper.toDto(moment);
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
            throw new DataValidException("Some users are not in projects team. Id: \" + momentDto.getId()");
        }
    }

    private boolean checkMembersOfProjectsTeam(MomentDto momentDto) {
        return getMembersOfProjectsTeam(momentDto).equals(momentDto.getUserIds());
    }

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
