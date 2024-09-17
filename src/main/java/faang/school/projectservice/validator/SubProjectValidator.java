package faang.school.projectservice.validator;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.util.CannotCreatePrivateProjectForPublicParent;
import faang.school.projectservice.util.ParentProjectMusNotBeNull;
import faang.school.projectservice.util.RootProjectsParentMustNotBeNull;

public interface SubProjectValidator {
    public void validate(Project project) throws RootProjectsParentMustNotBeNull,
            CannotCreatePrivateProjectForPublicParent, ParentProjectMusNotBeNull;
}
