package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final ProjectMapper projectMapper;

    public Long createMoment(ProjectDto projectDto) {
        Project project = projectMapper.toProject(projectDto);
        Moment moment = momentMapper.toEntity(project);

        addCreatingInfo(project, moment);

        return momentRepository.save(moment).getId();
    }

    private void addCreatingInfo(Project project, Moment moment) {
        List<Project> projects = moment.getProjects();

        if (projects == null) {
            projects = new ArrayList<>();
        }
        projects.add(project);

        moment.setProjects(projects);
        moment.setCreatedAt(LocalDateTime.now());
        addUserIds(project, moment);
    }

    private void addUserIds(Project project, Moment moment) {
        List<Long> userIds = new ArrayList<>();
        project.getTeams().stream()
                .forEach(teammate -> userIds.add(teammate.getId()));
        moment.setUserIds(userIds);
    }
}
