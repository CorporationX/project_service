package faang.school.projectservice.service.resource;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import faang.school.projectservice.dto.resource.FileResourceDto;
import faang.school.projectservice.model.Project;
import lombok.SneakyThrows;

@Component
public class ProjectFileResource extends MultipartFileResourceConverter {
    @SneakyThrows
    @Override
    public FileResourceDto createFileResourceDto(MultipartFile file, Project project) {
        return FileResourceDto.builder()
            .size(file.getSize())
            .contentType(file.getContentType())
            .fileName(file.getOriginalFilename())
            .folderName(getDefaultProjectFolderName(project))
            .resourceInputStream(file.getInputStream())
            .build();
    }
    
    @Override
    public ProjectResourceType getSupportedType() {
        return ProjectResourceType.PROJECT_RESOURCE;
    }
}
