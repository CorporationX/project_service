package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentRequestDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateDto;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.moment.MomentRepository;
import faang.school.projectservice.service.utilservice.MomentUtilService;
import faang.school.projectservice.service.utilservice.ProjectUtilService;
import faang.school.projectservice.service.utilservice.TeamMemberUtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MomentService {

    private final MomentUtilService momentUtilService;
    private final MomentMapper momentMapper;
    private final TeamMemberUtilService teamMemberUtilService;
    private final ProjectUtilService projectUtilService;
    private final MomentRepository momentRepository;

    @Transactional(readOnly = true)
    public MomentResponseDto getById(long id) {
        Moment moment = momentUtilService.getById(id);

        return momentMapper.toResponseDto(moment);
    }

    @Transactional(readOnly = true)
    public List<MomentResponseDto> getAll() {
        List<Moment> moments = momentUtilService.getAll();
        return momentMapper.toResponseDtoList(moments);
    }

    @Transactional(readOnly = true)
    public List<MomentResponseDto> getAllFilteredByProjectId(long projectId, MomentFilterDto filter) {
        List<Moment> moments;
        if (filter == null) {
            moments = momentUtilService.findAllByProjectId(projectId);
        } else {
            moments = momentUtilService.findAllByProjectIdAndDateBetween(
                    projectId, filter.getStart(), filter.getEndExclusive());

            if (filter.getPartnerProjectIds() != null) {
                moments = filterByProjects(moments, filter.getPartnerProjectIds());
            }
        }

        return momentMapper.toResponseDtoList(moments);
    }

    public MomentResponseDto addNew(MomentRequestDto momentRequestDto, long creatorId) {
        List<Project> projects;
        //если проекты пришли == null - заполняем их проектами пришедших мемберов
        if (momentRequestDto.getProjectIds() == null) {
            teamMemberUtilService.checkExistAllByIds(momentRequestDto.getTeamMemberIds());
            projects = projectUtilService
                    .findAllDistinctByTeamMemberIds(momentRequestDto.getTeamMemberIds());
            projectUtilService.checkProjectsNotClosed(projects);
        }
        // проекты не null
        else {
            projects = projectUtilService.getAllByIdsStrictly(momentRequestDto.getProjectIds());
            projectUtilService.checkProjectsNotClosed(projects);
            // если проекты не null, мемберы null - вытягиваем мемберов из проектов
            if (momentRequestDto.getTeamMemberIds() == null) {
                List<Long> teamMemberIds = teamMemberUtilService
                        .findIdsByProjectIds(momentRequestDto.getProjectIds());
                momentRequestDto.setTeamMemberIds(teamMemberIds);
            }
            // проекты не null, мемберы не null
            else {
                // проверяем что нам пришли мемберы из нужных проектов
                teamMemberUtilService.checkTeamMembersFitProjects(
                        momentRequestDto.getTeamMemberIds(), momentRequestDto.getProjectIds()
                );
                // предусматриваем ситуацию, когда среди мемберов нету участников определенных
                // проектов - заполняем всеми участниками таких проектов (согласно ТЗ)
                momentRequestDto.getTeamMemberIds().addAll(
                        findMissingMemberIdsForProjects(
                                momentRequestDto.getTeamMemberIds(), momentRequestDto.getProjectIds()
                        )
                );
            }
        }

        Moment moment = momentMapper.toEntity(momentRequestDto, projects);
        moment.setCreatedAt(LocalDateTime.now());
        moment.setCreatedBy(creatorId);

        moment = momentUtilService.save(moment);
        return momentMapper.toResponseDto(moment);
    }

    public MomentResponseDto update(MomentUpdateDto momentUpdateDto, long userId) {
        Moment moment = momentUtilService.getById(momentUpdateDto.getId());

        checkAndFillDependentFields(momentUpdateDto, moment);
        checkAndFillSimpleFields(momentUpdateDto, moment);

        moment.setUpdatedAt(LocalDateTime.now());
        moment.setUpdatedBy(userId);

        momentUtilService.save(moment);
        return momentMapper.toResponseDto(moment);
    }

    private List<Moment> filterByProjects(List<Moment> moments, List<Long> projectIds) {
        Set<Long> projectIdsSet = new HashSet<>(projectIds);

        return moments.stream()
                .filter(moment -> moment.getProjects().stream()
                        .anyMatch(project ->
                                projectIdsSet.contains(project.getId())))
                .toList();
    }

    private List<Long> findMissingMemberIdsForProjects(Collection<Long> teamMemberIds,
                                                       Collection<Long> projectIds) {
        // Находим таких мемберов, которые отсутствуют в переданном списке, но относятся к переданным проектам

        List<Long> memberIdsFromProjects = teamMemberUtilService.findIdsByProjectIds(projectIds);
        return (List<Long>) CollectionUtils.subtract(memberIdsFromProjects, teamMemberIds);
    }

    private List<Long> findExcessMemberIdsForProjects(Collection<Long> teamMemberIds,
                                                      Collection<Long> projectIds) {
        // Находим таких мемберов из переданного списка, которые не относятся к переданным проектам

        List<Long> memberIdsFromProjects = teamMemberUtilService.findIdsByProjectIds(projectIds);
        return (List<Long>) CollectionUtils.subtract(teamMemberIds, memberIdsFromProjects);
    }

    private List<Project> findMissingProjectsForMembers(Collection<Project> projects,
                                                        Collection<Long> teamMemberIds) {
        // Находим такие проекты, которые отсутствуют в переданном списке, но относятся к переданным мемберам

        List<Project> memberProjects = projectUtilService.findAllDistinctByTeamMemberIds(teamMemberIds);
        return (List<Project>) CollectionUtils.subtract(memberProjects, projects);
    }

    private List<Project> findExcessProjectsForMembers(Collection<Project> projects,
                                                       Collection<Long> teamMemberIds) {
        // Находим такие проекты из переданного списка, которые не относятся к переданным мемберам

        List<Project> memberProjects = projectUtilService.findAllDistinctByTeamMemberIds(teamMemberIds);
        return (List<Project>) CollectionUtils.subtract(projects, memberProjects);
    }

    private void checkAndFillDependentFields(MomentUpdateDto momentUpdateDto, Moment moment) {
        List<Long> newProjectIds = momentUpdateDto.getProjectIds();
        List<Long> newTeamMemberIds = momentUpdateDto.getTeamMemberIds();

        if (newProjectIds != null && newTeamMemberIds != null) {
            // Явно меняем и проекты и мемберов=
            List<Project> newProjects = projectUtilService.getAllByIdsStrictly(newProjectIds);

            projectUtilService.checkProjectsNotClosed(newProjects);
            projectUtilService.checkProjectsFitTeamMembers(newProjectIds, newTeamMemberIds);
            teamMemberUtilService.checkTeamMembersFitProjects(newTeamMemberIds, newProjectIds);

            moment.setTeamMemberIds(newTeamMemberIds);
            moment.setProjects(newProjects);
        } else if (newProjectIds != null) {
            // Явно меняем проекты, мемберов исходя из проектов
            List<Project> newProjects = projectUtilService.getAllByIdsStrictly(newProjectIds);
            projectUtilService.checkProjectsNotClosed(newProjects);
            moment.setProjects(newProjects);

            // 1) проектов стало меньше (привязаны лишние пользователи, надо удалить)
            moment.getTeamMemberIds().removeAll(
                    findExcessMemberIdsForProjects(moment.getTeamMemberIds(), newProjectIds)
            );
            // 2) проектов стало больше (пользователей не хватает, надо добавить)
            moment.getTeamMemberIds().addAll(
                    findMissingMemberIdsForProjects(moment.getTeamMemberIds(), newProjectIds)
            );
        } else if (newTeamMemberIds != null) {
            // Явно меняем мемберов, проекты исходя из мемберов
            teamMemberUtilService.checkExistAllByIds(newTeamMemberIds);
            moment.setTeamMemberIds(newTeamMemberIds);

            // 1) Мемберов стало меньше, возможно появился лишний проект, надо удалить
            moment.getProjects().removeAll(
                    findExcessProjectsForMembers(moment.getProjects(), newTeamMemberIds)
            );
            // 2) Мемберов стало больше, возможно недостает проекта, надо добавить и провалидировать
            List<Project> extraProjects = findMissingProjectsForMembers(moment.getProjects(), newTeamMemberIds);
            projectUtilService.checkProjectsNotClosed(extraProjects);
            moment.getProjects().addAll(extraProjects);
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

    public void addMomentToAccomplishedProject(Project project, List<Moment> moments, Long userId) {
        List<Moment> momentsOfCompletedProjects;
        if (moments.isEmpty()) {
            momentsOfCompletedProjects = addMomentToList(project.getId(), new ArrayList<>(), userId);
        } else {
            momentsOfCompletedProjects = addMomentToList(project.getId(), moments, userId);
        }
        project.setMoments(momentsOfCompletedProjects);
        log.info("Moment was set successfully for project id = {}", project.getId());
        momentRepository.saveAll(moments);
    }

    private List<Moment> addMomentToList(long id, List<Moment> moments, long userId) {
        Moment moment = Moment.builder()
                .name("project " + id + " has been completed")
                .updatedBy(userId)
                .createdBy(userId)
                .build();
        List<Moment> list = new ArrayList<>(moments);
        list.add(moment);
        return list;
    }
}
