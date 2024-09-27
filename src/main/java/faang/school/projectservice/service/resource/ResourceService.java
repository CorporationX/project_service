package faang.school.projectservice.service.resource;

import com.amazonaws.services.s3.model.S3Object;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.StorageLimitException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.resource.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.subproject.ValidatorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final ValidatorService validatorService;
    private final UserContext userContext;
    private final TeamMemberRepository teamMemberRepository;

    public S3Object getResource(Long resourceId) {
        Resource resourceFromDb = resourceRepository.findById(resourceId).orElseThrow(EntityNotFoundException::new);
        return s3Service.getFile(resourceFromDb.getKey());
    }

    @Transactional
    public ResourceDto updateResource(Long resourceId, MultipartFile file) {
        Resource resourceFromDb = resourceRepository.findById(resourceId).orElseThrow(EntityNotFoundException::new);
        TeamMember teamMember = teamMemberRepository.findById(userContext.getUserId());
        Project project = projectRepository.findById(resourceFromDb.getProject().getId());

        Resource newResourceFromS3 = s3Service.uploadFile(file, resourceFromDb.getProject().getName());
        BigInteger newStorageSize = project.getStorageSize()
                .subtract(resourceFromDb.getSize())
                .add(newResourceFromS3.getSize());
        project.setStorageSize(newStorageSize);
        s3Service.deleteFile(resourceFromDb.getKey());
        resourceFromDb.setKey(newResourceFromS3.getKey());
        resourceFromDb.setSize(newResourceFromS3.getSize());
        resourceFromDb.setUpdatedBy(teamMember);
        return resourceMapper.mapToResourceDto(resourceFromDb);
    }


    @Transactional
    public void deleteResource(Long resourceId) {
        Resource resource = resourceRepository.findById(resourceId).orElseThrow(EntityNotFoundException::new);
        TeamMember teamMember = teamMemberRepository.findById(userContext.getUserId());
        checkOwnerOrManager(resource, teamMember);
        Project project = resource.getProject();
        BigInteger newStorageSize = project.getStorageSize();
        newStorageSize = newStorageSize.subtract(resource.getSize());
        project.setStorageSize(newStorageSize);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setSize(BigInteger.ZERO);
        resource.setUpdatedBy(teamMember);

        s3Service.deleteFile(resource.getKey());
        resource.setKey(null);

    }

    @Transactional
    public ResourceDto addResource(Long projectId, MultipartFile file) {
        validatorService.isProjectExists(projectId);
        TeamMember teamMember = teamMemberRepository.findById(userContext.getUserId());
        Project project = projectRepository.findById(projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSize(newStorageSize, project.getMaxStorageSize());
        Resource resource = s3Service.uploadFile(file, project.getName());
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        List<TeamRole> teamMemberRoles = new ArrayList<>(teamMember.getRoles());
        resource.setAllowedRoles(teamMemberRoles);
        resource = resourceRepository.save(resource);
        project.setStorageSize(newStorageSize);
        return resourceMapper.mapToResourceDto(resource);
    }

    private void checkOwnerOrManager(Resource resource, TeamMember teamMember) {
        if (!teamMember.getRoles().contains(TeamRole.MANAGER) && resource.getCreatedBy() != teamMember) {
            throw new PermissionDeniedDataAccessException("You can't delete this file", null);
        }
    }

    private void checkStorageSize(BigInteger storageSize, BigInteger maxStorageSize) {
        if (storageSize.compareTo(maxStorageSize) > 0) {
            throw new StorageLimitException("Storage limit exceeded");
        }
    }
}
