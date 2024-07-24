package faang.school.projectservice.service.project.subproject;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectUpdateDto;

public class SubProjectStatusUpdater implements SubProjectUpdater{
    @Override
    public boolean isApplicable(SubProjectUpdateDto updateDto) {
        return updateDto.getStatus() != null;
    }

    @Override
    public ProjectDto apply(ProjectDto projectDto, SubProjectUpdateDto updateDto) {
        projectDto.setStatus(updateDto.getStatus());
        return projectDto;
    }
}
