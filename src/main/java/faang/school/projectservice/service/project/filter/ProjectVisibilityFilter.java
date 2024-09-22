package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.model.Project;

public interface ProjectVisibilityFilter {
    boolean isVisible(Project project, Long userId);
}
