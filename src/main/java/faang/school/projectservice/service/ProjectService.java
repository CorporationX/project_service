package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project_filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilter;

    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("Project " + projectDto.getName() + " already exists");
        }

        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setCreatedAt(LocalDateTime.now());
        return projectMapper.toDto(projectRepository.save(projectMapper.toEntity(projectDto)));
    }

    //Обновить проект. Должна быть возможность изменять статус проекта, описание проекта.
    // При этом для аудита необходимо проставлять TIMESTAMP на каждое последние изменение проекта.
    public ProjectDto updateProject(ProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getId());
        projectDto.setUpdatedAt(LocalDateTime.now());
        projectMapper.update(projectDto, project);
        return projectMapper.toDto(projectRepository.save(project));
    }
    //Получить все проекты с фильтрами по названию или статусу.
    // У проекта также должен быть признак приватности.
    // Если проект приватный, то по поиску он должен быть видим только своим участникам.

    public List<ProjectDto> getAllProjectsByStatus(ProjectDto projectDto) {
        Stream<Project> projectStream = projectRepository.findAll().stream();
        List<ProjectFilter> projectFilterList = projectFilter.stream()
                .filter(filter -> filter.isApplicable(projectDto))
                .toList();
        for (ProjectFilter project : projectFilterList) {
            projectStream = project.apply(projectStream, projectDto);
        }
        return projectStream.map(projectMapper::toDto).toList();
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectDto getProjectById(Long id) {
        return projectMapper.toDto(projectRepository.getProjectById(id));
    }


    //Обновить стажировку. Если стажировка завершена, то стажирующиеся должны получить новые роли на проекте,
    // если прошли, и быть удалены из списка участников проекта, если не прошли.
    // Участник считается прошедшим стажировку, если все запланированные задачи выполнены.
    // После старта стажировки нельзя добавлять новых стажёров.
    // Стажировку можно пройти досрочно или досрочно быть уволенным.
//    public InternshipDto internshipUpdate(InternshipDto internshipDto) {
//        Internship internship = internshipRepository.findById(internshipDto.getId())
//                .orElseThrow(() -> new IllegalArgumentException("Invalid internship"));
//        Project project = internship.getProject();
//        if (internship.getStatus().equals(IN_PROGRESS)){
//            if (internship.getInterns().getStages().getTasks().) {
//                internship.setStatus(ENDED);
//            }
//            for (TeamMember intern : internship.getInterns()){
//                project.getTeam().getTeamMembers().remove(intern);
//            }
//            internship.setUpdatedAt(LocalDateTime.now());
//        }
//            List<TeamMember> interns = internship.getInterns();
//            interns.forEach(intern -> {
//                List <TaskStatus> tasksOfIntern = taskRepository.findAllByProjectIdAndPerformerId(internship.getProject().getId(), intern.getId())
//                        .stream()
//                        .map(task -> task.getStatus())
//                        .toList();
//                if (tasksOfIntern.stream().allMatch(task -> task.equals(TaskStatus.DONE))) {
//                    //переписать будто стажер получает статус как у ментора
//                    intern.addRole(TeamRole.JUNIOR);
//                    intern.addRole(TeamRole.INTERN);
//
//                } else {
//                    project.getTeam().getTeamMembers().remove(intern);
//                }
//            });

}
