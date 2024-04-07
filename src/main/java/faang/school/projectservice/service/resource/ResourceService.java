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
        TeamMember user = teamMemberRepository.findById(userId);
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
        if (project.getResources() == null) {
            project.setResources(new ArrayList<>());
        }
        project.getResources().add(resource);
        projectRepository.save(project);
        log.info("Успешно загрузили объект Project в репозиторий {}", project.getName());

        return resourceMapper.toDto(resource);
    }

    public ResourceDto updateFileFromProject(Long user_id, Long resource_id, MultipartFile file) {
        TeamMember user = teamMemberRepository.findById(user_id);
        log.info("Успешно получили объект TeamMember {}", user.getId());

        Resource resource = resourceRepository.findById(resource_id).orElseThrow(() -> new EntityNotFoundException(String.format("Ресурс по id: %d не найден", resource_id)));
        log.info("Успешно получили объект Resource {}", resource.getName());

        Project project = resource.getProject();
        log.info("Успешно получили объект Project {}", project.getName());

        resourceValidation.checkingUserForUpdatingFile(user, resource);
        log.info("Успешно прошли валидацию на право изменения ресурса");

        long fileSize = file.getSize();
        BigInteger newStorageSize = project.getStorageSize().subtract(resource.getSize()).add(BigInteger.valueOf(fileSize));
        int compareStorage = newStorageSize.compareTo(project.getMaxStorageSize());
        projectValidation.projectSizeIsFull(compareStorage);
        log.info("Успешно прошли валидацию на заполненность проекта ресурсами");

        String key = resource.getKey();
        Resource updatedResource = s3Client.uploadFile(file, key);
        log.info("Успешно обновили и загрузили объект в облако и получили Resource {}", updatedResource.getName());

        updatedResource.setId(resource_id);
        updatedResource.setCreatedBy(resource.getCreatedBy());
        updatedResource.setUpdatedBy(user);
        updatedResource.setProject(project);
        updatedResource.setCreatedAt(resource.getCreatedAt());
        resourceRepository.save(updatedResource);
        log.info("Успешно обновили объект Resource в репозитории {}", updatedResource.getName());

        project.setStorageSize(newStorageSize);
        project.getResources().remove(resource);
        project.getResources().add(updatedResource);
        projectRepository.save(project);
        log.info("Успешно обновили данные в объекте Project и сохранили в репозиторий {}", project.getName());

        return resourceMapper.toDto(updatedResource);
    }

    public void deleteFileFromProject(Long user_id, Long resource_id) {
        TeamMember user = teamMemberRepository.findById(user_id);
        log.info("Успешно получили объект TeamMember {}", user.getId());

        Resource resource = resourceRepository.findById(resource_id).orElseThrow(() -> new EntityNotFoundException(String.format("Ресурс по id: %d не найден", resource_id)));
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

        resource.setKey(null);
        resource.setSize(null);
        resource.setProject(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(user);

        resourceRepository.save(resource);
        log.info("Успешно удалили данные о Resource {} ", resource.getName());
    }
}
