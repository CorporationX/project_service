package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.moment.MomentRequestDto;
import faang.school.projectservice.dto.client.moment.MomentResponseDto;
import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MomentService {

    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;

    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public MomentResponseDto addNew(MomentRequestDto momentRequestDto, long creatorId) {
        List<Project> projects;
        //если проекты пришли == null - заполняем их проектами пришедших мемберов
        if (momentRequestDto.getProjectIds() == null) {
            teamMemberRepository.checkExistAll(momentRequestDto.getTeamMemberIds());
            projects = projectRepository
                    .findAllDistinctByTeamMemberIds(momentRequestDto.getTeamMemberIds());
            validateProjectStatuses(projects);
        }
        // проекты не null
        else {
            projects = projectRepository.findAllByIds(momentRequestDto.getProjectIds());
            validateProjectStatuses(projects);
            // если проекты не null, мемберы null - вытягиваем мемберов из проектов
            if (momentRequestDto.getTeamMemberIds() == null) {
                List<Long> teamMemberIds = teamMemberRepository
                        .findIdsByProjectIds(momentRequestDto.getProjectIds());
                momentRequestDto.setTeamMemberIds(teamMemberIds);
            }
            // проекты не null, мемберы не null
            else {
                // проверяем что нам пришли мемберы из нужных проектов
                validateTeamMembersFitProjects(
                        momentRequestDto.getTeamMemberIds(), momentRequestDto.getProjectIds()
                );
                // предусматриваем ситуацию, когда среди мемберов нету участников определенных
                // проектов - заполняем всеми участниками таких проектов (согласно ТЗ)
                momentRequestDto.getTeamMemberIds().addAll(
                        findMembersForEmptyProjects(
                                momentRequestDto.getTeamMemberIds(), momentRequestDto.getProjectIds()
                        )
                );
            }
        }

        Moment moment = momentMapper.toEntity(momentRequestDto, projects);
        moment.setCreatedAt(LocalDateTime.now());
        moment.setCreatedBy(creatorId);

        moment = momentRepository.save(moment);
        return momentMapper.toResponseDto(moment);
    }

    private void validateProjectStatuses(Collection<Project> projects) {
        boolean isValid = projects.stream()
                .noneMatch(project -> project.getStatus() == ProjectStatus.COMPLETED
                        || project.getStatus() == ProjectStatus.CANCELLED);
        if (!isValid) {
            throw new ConflictException(ErrorMessage.PROJECT_STATUS_INVALID);
        }
    }

    private void validateTeamMembersFitProjects(List<Long> teamMemberIds, List<Long> projectIds) {
        Set<Long> memberIdsInProjects =
                new HashSet<>(teamMemberRepository.findIdsByProjectIds(projectIds));
        boolean isValid = memberIdsInProjects.containsAll(teamMemberIds);
        if (!isValid) {
            throw new DataValidationException(ErrorMessage.MEMBERS_UNFIT_PROJECTS);
        }
    }

    private List<Long> findMembersForEmptyProjects(List<Long> teamMemberIds,
                                                   List<Long> projectIds) {
        Set<Long> filledProjectsIds =
                projectRepository.findAllDistinctByTeamMemberIds(teamMemberIds)
                        .stream()
                        .map(Project::getId)
                        .collect(Collectors.toSet());

        List<Long> emptyProjectIds = projectIds.stream()
                .filter(projectId -> !filledProjectsIds.contains(projectId))
                .toList();

        return teamMemberRepository.findIdsByProjectIds(emptyProjectIds);
    }
}
