package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.util.FileStore;
import faang.school.projectservice.validator.ResourcesValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {
    private final TeamMemberRepository teamMemberRepository;
    private final ResourceRepository resourceRepository;
    private final ResourcesValidator resourcesValidator;
    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;
    private final UserContext userContext;
    private final FileStore fileStore;

    public ResourceDto uploadFile(ResourceDto resourceDto, MultipartFile file) {
        Project project = projectRepository.getProjectById(resourceDto.getProjectId());
        String key = resourceDto.getProjectId() + "_" + project.getName() + "/" + file.getOriginalFilename();

        Resource resource = fillResourceCreate(resourceDto, file, key);
        updateProjectStorageCapacity(file, project);

        fileStore.uploadFile(file, key);

        Resource entity = resourceRepository.save(resource);
        return resourceMapper.toDto(entity);
    }

    @Transactional
    public ResourceDto updateFile(long id, ResourceDto resourceDto, MultipartFile file) {
        Resource resource = getResourceById(id);

        Project project = projectRepository.getProjectById(resourceDto.getProjectId());
        updateProjectStorageCapacity(file, project);

        String key = resourceDto.getProjectId() + "_" + project.getName() + "/" + file.getOriginalFilename();

        fileStore.deleteFile(resource.getKey());

        fillResourceUpdate(resourceDto, resource, file, key);

        fileStore.uploadFile(file, key);

        Resource save = resourceRepository.save(resource);

        return resourceMapper.toDto(save);
    }

    public void deleteResource(long id) {
        Resource resource = getResourceById(id);
        Project project = projectRepository.getProjectById(resource.getProject().getId());

        resourcesValidator.checkRightsToDelete(resource, project, userContext.getUserId());

        fileStore.deleteFile(resource.getKey());

        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
        projectRepository.save(project);

        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resourceRepository.save(resource);
    }

    private Resource getResourceById(long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
    }

    private Resource fillResourceCreate(ResourceDto resourceDto, MultipartFile file, String key) { //при create
        TeamMember teamMember = teamMemberRepository.findById(userContext.getUserId());

        Resource resource = resourceMapper.toEntity(resourceDto);

        List<TeamRole> roles = new ArrayList<>(teamMember.getRoles());
        resource.setAllowedRoles(roles);
        TeamMember createdBy = TeamMember.builder().id(userContext.getUserId()).build();
        resource.setCreatedBy(createdBy);
        resource.setUpdatedBy(createdBy);

        fillResource(resource, file, key);

        return resource;
    }

    private void fillResourceUpdate(ResourceDto resourceDto, Resource resource, MultipartFile file, String key) { //при update
        TeamMember teamMember = teamMemberRepository.findById(userContext.getUserId());

        resourceMapper.update(resourceDto, resource);

        List<TeamRole> roles = Stream.concat(teamMember.getRoles().stream(), resource.getAllowedRoles().stream())
                .distinct()
                .toList();

        resource.setAllowedRoles(roles);
        resource.setUpdatedBy(TeamMember.builder().id(userContext.getUserId()).build());
        resource.setUpdatedAt(null);

        fillResource(resource, file, key);
    }

    private Resource fillResource(Resource resource, MultipartFile file, String key) {
        resource.setName(file.getOriginalFilename());
        resource.setKey(key);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setSize(BigInteger.valueOf(file.getSize()));

        return resource;
    }

    private void updateProjectStorageCapacity(MultipartFile file, Project project) {
        long newStorageCapacity = project.getStorageSize().intValue() + file.getSize();

        resourcesValidator.checkStorageCapacity(newStorageCapacity);

        project.setStorageSize(project.getStorageSize().add(BigInteger.valueOf(file.getSize())));
        projectRepository.save(project);
    }
}
