package faang.school.projectservice.service.presentation;

import faang.school.projectservice.dto.resource.S3FileResponse;

public interface ProjectPresentationService {
    void create(Long projectId);
    S3FileResponse downloadPresentation(Long projectId);
}
