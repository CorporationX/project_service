package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.resource.S3ObjectDto;
import org.springframework.core.io.InputStreamResource;


public interface ProjectService {

    void createPresentation(long projectId);

    S3ObjectDto downloadPdf(Long projectId);

    InputStreamResource getPresentation(S3ObjectDto obj);
}
