package faang.school.projectservice.service.project.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public interface SubProjectUpdater {
    boolean isApplicable(SubProjectUpdateDto updateDto);

    ProjectDto apply(ProjectDto projectDto, SubProjectUpdateDto updateDto);
}
