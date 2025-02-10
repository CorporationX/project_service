package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectInfoDto;


public interface ProjectService {

    ProjectInfoDto creatingPresentation(long projectId);

    String getPresentationFileKey(long projectId);
}
