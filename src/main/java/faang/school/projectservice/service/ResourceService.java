package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.ErrorMessage;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ResourceMapper resourceMapper;

    @Transactional(readOnly = true)
    public List<ResourceDto> getAvailableResources(Long projectId, Long userId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(projectId, userId);
        if(isNull(teamMember)) {
            throw new EntityNotFoundException("Couldn't find a team member with id " + userId);
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find a project with id " + projectId));

        return getAllAvailable(project, teamMember);
    }

    public InputStream downloadResource(Long resourceId, Long userId) {
        Resource resource = getResourceWithCheckedPermissions(resourceId, userId);
        return s3Service.downloadFile(resource.getKey());
    }

    public ResourceDto addResource(Long projectId, Long userId, MultipartFile file) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
        if(isNull(teamMember)) {
            throw new EntityNotFoundException("Couldn't find a team member with id " + userId);
        }
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find a project with id " + projectId));

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        ResourceDto resourceDto = s3Service.uploadFile(file, folder);

        Resource resource = resourceMapper.toEntity(resourceDto);
        resource.setAllowedRoles(teamMember.getRoles());
        resource.setProject(project);
        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return resourceMapper.toDto(resource);
    }

    public ResourceDto updateResource(Long resourceId, Long userId, MultipartFile file) {
        Resource resourceFromDB = getResourceWithCheckedPermissions(resourceId, userId);
        Project project = resourceFromDB.getProject();

        BigInteger newStorageSize = project.getStorageSize()
                .add(BigInteger.valueOf(file.getSize()))
                .subtract(resourceFromDB.getSize());
        checkStorageSizeExceeded(newStorageSize, project.getMaxStorageSize());

        String folder = project.getId() + project.getName();
        s3Service.deleteFile(resourceFromDB.getKey());
        ResourceDto resourceDto = s3Service.uploadFile(file, folder);
        resourceFromDB.setKey(resourceDto.getKey());
        resourceFromDB.setSize(resourceDto.getSize());
        resourceFromDB.setUpdatedAt(resourceDto.getUpdatedAt());
        resourceFromDB.setName(resourceDto.getName());
        resourceFromDB.setType(resourceDto.getType());
        resourceRepository.save(resourceFromDB);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);

        return resourceMapper.toDto(resourceFromDB);
    }

    public void deleteResource(Long resourceId, Long userId) {
        Resource resource = getResourceWithCheckedPermissions(resourceId, userId);
        s3Service.deleteFile(resource.getKey());
        resourceRepository.deleteById(resource.getId());
    }

    private List<ResourceDto> getAllAvailable(Project project, TeamMember teamMember) {
        List<Resource> resources = project.getResources();
        return resources.stream()
                .filter(resource -> filterWithRoles(resource.getAllowedRoles(), teamMember.getRoles()))
                .map(resourceMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean filterWithRoles(List<TeamRole> allowedRoles, List<TeamRole> roles) {
        return roles.stream().anyMatch(allowedRoles::contains);
    }

    private void checkStorageSizeExceeded(BigInteger newStorageSize, BigInteger maxStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            throw new FileException(ErrorMessage.FILE_SIZE_EXCEEDED);
        }
    }

    private Resource getResourceWithCheckedPermissions(Long resourceId, Long userId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow
                (() -> new EntityNotFoundException("Couldn't find a resource with id " + resourceId));

        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userId, resource.getProject().getId());
        if(isNull(teamMember)) {
            throw new EntityNotFoundException("Couldn't find a team member with id " + userId);
        }

        if(!filterWithRoles(resource.getAllowedRoles(), teamMember.getRoles())) {
            throw new EntityNotFoundException("Couldn't find a resource with id " + resourceId);
        }

        return resource;
    }
}
