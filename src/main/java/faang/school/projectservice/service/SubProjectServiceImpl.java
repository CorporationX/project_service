package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.ChildrenNotFinishedException;
import faang.school.projectservice.util.ThrowingConsumer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static faang.school.projectservice.model.ProjectStatus.CANCELLED;
import static faang.school.projectservice.model.ProjectStatus.COMPLETED;

@Service
public class SubProjectServiceImpl implements SubProjectService {
    private ProjectRepository repository;
    private ProjectMapper projectMapper;

    public CreateSubProjectDto createSubProject(CreateSubProjectDto subProjectDto) {
        repository.getProjectById(subProjectDto.getParentProjectId());
        var project = repository.save(projectMapper.toEntity(subProjectDto));
        return projectMapper.toDTO(project);
    }

    @Override
    public CreateSubProjectDto refreshSubProject(CreateSubProjectDto subProjectDto) throws ChildrenNotFinishedException {

        var subproject = repository.getProjectById(subProjectDto.getId());
        if (subproject.getStatus() == COMPLETED) {
            ifChildProjectNotCompleted(subproject, (listOfNotComletedProject) -> {
                throw new ChildrenNotFinishedException(listOfNotComletedProject);
            });
        }

        var project = repository.save(projectMapper.toEntity(subProjectDto));
        return null;
    }

    public static List<Project> getAllProjects(Project project) {
        return Stream.concat(
                Stream.of(project),  // Добавляем текущий проект
                Optional.ofNullable(project.getChildren())  // Handle the case where getChildren() might be null
                        .orElse(Collections.emptyList())    // If null, return an empty list
                        .stream()
                        .flatMap(child -> getAllProjects(child).stream())
        ).toList();
    }

    private void ifChildProjectNotCompleted(Project subproject, ThrowingConsumer<String> func) throws ChildrenNotFinishedException {
//        if (subproject.getStatus() != ProjectStatus.COMPLETED) {
            var notFinisedChildren = getAllProjects(subproject).stream()
                    .filter(child -> child.getStatus() != COMPLETED && child.getStatus() != CANCELLED).toList();

            if (!notFinisedChildren.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                notFinisedChildren.forEach(child -> {
                    builder.append(child.getId()).append(" status ").append(child.getStatus());
                });
                func.accept(builder.toString());
            }
//        }
    }

}
