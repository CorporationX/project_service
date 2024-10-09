package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.model.entity.ProjectVisibility;

public interface SubProjectValidator {
    public void validateSubProjectVisibility(ProjectVisibility parentProjectVisibility,
                                             ProjectVisibility childProjectVisibility);

}
