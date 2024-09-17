package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.exception.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.exception.ParentProjectMusNotBeNull;
import faang.school.projectservice.exception.RootProjectsParentMustNotBeNull;

public interface SubProjectValidator {
    public void validate(Project project) throws RootProjectsParentMustNotBeNull,
            CannotCreatePrivateProjectForPublicParent, ParentProjectMusNotBeNull;
}
