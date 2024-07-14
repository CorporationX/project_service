package faang.school.projectservice.service.project.update_subproject_param;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateSubProjectStatus implements UpdateSubProjectParam {
    private final ProjectMapper mapper;
    private final MomentRepository momentRepository;
    @Override
    public boolean isExecutable(UpdateSubProjectDto paramDto) {
        return paramDto.getStatus() != null;
    }

    @Override
    public void execute(ProjectDto projectDto, UpdateSubProjectDto paramDto) {
        ProjectStatus status = paramDto.getStatus();
        for (ProjectDto children : projectDto.getChildren()) {
            if (status != children.getStatus()) {
                throw new IllegalArgumentException("Не все подпроекты имеют указанный статус");
            }

            if (status == ProjectStatus.COMPLETED) {
                // ? непонятно надо ли добавлять проекты подпроектов в момент или только текущий проект
                // ? непонятно надо ли добавлять участников подпроекта в момент или только текущий участников
                Moment moment = addProjectToMomentComplited(projectDto);
                projectDto.getMomentIds().add(moment.getId());
            }
            projectDto.setStatus(status);

        }
    }

    private Moment addProjectToMomentComplited(ProjectDto projectDto) {
        String completedMomentName = "Выполнены все подпроекты";
        Moment moment = momentRepository.findByName(completedMomentName);
        if (moment == null) {
            moment = new Moment();
            moment.setName(completedMomentName);
            moment.setDescription(completedMomentName);
            moment.setDate(LocalDateTime.now());

            List<Resource> resources = projectDto.getResourceIds().stream()
                    .map(id -> {
                        Resource resource = new Resource();
                        resource.setId(id);
                        return resource;
                    }).toList();
            moment.setResource(resources);
            List<Long> userIds = projectDto.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream())
                    .map(TeamMemberDto::getUserId)
                    .toList();
            moment.setUserIds(userIds);
            moment.setProjects(new ArrayList<>(List.of(mapper.toEntity(projectDto))));
            moment.setCreatedAt(LocalDateTime.now());
            moment.setUpdatedAt(LocalDateTime.now());
            // не понятно чем заполнять createdBy и updatedBy
            return momentRepository.save(moment);
        } else {
            return moment;
        }
    }
}
