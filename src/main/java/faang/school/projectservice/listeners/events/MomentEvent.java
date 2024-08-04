package faang.school.projectservice.listeners.events;

import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class MomentEvent extends ApplicationEvent {

    private Project project;
    private List<Project> childProjects = new ArrayList<>();
    private String message;
    private Long updatedBy;
    private Long createdBy;

    public MomentEvent(Project project, Long id, List<Project> childProjects,String message) {
        super(message);
        this.project = project;
        this.message = message;
        this.updatedBy = id;
        this.createdBy = id;
        this.childProjects = childProjects;
    }
}
