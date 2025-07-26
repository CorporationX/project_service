package faang.school.projectservice.service.presentation.generator;

import faang.school.projectservice.dto.presentation.ProjectPresentationDto;

import java.io.File;

public interface PdfGenerator {
    File generateToFile(ProjectPresentationDto dto);
}
