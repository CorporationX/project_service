package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validation.resource.ProjectResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectResourceServiceImpl implements ProjectResourceService {

    private final AmazonS3Service amazonS3Service;
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final ProjectResourceValidator projectResourceValidator;
    private final ResourceMapper resourceMapper;

    @Override
    @Transactional
    public ResourceDto saveFile(long userId, long projectId, MultipartFile file) {

        Project project = projectRepository.getProjectById(projectId);
        TeamMember teamMember = findTeamMemberByUserIdAndProjectId(userId, projectId);

        projectResourceValidator.validateMaxStorageSize(project, file.getSize());

        String path = "project/" + projectId + "/";
        String key = amazonS3Service.uploadFile(path, file);

        Resource resource = Resource.builder()
                .key(key)
                .name(file.getName())
                .size(BigInteger.valueOf(file.getSize()))
                .allowedRoles(teamMember.getRoles())
                .type(ResourceType.valueOf(file.getContentType()))
                .status(ResourceStatus.ACTIVE)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .project(project)
                .build();

        resource = resourceRepository.save(resource);

        return resourceMapper.toDto(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public InputStreamResource getFile(long userId, long projectId, long resourceId) {

        Resource resource = findResourceById(resourceId);
        findTeamMemberByUserIdAndProjectId(userId, projectId);

        return amazonS3Service.downloadFile(resource.getKey());
    }

    @Override
    @Transactional
    public void deleteFile(long userId, long projectId, long resourceId) {

        Resource resource = findResourceById(resourceId);
        TeamMember teamMember = findTeamMemberByUserIdAndProjectId(userId, projectId);

        projectResourceValidator.validateDeletePermission(teamMember, resource);

        amazonS3Service.deleteFile(resource.getKey());

        Project project = resource.getProject();
        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));

        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(teamMember);
        resource.setUpdatedAt(LocalDateTime.now());

        resourceRepository.save(resource);
    }

    private Resource findResourceById(long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resource with id=" + id + " not found"));
    }

    private TeamMember findTeamMemberByUserIdAndProjectId(long userId, long projectId) {
        return teamMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new NotFoundException("TeamMember with userId=" + userId + " and projectId= " + projectId + " not found"));
    }
}
