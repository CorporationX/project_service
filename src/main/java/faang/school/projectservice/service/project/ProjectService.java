package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDtoResponse;
import faang.school.projectservice.dto.resource.S3ObjectDto;


public interface ProjectService {

    ProjectDtoResponse creatingPresentation(long projectId);

    String getPresentationFileKey(long projectId);

    S3ObjectDto downloadPdf(Long projectId);
}
