package faang.school.projectservice.validator;

import faang.school.projectservice.model.ProjectVisibility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProjectValidator {
    public void validateSubProjectVisibility(ProjectVisibility parentProjectVisibility,
                                             ProjectVisibility childProjectVisibility) {
        if (parentProjectVisibility == ProjectVisibility.PUBLIC &&
                childProjectVisibility == ProjectVisibility.PRIVATE) {
            throw new IllegalStateException("Your cannot create Private subproject from Public project");
        }
    }
}
