package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.client.resource.MapperResource;
import faang.school.projectservice.dto.client.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.FileServiceValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final AmazonS3Service s3Service;
    private final MapperResource mapperResource;
    private final TeamMemberRepository teamMemberRepository;
    private final FileServiceValidator fileServiceValidator;
    private final UserServiceClient userServiceClient;

    @Transactional
    public ResourceDto createFile(MultipartFile file, long projectId, long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        TeamMember teamMember = teamMemberRepository.findById(userId);
        Project project = projectRepository.getProjectById(projectId);

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        fileServiceValidator.checkMemoryAvailability(newStorageSize, userDto);

        String nameFolder = project.getName() + projectId;
        Resource resource = s3Service.createFile(file, nameFolder);
        addConnectionsResource(resource, project, teamMember);

        project.setStorageSize(newStorageSize);
        resourceRepository.save(resource);
        projectRepository.save(project);

        return mapperResource.toResourceDto(resource);
    }

    @Transactional
    public void deleteFile(long resourceId, long userId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        Project project = projectRepository.getProjectById(resource.getProject().getId());
        fileServiceValidator.checkAccessRights(project.getOwnerId(), resource.getCreatedBy().getId(), userId);

        BigInteger newSize = project.getStorageSize().subtract(resource.getSize());
        project.setStorageSize(newSize);
        s3Service.deleteFile(resource);

        resource.setUpdatedBy(teamMemberRepository.findById(userId));
        projectRepository.save(project);
        resourceRepository.save(resource);
    }

    private void addConnectionsResource(Resource resource, Project project, TeamMember teamMember) {
        resource.setProject(project);
        resource.setAllowedRoles(teamMember.getRoles());
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
    }
}
