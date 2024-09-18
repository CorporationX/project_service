package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.exception.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.exception.ParentProjectMusNotBeNull;
import faang.school.projectservice.exception.RootProjectsParentMustNotBeNull;
import org.springframework.stereotype.Component;

@Component
public class SubProjectValidatorImpl implements SubProjectValidator{
    public void validateSubProjectVisibility(ProjectVisibility parentProjectVisibility,
                                             ProjectVisibility childProjectVisibility) {
        if (parentProjectVisibility == ProjectVisibility.PUBLIC &&
                childProjectVisibility == ProjectVisibility.PRIVATE) {
            throw new IllegalStateException("Your cannot create Private subproject from Public project");
        }
    }

}
