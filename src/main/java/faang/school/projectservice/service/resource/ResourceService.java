package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.handler.exceptions.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.validation.project.ProjectValidation;
import faang.school.projectservice.validation.resource.ResourceValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final S3Service s3Client;
    private final TeamMemberRepository teamMemberRepository;
    private final ResourceMapper resourceMapper;
    private final ProjectRepository projectRepository;
    private final ProjectValidation projectValidation;
    private final ResourceValidation resourceValidation;


    @Transactional
    public ResourceDto uploadFileToProject(Long userId, MultipartFile file) {
        TeamMember user = getTeamMember(userId);
        log.info("Успешно получили объект TeamMember {}", user.getId());
        Project project = user.getTeam().getProject();
        log.info("Успешно получили объект Project {}", project.getName());

        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        int compareStorage = newStorageSize.compareTo(project.getMaxStorageSize());
        projectValidation.projectSizeIsFull(compareStorage);
        log.info("Успешно прошли валидацию на заполненность проекта ресурсами");
        String folder = project.getName() + project.getId();
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        Resource resource = s3Client.uploadFile(file, key);
        log.info("Успешно загрузили объект в облако и получили Resource {}", resource.getName());
        //resource.setAllowedRoles(user.getRoles());
        resource.setCreatedBy(user);
        resource.setUpdatedBy(user);
        resource.setProject(project);
        resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        project.getResources().add(resource);
        projectRepository.save(project);
        log.info("Успешно загрузили объект Project в репозиторий {}", project.getName());

        return resourceMapper.toDto(resource);
    }

    public ResourceDto updateFileFromProject(Long user_id, Long resource_id, MultipartFile file) {
        TeamMember user = getTeamMember(user_id);
        log.info("Успешно получили объект TeamMember {}", user.getId());

        Resource resource = getResource(resource_id);
        log.info("Успешно получили объект Resource {}", resource.getName());

        Project project = resource.getProject();
        log.info("Успешно получили объект Project {}", project.getName());

        resourceValidation.checkingUserForUpdatingFile(user, resource);
        log.info("Успешно прошли валидацию на право изменения ресурса");

        BigInteger newStorageSize = currentSizeStorage(resource, project, file);
        int compareStorage = newStorageSize.compareTo(project.getMaxStorageSize());
        projectValidation.projectSizeIsFull(compareStorage);
        log.info("Успешно прошли валидацию на заполненность проекта ресурсами");

        Resource updatedResource = updateResource(resource, user, file);
        log.info("Успешно обновили и загрузили объект в облако и получили Resource {}", updatedResource.getName());

        resourceRepository.save(updatedResource);
        log.info("Успешно обновили объект Resource в репозитории {}", updatedResource.getName());

        upadateProject(project, resource, updatedResource, newStorageSize);
        log.info("Успешно обновили данные в объекте Project и сохранили в репозиторий {}", project.getName());

        return resourceMapper.toDto(updatedResource);
    }

    public void deleteFileFromProject(Long user_id, Long resource_id) {
        TeamMember user = getTeamMember(user_id);
        log.info("Успешно получили объект TeamMember {}", user.getId());

        Resource resource = getResource(resource_id);
        log.info("Успешно получили объект Resource {}", resource.getName());

        Project project = resource.getProject();
        log.info("Успешно получили объект Project {}", project.getName());

        resourceValidation.checkingUserForDeletingFile(user, resource);
        log.info("Успешно прошли валидацию на право удаления ресурса");

        s3Client.deleteFile(resource.getKey());
        log.info("Успешно удалили из облака Resource {}", resource.getName());

        project.getResources().remove(resource);
        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
        projectRepository.save(project);
        log.info("Успешно данные в объекте Project и сохранили в репозиторий {}", project.getName());

        deleteResource(resource, user);
        resourceRepository.save(resource);
        log.info("Успешно удалили данные о Resource {} ", resource.getName());
    }

    private TeamMember getTeamMember(Long user_id) {
        return teamMemberRepository.findById(user_id);
    }

    private Resource getResource(Long resource_id) {
        return resourceRepository.findById(resource_id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Ресурс по id: %d не найден", resource_id)));
    }

    private BigInteger currentSizeStorage(Resource resource, Project project, MultipartFile file) {
        long fileSize = file.getSize();
        return project.getStorageSize().subtract(resource.getSize()).add(BigInteger.valueOf(fileSize));
    }

    private Resource updateResource(Resource resource, TeamMember user, MultipartFile file) {
        String key = resource.getKey();
        Resource updatedResource = s3Client.uploadFile(file, key);

        updatedResource.setId(resource.getId());
        updatedResource.setCreatedBy(resource.getCreatedBy());
        updatedResource.setUpdatedBy(user);
        updatedResource.setProject(resource.getProject());
        updatedResource.setCreatedAt(resource.getCreatedAt());
        return updatedResource;
    }

    private Project upadateProject(Project project, Resource removeResource, Resource saveResource, BigInteger newStorageSize) {
        project.setStorageSize(newStorageSize);
        project.getResources().remove(removeResource);
        project.getResources().add(saveResource);
        return projectRepository.save(project);
    }

    private void deleteResource(Resource resource, TeamMember user) {
        resource.setKey(null);
        resource.setSize(null);
        resource.setProject(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(user);
    }
}
