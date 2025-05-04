package faang.school.projectservice.service.projectresource;

import faang.school.projectservice.dto.resource.ResourceFileDto;
import faang.school.projectservice.exception.ResourceHandlingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;

public interface ProjectResourceService {

    ResourceFileDto uploadFile(Long projectId, MultipartFile file) throws IOException;

    InputStream downloadFile(Long resourceId) throws ResourceHandlingException;

    void deleteFile(Long resourceId) throws AccessDeniedException, ResourceHandlingException;

    ResourceFileDto getResourceInfo(Long resourceId) throws ResourceHandlingException;
}
