package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ScheduleRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubProjectServiceImpl implements SubProjectService {
    private final SubProjectMapper subProjectMapper;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final StageRepository stageRepository;
    private final VacancyRepository vacancyRepository;
    private final MeetRepository meetRepository;
    private final ScheduleRepository scheduleRepository;

    @Override
    @Transactional
    public SubProjectDto create(long creatorId, CreateSubProjectDto createSubProjectDto) {
        Project subProjectToCreate = subProjectMapper.toSubProject(createSubProjectDto);
        subProjectToCreate.setOwnerId(creatorId);
        Project parentProject = projectRepository.findById(createSubProjectDto.parentProjectId()).get();
        if (parentProject.getOwnerId() != creatorId) {
            log.error("Пользователь с id: {} не является владельцем проекта с id: {}, " +
                    "он не может создавать от него подпроекты.",
                    creatorId, parentProject.getId());
            throw new ForbiddenException("Нельзя создать подпроект от чужого проекта.");
        }
        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE
                && createSubProjectDto.visibility() == ProjectVisibility.PUBLIC) {
            log.error("Проект с id: {} не может быть публичным, он создан от приватного проекта с id: {}.",
                    subProjectToCreate.getId(), parentProject.getId());
            throw new ForbiddenException("Нельзя создать публичный подпроект от приватного проекта.");
        }
        subProjectToCreate.setParentProject(parentProject);
        subProjectToCreate.setTeams(teamRepository.findAllById(createSubProjectDto.teamIds()));
        subProjectToCreate.setStages(stageRepository.findAllById(createSubProjectDto.stageIds()));
        subProjectToCreate.setVacancies(vacancyRepository.findAllById(createSubProjectDto.vacancyIds()));
        subProjectToCreate.setMeets(meetRepository.findAllById(createSubProjectDto.meetIds()));
        subProjectToCreate.setSchedule(scheduleRepository.findById(createSubProjectDto.scheduleId()).get());
        projectRepository.save(subProjectToCreate);
        log.info("Подпроект с id: {} успешно создан", subProjectToCreate.getId());
        return subProjectMapper.toSubProjectDto(subProjectToCreate);
    }

    @Override
    @Transactional
    public SubProjectDto update(long requesterId, long subProjectId, UpdateSubProjectDto updateSubProjectDto) {
        Project subProjectToUpdate = projectRepository.findById(subProjectId).get();
        if (subProjectToUpdate.getOwnerId() != requesterId) {
            log.error(
                    "Пользователь с id: {} не может редактировать проект с id: {}, он не является его владельцем",
                    requesterId, updateSubProjectDto.id());
            throw new ForbiddenException("Нельзя редактировать чужие подпроекты.");
        }
        Project parentProject = subProjectToUpdate.getParentProject();
        if (parentProject.getVisibility() == ProjectVisibility.PRIVATE
                && updateSubProjectDto.visibility() == ProjectVisibility.PUBLIC) {
            log.error("Проект с id: {} нельзя сделать публичным, он создан от приватного проекта с id: {}.",
                    subProjectToUpdate.getId(), parentProject.getId());
            throw new ForbiddenException("Нельзя сделать публичным подпроект, созданный от приватного проекта.");
        }
        if (updateSubProjectDto.visibility() == ProjectVisibility.PRIVATE
                && !subProjectToUpdate.getChildren().isEmpty()) {
            for (Project child : subProjectToUpdate.getChildren()) {
                child.setVisibility(ProjectVisibility.PRIVATE);
            }
        }
        subProjectMapper.update(updateSubProjectDto, subProjectToUpdate);
        subProjectToUpdate.setTeams(teamRepository.findAllById(updateSubProjectDto.teamIds()));
        subProjectToUpdate.setStages(stageRepository.findAllById(updateSubProjectDto.stageIds()));
        subProjectToUpdate.setVacancies(vacancyRepository.findAllById(updateSubProjectDto.vacancyIds()));
        subProjectToUpdate.setMeets(meetRepository.findAllById(updateSubProjectDto.meetIds()));
        subProjectToUpdate.setSchedule(scheduleRepository.findById(updateSubProjectDto.scheduleId()).get());
        projectRepository.save(subProjectToUpdate);
        log.info("Проект с id: {} успешно изменен пользователем с id: {}.",updateSubProjectDto.id(), requesterId);
        return subProjectMapper.toSubProjectDto(subProjectToUpdate);
    }

    @Override
    @Transactional
    public boolean complete(long requesterId, long subprojectId) {
        Project subProjectToComplete = projectRepository.findById(subprojectId).get();
        for (Project child : subProjectToComplete.getChildren()) {
            if (child.getStatus() != ProjectStatus.COMPLETED
                    && child.getStatus() != ProjectStatus.CANCELLED) {
                log.error("Подпроект с id: {} не может быть закрыт, у него есть незакрытые подпроекты.",
                        subprojectId);
                throw new ForbiddenException("Данный подпроект нельзя закрыть, у него есть незакрытые подпроекты.");
            }
        }
        if (subProjectToComplete.getOwnerId() != requesterId) {
            log.error("Пользователь с id: {} не может закрыть проект с id: {}, он не является его владельцем",
                    requesterId, subprojectId);
            throw new ForbiddenException("Нельзя закрыть чужой подпроект.");
        }
        subProjectToComplete.setStatus(ProjectStatus.COMPLETED);
        return true;
    }

    @Override
    public SubProjectDto getById(long subprojectId) {
        Optional<Project> optionalSubProject = projectRepository.findById(subprojectId);
        if (optionalSubProject.isPresent()) {
            return subProjectMapper.toSubProjectDto(optionalSubProject.get());
        }
        log.warn("Проект с id: {} не найден.", subprojectId);
        throw new EntityNotFoundException("Такого подпроекта нет.");
    }

    @Override
    public List<SubProjectDto> getAllByParentProject(long parentProjectId) {
        List<Project> subProjects = projectRepository.findAll()
                .stream()
                .filter(project -> project.getParentProject().getId() == parentProjectId)
                .toList();
        return subProjectMapper.toSubProjectList(subProjects);
    }

    @Override
    @Transactional
    public boolean delete(long requesterId, long subprojectId) {
        Project subProjectToDelete = projectRepository.findById(subprojectId).get();
        if (subProjectToDelete.getOwnerId() != requesterId) {
            log.error("У пользователя с id: {} нет прав на удаление проекта c id: {}.",
                    requesterId, subprojectId);
            throw new ForbiddenException("Нельзя удалить чужой подпроект.");
        }
        if (!subProjectToDelete.getChildren().isEmpty()) {
            log.error("Подпроект с id: {} не может быть удален, у него есть подпроекты.",
                    subprojectId);
            throw new ForbiddenException("Данный проект нельзя удалить, у него есть подпроекты.");
        }
        projectRepository.deleteById(subprojectId);
        log.info("Подпроект с id: {} успешно удален.", subprojectId);
        return true;
    }
}
