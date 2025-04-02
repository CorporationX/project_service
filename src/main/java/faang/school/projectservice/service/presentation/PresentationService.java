package faang.school.projectservice.service.presentation;


import faang.school.projectservice.dto.project.ProjectPresentationDto;

import java.io.InputStream;


public interface PresentationService {
    InputStream createProjectPresentation(ProjectPresentationDto dto);
}
