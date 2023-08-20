package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.resource.GetResourceDto;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.InvalidCurrentUserException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.model.resource.Resource;
import faang.school.projectservice.model.resource.ResourceStatus;
import faang.school.projectservice.model.resource.ResourceType;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.util.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectFileService {
    private final ProjectRepository projectRepository;
    private final ResourceRepository resourceRepository;
    private final FileService fileService;
    private final TeamMemberRepository teamMemberRepository;
    private final ResourceMapper resourceMapper;

    @Transactional
    public ResourceDto uploadFile(MultipartFile multipartFile, long projectId, long teamMemberId) {
        Project project = projectRepository.getProjectById(projectId);
        validateTeamMember(project, teamMemberId);
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);

        String objectKey = fileService.upload(multipartFile, projectId);

        Resource resource = Resource.builder()
                .name(multipartFile.getName())
                .key(objectKey)
                .size(BigInteger.valueOf(multipartFile.getSize()))
                .type(ResourceType.getResourceType(multipartFile.getContentType()))
                .createdBy(teamMember)
                .project(project)
                .build();

        updateProjectStorage(resource);// написать валидацию на сторедж перед тем как загружать файл
        resourceRepository.save(resource);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public void deleteFile(long resourceId, long teamMemberId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        validateIfUserCanChangeFile(resource, teamMemberId);

        if (!resource.getStatus().equals(ResourceStatus.DELETED)) {
            fileService.delete(resource.getKey());

            resource.setStatus(ResourceStatus.DELETED);
            updateProjectStorage(resource);
            resourceRepository.save(resource);
        }
    }

    public GetResourceDto getFile(long resourceId, long teamMemberId) {
        Resource resource = resourceRepository.getReferenceById(resourceId);
        validateTeamMember(resource.getProject(), teamMemberId);

        return GetResourceDto.builder()
                .name(resource.getName())
                .type(resource.getType())
                .inputStream(fileService.getFile(resource.getKey()))
                .build();
    }


    private void validateTeamMember(Project project, long teamMemberId) {
        List<Long> projectMembers = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .distinct()
                .toList();

        if (!projectMembers.contains(teamMemberId)) {
            String errorMessage = String.format("The team member with id: %d is not on the project", teamMemberId);
            throw new InvalidCurrentUserException(errorMessage);
        }
    }

//    private void validateProjectStorage(Project project, BigInteger fileSize){
//       if (fileSize.compareTo(project.getStorageSize()) <= 0){
//           throw new
//       }
//    }

    private void updateProjectStorage(Resource resource) {
        Project project = resource.getProject();
        BigInteger storageSize = project.getStorageSize();
        BigInteger resourceSize = resource.getSize();

        if (resource.getStatus().equals(ResourceStatus.DELETED)) {
            project.setStorageSize(storageSize.add(resourceSize));
        } else if (resourceSize.compareTo(storageSize) <= 0) {
            project.setStorageSize(storageSize.subtract(resourceSize));
        }

        projectRepository.save(project);
    }

    private void validateIfUserCanChangeFile(Resource resource, long teamMemberId) {
        boolean notAProjectManager = !userIsProjectManager(resource.getProject(), teamMemberId);
        boolean notAFileCreator = !userIsFileCreator(resource, teamMemberId);

        if (notAProjectManager && notAFileCreator) {
            throw new InvalidCurrentUserException("You should be creator of a file or a project manager to change files");
        }
    }

    private boolean userIsProjectManager(Project project, long teamMemberId) {
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .distinct()
                .anyMatch(teamMember ->
                        teamMember.getRoles().contains(TeamRole.MANAGER) && teamMember.getId().equals(teamMemberId));
    }

    private boolean userIsFileCreator(Resource resource, long teamMemberId) {
        return resource.getCreatedBy().getId().equals(teamMemberId);
    }
}
