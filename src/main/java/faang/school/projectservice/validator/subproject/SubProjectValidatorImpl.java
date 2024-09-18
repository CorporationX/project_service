package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.model.ProjectVisibility;
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
