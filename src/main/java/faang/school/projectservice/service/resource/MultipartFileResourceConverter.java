package faang.school.projectservice.service.resource;

import org.springframework.web.multipart.MultipartFile;

import faang.school.projectservice.dto.resource.FileResourceDto;
import faang.school.projectservice.model.Project;

public abstract class MultipartFileResourceConverter {
    private static final String DEFAULT_FOLDER_RESOURCE_DELIMITER = "_";
    
    public abstract FileResourceDto createFileResourceDto(MultipartFile file, Project project);
    
    public abstract ProjectResourceType getSupportedType();
    
    protected String getDefaultProjectFolderName(Project project) {
        return String.format("%s%s%s", project.getId(), DEFAULT_FOLDER_RESOURCE_DELIMITER, project.getName());
    }
}
