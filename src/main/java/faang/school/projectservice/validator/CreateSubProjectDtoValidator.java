package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;

public interface CreateSubProjectDtoValidator {
    void validateOnCreate(CreateSubProjectDto projectDto);
    void validateOnUpdate(UpdateSubProjectDto projectDto);

}
