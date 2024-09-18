package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.exception.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.exception.ParentProjectMusNotBeNull;
import faang.school.projectservice.exception.RootProjectsParentMustNotBeNull;
import faang.school.projectservice.model.ProjectVisibility;

public interface SubProjectValidator {
    public void validateSubProjectVisibility(ProjectVisibility parentProjectVisibility,
                                             ProjectVisibility childProjectVisibility);

}
