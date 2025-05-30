package faang.school.projectservice.service.resourse;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.ResourceService;
import faang.school.projectservice.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    protected final S3Service s3Service;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserContext userContext;
    private final ResourceMapper resourceMapper;

    @Transactional
    @Override
    public ResourceDto uploadEntityFile(MultipartFile file, long id) {
        Project project = projectRepository.findById(id).orElseThrow();
        // ToDo: Предполагается, что файл уникален для имени и проекта. Этого достаточно в данный момент?
        Optional<Resource> resource = resourceRepository.findByNameAndProjectId(file.getName(), id);
        if (resource.isEmpty()) {
            return createResource(file, project);
        }

        return updateResource(file, project, resource.get());
    }

    @Override
    public URL getFileUrl(String fileKey) {
        return s3Service.getFileUrl(fileKey);
    }

    @Transactional
    @Override
    public void deleteFile(String fileKey) {
        Resource resource = resourceRepository.findByKey(fileKey).orElseThrow();
        validateDeletionAccess(resource);

        s3Service.deleteFile(fileKey);

        deleteResource(resource);
        decreaseProjectSize(resource.getProject(), resource.getSize());
    }

    public ResourceDto createResource(MultipartFile file, Project project) {
        validateFreeSpace(project.getStorageSize(), project.getMaxStorageSize(), file.getSize());
        TeamMember teamMember = findTeamMember(project.getId());

        Resource uploadedResource = s3Service.uploadFile(file, project.getName());

        createResourceByTeamMember(uploadedResource, teamMember, project);
        increaseProjectSize(project, uploadedResource.getSize());

        return resourceMapper.toDto(uploadedResource);
    }

    private ResourceDto updateResource(MultipartFile file, Project project, Resource existingResource) {
        validateFreeSpace(project.getStorageSize(), project.getMaxStorageSize(), file.getSize());
        TeamMember teamMember = findTeamMember(project.getId());

        return null;
    }

    private void validateFreeSpace(BigInteger currentSize, BigInteger maxSize, long fileSize) {
        int freeSpace = maxSize.getLowestSetBit() - currentSize.getLowestSetBit();
        if (freeSpace > fileSize) {
            return;
        }

        throw new DataValidationException(String.format("Can't upload file. Storage size limit reached. File size: %d. Free space: %d",
                fileSize, freeSpace));
    }

    private void validateDeletionAccess(Resource resource) {
        if (
                resource.getProject().getOwnerId() == userContext.getUserId()
                || resource.getCreatedBy().getUserId() == userContext.getUserId()
        ) {
            return;
        }

        throw new AccessDeniedException("Only owner and project manager can delete files.");
    }

    private TeamMember findTeamMember(long projectId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(userContext.getUserId(), projectId);
        if (teamMember == null) {
            throw new AccessDeniedException("Only project members can upload files");
        }

        return teamMember;
    }

    private void increaseProjectSize(Project project, BigInteger fileSize) {
        project.setStorageSize(project.getStorageSize().add(fileSize));
        projectRepository.save(project);
    }

    private void decreaseProjectSize(Project project, BigInteger fileSize) {
        project.setStorageSize(project.getStorageSize().subtract(fileSize));
        projectRepository.save(project);
    }

    private void deleteResource(Resource resource) {
        resource.setKey(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setSize(BigInteger.ZERO);

        resourceRepository.save(resource);
    }

    private void createResourceByTeamMember(Resource resource, TeamMember teamMember, Project project) {
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        resource.setAllowedRoles(teamMember.getRoles());
        resource.setProject(project);

        resourceRepository.save(resource);
    }
}
