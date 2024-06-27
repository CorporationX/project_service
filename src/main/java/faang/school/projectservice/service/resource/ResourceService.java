package faang.school.projectservice.service.resource;

import java.math.BigInteger;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import faang.school.projectservice.dto.resource.FileResourceDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.service.s3.requests.S3RequestService;
import faang.school.projectservice.validator.project.ProjectValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * Сервис обработки ресурсов проекта
 */
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    @Qualifier("s3RequestService")
    private final S3RequestService s3RequestService;
    private final ProjectValidator projectValidator;
    private final TeamMemberRepository teamMemberRepository;
    private final List<MultipartFileResourceConverter> fileResourceConverters;
    
    /**
     * Общий метод загрузки ресурса в s3
     *
     * @param projectId ID проекта, к которому прикрепляется ресурс
     * @param teamMemberId ID члена команды, который прикрепляет ресурс
     * @param file MultipartFile, который хотят загрузить
     * @param resourceType тип ресурса для проекта. Вся логика накрутки находится в методах, вызывающих этот метод
     *
     * @return DTO сохраненного ресурса
     */
    @Transactional
    public ResourceDto uploadResource(
        Long projectId,
        Long teamMemberId,
        MultipartFile file,
        ProjectResourceType resourceType
    ) {
        Project project = projectRepository.getProjectById(projectId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        
        projectValidator.verifyStorageSizeNotExceeding(project, file.getSize());
        
        FileResourceDto resourceDto = convertMultipartFileToFileResourceDto(file, project, resourceType);
        PutObjectRequest putRequest = s3RequestService.createPutRequest(resourceDto);
        s3Service.uploadFile(putRequest);
        
        Resource saved = resourceRepository.save(createResource(project, teamMember, putRequest, resourceDto));
        project.addStorageSize(saved.getSize());
        project.addResource(saved);
        return resourceMapper.toDto(saved);
    }
    
    /**
     * Метод сохранения обложки проекта
     */
    @SneakyThrows
    @Transactional
    public ResourceDto uploadProjectCover(Long projectId, Long teamMemberId, MultipartFile file) {
        Project project = projectRepository.getProjectById(projectId);
        ResourceDto resourceDto = uploadResource(projectId, teamMemberId, file, ProjectResourceType.PROJECT_COVER);
        
        project.addStorageSize(resourceDto.getSize());
        project.setCoverImageId(resourceDto.getKey());
        projectRepository.save(project);
        
        return resourceDto;
    }
    
    /**
     * Метод удаления обложки проекта
     */
    public void deleteProjectCover(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        projectValidator.verifyNoCover(project);
        
        DeleteObjectRequest deleteRequest = s3RequestService.createDeleteRequest(project.getCoverImageId());
        s3Service.deleteFile(deleteRequest);
        
        project.setCoverImageId(null);
    }
    
    /**
     * Создание ресурса для вставки в БД
     */
    private Resource createResource(
        Project project,
        TeamMember teamMember,
        PutObjectRequest putObjectRequest,
        FileResourceDto resourceDto
    ) {
        Resource resource = new Resource();
        resource.setKey(putObjectRequest.getKey());
        resource.setSize(BigInteger.valueOf(resourceDto.getSize()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setName(resourceDto.getFileName());
        resource.setType(ResourceType.getResourceType(resourceDto.getContentType()));
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        return resource;
    }
    
    /**
     * Конвертация MultipartFile в DTO, для последующей обработки в S3
     */
    private FileResourceDto convertMultipartFileToFileResourceDto(
        MultipartFile file,
        Project project,
        ProjectResourceType resourceType
    ) {
        return fileResourceConverters.stream()
            .filter(converter -> converter.getProjectResourceSupportedType().equals(resourceType))
            .findFirst()
            .map(converter -> converter.convertToFileResourceDto(file, project))
            .orElseThrow(
                () -> new IllegalArgumentException(String.format("Не найден конвертер для типа %s", resourceType))
            );
    }
}
