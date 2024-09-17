package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.exception.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.exception.ParentProjectMusNotBeNull;
import faang.school.projectservice.exception.RootProjectsParentMustNotBeNull;
import org.springframework.stereotype.Component;

@Component
public class SubProjectValidatorImpl implements SubProjectValidator{
    @Override
    public void validate(Project project){
        if (project.getParentProject() == null) {
            throw new ParentProjectMusNotBeNull();
        }
        if (project.getParentProject().getVisibility() == ProjectVisibility.PUBLIC
                && project.getVisibility() == ProjectVisibility.PRIVATE) {
            throw new CannotCreatePrivateProjectForPublicParent();
        }
    }
}
