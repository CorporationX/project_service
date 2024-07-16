package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.moment.MomentRequestDto;
import faang.school.projectservice.dto.client.moment.MomentResponseDto;
import faang.school.projectservice.dto.client.moment.MomentUpdateDto;
import faang.school.projectservice.exception.ConflictException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.NotFoundException;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.util.CollectionsUtil;
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
                        findMissingMemberIdsForProjects(
                                momentRequestDto.getTeamMemberIds(), momentRequestDto.getProjectIds()
                        )
                );
                //todo: optimize - оба метода обращаются к БД за теми же данными (лишний запрос к БД)
            }
        }

        Moment moment = momentMapper.toEntity(momentRequestDto, projects);
        moment.setCreatedAt(LocalDateTime.now());
        moment.setCreatedBy(creatorId);

        moment = momentRepository.save(moment);
        return momentMapper.toResponseDto(moment);
    }

    public MomentResponseDto update(MomentUpdateDto momentUpdateDto, long userId) {
        Moment moment = momentRepository.findById(momentUpdateDto.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.MOMENT_NOT_EXIST));

        checkAndFillDependentFields(momentUpdateDto, moment);
        checkAndFillSimpleFields(momentUpdateDto, moment);

        moment.setUpdatedAt(LocalDateTime.now());
        moment.setUpdatedBy(userId);

        momentRepository.save(moment);
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

    private void validateTeamMembersFitProjects(Collection<Long> teamMemberIds, Collection<Long> projectIds) {
        // Лишний мембер / недостающий проект

        Set<Long> memberIdsInProjects =
                new HashSet<>(teamMemberRepository.findIdsByProjectIds(projectIds));
        boolean isValid = memberIdsInProjects.containsAll(teamMemberIds);
        if (!isValid) {
            throw new DataValidationException(ErrorMessage.MEMBERS_UNFIT_PROJECTS);
        }
    }

    private void validateProjectsFitTeamMembers(Collection<Long> projectIds, Collection<Long> teamMemberIds) {
        // Лишний проект / недостающие мемберы

        Set<Long> projectIdsFromMembers =
                projectRepository.findAllDistinctByTeamMemberIds(teamMemberIds).stream()
                        .map(Project::getId)
                        .collect(Collectors.toSet());
        boolean isValid = projectIdsFromMembers.containsAll(projectIds);
        if (!isValid) {
            throw new DataValidationException(ErrorMessage.PROJECTS_UNFIT_MEMBERS);
        }
    }

    private List<Long> findMissingMemberIdsForProjects(Collection<Long> teamMemberIds,
                                                       Collection<Long> projectIds) {
        // Находим таких мемберов, которые отсутствуют в переданном списке, но относятся к переданным проектам

        List<Long> memberIdsFromProjects = teamMemberRepository.findIdsByProjectIds(projectIds);
        return CollectionsUtil.leftOuterJoin(memberIdsFromProjects, teamMemberIds);
    }

    private List<Long> findExcessMemberIdsForProjects(Collection<Long> teamMemberIds,
                                                      Collection<Long> projectIds) {
        // Находим таких мемберов из переданного списка, которые не относятся к переданным проектам

        List<Long> memberIdsFromProjects = teamMemberRepository.findIdsByProjectIds(projectIds);
        return CollectionsUtil.leftOuterJoin(teamMemberIds, memberIdsFromProjects);
    }

    private List<Project> findMissingProjectsForMembers(Collection<Project> projects,
                                                        Collection<Long> teamMemberIds) {
        // Находим такие проекты, которые отсутствуют в переданном списке, но относятся к переданным мемберам

        List<Project> memberProjects = projectRepository.findAllDistinctByTeamMemberIds(teamMemberIds);
        return CollectionsUtil.leftOuterJoin(memberProjects, projects);
    }

    private List<Project> findExcessProjectsForMembers(Collection<Project> projects,
                                                       Collection<Long> teamMemberIds) {
        // Находим такие проекты из переданного списка, которые не относятся к переданным мемберам

        List<Project> memberProjects = projectRepository.findAllDistinctByTeamMemberIds(teamMemberIds);
        return CollectionsUtil.leftOuterJoin(projects, memberProjects);
    }

    private void checkAndFillDependentFields(MomentUpdateDto momentUpdateDto, Moment moment) {
        List<Long> newProjectIds = momentUpdateDto.getProjectIds();
        List<Long> newTeamMemberIds = momentUpdateDto.getTeamMemberIds();

        // Явно меняем и проекты и мемберов
        if (newProjectIds != null && newTeamMemberIds != null) {
            List<Project> newProjects = projectRepository.findAllByIds(newProjectIds);

            validateProjectStatuses(newProjects);
            validateTeamMembersFitProjects(newTeamMemberIds, newProjectIds);
            validateProjectsFitTeamMembers(newProjectIds, newTeamMemberIds);

            moment.setTeamMemberIds(newTeamMemberIds);
            moment.setProjects(newProjects);
        }

        // Явно меняем проекты, мемберов исходя из проектов
        if (newProjectIds != null && newTeamMemberIds == null) {
            List<Project> newProjects = projectRepository.findAllByIds(newProjectIds);
            validateProjectStatuses(newProjects);
            moment.setProjects(newProjects);
            // 1) проектов стало меньше (привязаны лишние пользователи, надо удалить)
            moment.getTeamMemberIds().removeAll(
                    findExcessMemberIdsForProjects(moment.getTeamMemberIds(), newProjectIds)
            );
            // 2) проектов стало больше (пользователей не хватает, надо добавить)
            moment.getTeamMemberIds().addAll(
                    findMissingMemberIdsForProjects(moment.getTeamMemberIds(), newProjectIds)
            );

            //todo: optimize - оба метода обращаются к БД за теми же данными (лишний запрос к БД)
        }

        // Явно меняем мемберов, проекты исходя из мемберов
        if (newProjectIds == null && newTeamMemberIds != null) {
            teamMemberRepository.checkExistAll(newTeamMemberIds);
            moment.setTeamMemberIds(newTeamMemberIds);
            // 1) Мемберов стало меньше, возможно появился лишний проект, надо удалить
            moment.getProjects().removeAll(
                    findExcessProjectsForMembers(moment.getProjects(), newTeamMemberIds)
            );
            // 2) Мемберов стало больше, возможно недостает проекта, надо добавить и провалидировать
            List<Project> extraProjects = findMissingProjectsForMembers(moment.getProjects(), newTeamMemberIds);
            validateProjectStatuses(extraProjects);
            moment.getProjects().addAll(extraProjects);

            //todo: optimize - оба метода обращаются к БД за теми же данными (лишний запрос к БД)
        }


    }

    private void checkAndFillSimpleFields(MomentUpdateDto momentUpdateDto, Moment moment) {
        if (momentUpdateDto.getName() != null) {
            moment.setName(momentUpdateDto.getName());
        }
        if (momentUpdateDto.getDescription() != null) {
            moment.setDescription(momentUpdateDto.getDescription());
        }
        if (momentUpdateDto.getDate() != null) {
            moment.setDate(momentUpdateDto.getDate());
        }
        if (momentUpdateDto.getImageId() != null) {
            moment.setImageId(momentUpdateDto.getImageId());
        }
    }
}
