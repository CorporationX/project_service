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
        
        FileResourceDto resourceDto = getFileResourceDtoByType(file, project, resourceType);
        PutObjectRequest putRequest = s3RequestService.createPutRequest(resourceDto);
        s3Service.uploadFile(putRequest);
        
        Resource saved = resourceRepository.save(createResource(project, teamMember, putRequest, resourceDto));
        project.addStorageSize(saved.getSize());
        project.addResource(saved);
        return resourceMapper.toDto(saved);
    }
    
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
    
    public void deleteProjectCover(Long projectId) {
        Project project = projectRepository.getProjectById(projectId);
        projectValidator.verifyNoCover(project);
        
        deleteProjectCover(project.getCoverImageId());
        
        project.setCoverImageId(null);
    }
    
    public void deleteProjectCover(String coverImageId) {
        DeleteObjectRequest deleteRequest = s3RequestService.createDeleteRequest(coverImageId);
        s3Service.deleteFile(deleteRequest);
    }
    
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
    
    private FileResourceDto getFileResourceDtoByType(MultipartFile file, Project project, ProjectResourceType resourceType) {
        return fileResourceConverters.stream()
            .filter(converter -> converter.getSupportedType().equals(resourceType))
            .findFirst()
            .map(converter -> converter.createFileResourceDto(file, project))
            .orElseThrow(
                () -> new IllegalArgumentException(String.format("Не найден конвертер для типа %s", resourceType))
            );
    }
}
