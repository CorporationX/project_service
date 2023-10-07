package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ResourceDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.util.FileStore;
import faang.school.projectservice.validator.ResourcesValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "services.s3", havingValue = "true")
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourcesValidator resourcesValidator;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final ResourceMapper resourceMapper;
    private final FileStore fileStore;

    @Transactional
    public ResourceDto uploadFile(ResourceDto resourceDto, MultipartFile file, long userId) {
        TeamMember teamMember = teamMemberService.findByUserIdAndProjectId(userId, resourceDto.getProjectId());
        Project project = projectService.getProjectByIdFromRepo(resourceDto.getProjectId());

        resourcesValidator.checkTeamMemberInProject(teamMember);

        String key = resourceDto.getProjectId() + "_" + project.getName() + "/" + file.getOriginalFilename();
        fileStore.uploadFile(file, key);

        Resource resource = fillResourceCreate(resourceDto, file, key, teamMember);
        updateProjectStorageCapacity(file, project);

        Resource entity = resourceRepository.save(resource);
        resource.setUpdatedAt(null);
        return resourceMapper.toDto(entity);
    }

    @Transactional
    public ResourceDto updateFile(long id, ResourceDto resourceDto, MultipartFile file, long userId) {
        Resource resource = getResourceById(id);
        Project project = projectService.getProjectByIdFromRepo(resourceDto.getProjectId());
        TeamMember teamMember = teamMemberService.findByUserIdAndProjectId(userId, resourceDto.getProjectId());

        resourcesValidator.checkTeamMemberInProject(teamMember);

        String key = resourceDto.getProjectId() + "_" + project.getName() + "/" + file.getOriginalFilename();
        fileStore.deleteFile(resource.getKey());
        fileStore.uploadFile(file, key);

        updateProjectStorageCapacity(file, project);
        fillResourceUpdate(resourceDto, resource, file, key, teamMember);
        resource.setUpdatedAt(null);
        return resourceMapper.toDto(resourceRepository.save(resource));
    }

    @Transactional
    public void deleteResource(long id, long userId) {
        Resource resource = getResourceById(id);
        Project project = projectService.getProjectByIdFromRepo(id);
        resourcesValidator.checkRightsToDelete(resource, project, userId);

        fileStore.deleteFile(resource.getKey());
        subtractCapacity(project, resource);

        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedAt(null);
        resourceRepository.save(resource);
    }

    private Resource getResourceById(long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
    }

    private Resource fillResourceCreate(ResourceDto resourceDto, MultipartFile file, String key,
                                        TeamMember teamMember) {
        Resource resource = resourceMapper.toEntity(resourceDto);
        resource.setCreatedBy(teamMember);

        fillResource(resource, file, key, teamMember);
        return resource;
    }

    private void fillResourceUpdate(ResourceDto resourceDto, Resource resource, MultipartFile file, String key,
                                    TeamMember teamMember) {
        resourceMapper.update(resourceDto, resource);
        resource.setUpdatedAt(null);

        fillResource(resource, file, key, teamMember);
    }

    private void fillResource(Resource resource, MultipartFile file, String key, TeamMember teamMember) {
        List<TeamRole> roles = new ArrayList<>(teamMember.getRoles());

        resource.setUpdatedBy(teamMember);
        resource.setAllowedRoles(roles);
        resource.setName(file.getOriginalFilename());
        resource.setKey(key);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setSize(BigInteger.valueOf(file.getSize()));
    }

    private void updateProjectStorageCapacity(MultipartFile file, Project project) {
        long newStorageCapacity = project.getStorageSize().intValue() + file.getSize();
        resourcesValidator.checkStorageCapacity(newStorageCapacity);
        project.setStorageSize(project.getStorageSize().add(BigInteger.valueOf(file.getSize())));
        projectService.saveProject(project);
    }

    private void subtractCapacity(Project project, Resource resource) {
        BigInteger newSizeCapacity = project.getStorageSize().subtract(resource.getSize());
        if (newSizeCapacity.compareTo(BigInteger.ZERO) < 0) {
            newSizeCapacity = BigInteger.ZERO;
        }
        project.setStorageSize(newSizeCapacity);
        projectService.saveProject(project);
    }
}