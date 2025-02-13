package faang.school.projectservice.service.pdf;


import faang.school.projectservice.dto.project.ProjectPresentationDto;

import java.io.InputStream;


public interface ProjectPdfService {

    InputStream createProjectPresentation(ProjectPresentationDto dto);
}
