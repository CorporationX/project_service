package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ResourceDto;
import faang.school.projectservice.exception.ResourceException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceServiceImpl implements ResourceService {
    private static final Logger log = LoggerFactory.getLogger(ResourceServiceImpl.class);
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final ResourceMapper resourceMapper;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    @Transactional
    public ResourceDto addResource(MultipartFile file, Long projectId, Long currentMemberId) {
        Project project = projectRepository.getProjectById(projectId);
        BigInteger newStorageSize = BigInteger.valueOf(file.getSize()).add(project.getStorageSize());
        checkSize(newStorageSize, project.getMaxStorageSize());

        String folder = project.getName() + project.getId();

        Resource resource = s3Service.upload(file, folder);
        TeamMember teamMember = teamMemberRepository.findById(currentMemberId);
        resource.setUpdatedBy(teamMember);
        resource.setAllowedRoles(teamMember.getRoles());
        resource.setProject(project);
        resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        projectRepository.save(project);
        return resourceMapper.toDTO(resource);
    }

    @Override
    @Transactional
    public void deleteResource(Long id, Long currentMemberId) {
        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Resource not found"));
        TeamMember teamMember = teamMemberRepository.findById(currentMemberId);
        String key = resource.getKey();
        validateOnDelete(resource, teamMember);
        resource.setKey(null);
        updateProjectStorageSize(resource.getProject(), resource.getSize());
        resource.setSize(BigInteger.valueOf(0L));
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(teamMember);

        resourceRepository.save(resource);
        s3Service.delete(key);

        log.info("resource with id - {}, deleted", resource.getId());

    }

    private void checkSize(BigInteger newProjectSize, BigInteger maxProjectSize) {
        if (newProjectSize.compareTo(maxProjectSize) > 0) {
            throw new InvalidParameterException(String.format("Project size must be greater than or equal to max project(%d)", maxProjectSize.intValue()));
        }

    }

    private void updateProjectStorageSize(Project project, BigInteger sizeChange) {
        project.setStorageSize(project.getStorageSize().subtract(sizeChange));
        projectRepository.save(project);
    }

    private void validateOnDelete(Resource resource, TeamMember teamMember) {
        Long resourceCreatorId = resource.getCreatedBy().getId();
        Long projectOwnerId = resource.getProject().getOwnerId();
        Long teamMemberId = teamMember.getId();

        if (resourceCreatorId.equals(teamMemberId) || projectOwnerId.equals(teamMemberId)) {
            return;
        }

        log.error("Resource not allowed to be deleted. Resource - {}, TeamMember - {}", resource, teamMember);
        throw new ResourceException("You are not allowed to delete this resource");
    }
}
