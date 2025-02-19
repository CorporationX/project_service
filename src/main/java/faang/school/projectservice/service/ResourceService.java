package faang.school.projectservice.service;

import faang.school.projectservice.dto.resource.CreateResourceDto;
import faang.school.projectservice.dto.resource.ResourceResultDto;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.mapper.ResourceResultMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MinioService minioService;
    private final ResourceMapper resourceMapper;
    private final ResourceResultMapper resourceResultMapper;

    public Resource getResourceRefById(long id) {
        return resourceRepository.getReferenceById(id);
    }

    public ResourceResultDto uploadResource(MultipartFile file, Long projectId, Long teamMemberId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл пустой");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Проект не найден"));

        BigInteger currentSize = project.getStorageSize() == null ? BigInteger.ZERO : project.getStorageSize();
        BigInteger fileSize = BigInteger.valueOf(file.getSize());

        BigInteger maxSize = project.getMaxStorageSize() == null
                ? BigInteger.valueOf(2L * 1024 * 1024 * 1024)
                : project.getMaxStorageSize();
        if (currentSize.add(fileSize).compareTo(maxSize) > 0) {
            throw new IllegalArgumentException("Превышен лимит хранилища проекта");
        }

        String key = "project-" + projectId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file.getBytes())) {
            minioService.uploadFile(byteArrayInputStream, key, file.getContentType(), file.getSize());
        } catch (IOException e) {
            log.error("Ошибка при получении потока файла", e);
            throw new RuntimeException("Ошибка при получении потока файла", e);
        } catch (Exception e) {
            log.error("Ошибка загрузки файла в MinIO", e);
            throw new RuntimeException("Ошибка загрузки файла в MinIO", e);
        }

        CreateResourceDto dto = new CreateResourceDto(
                file.getOriginalFilename(),
                key,
                file.getSize(),
                file.getContentType(),
                teamMemberId,
                projectId
        );

        Resource resource = resourceMapper.toResource(dto);
        Resource savedResource = resourceRepository.save(resource);

        project.setStorageSize(currentSize.add(fileSize));
        projectRepository.save(project);

        log.info("Файл '{}' успешно загружен в проект '{}'", file.getOriginalFilename(), project.getName());
        return resourceResultMapper.toResultDto(savedResource);
    }

    public void deleteResource(Long resourceId, Long teamMemberId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ресурс не найден"));

        TeamMember currentUser = teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        if (!resource.getCreatedBy().getId().equals(teamMemberId)
                && !currentUser.getRoles().contains(TeamRole.MANAGER)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "У пользователя нет прав для удаления данного файла");
        }

        if (resource.getKey() != null) {
            minioService.removeFile(resource.getKey());
        }

        Project project = resource.getProject();
        BigInteger currentSize = project.getStorageSize() == null ? BigInteger.ZERO : project.getStorageSize();
        BigInteger fileSize = resource.getSize() == null ? BigInteger.ZERO : resource.getSize();
        if (currentSize.compareTo(fileSize) >= 0) {
            project.setStorageSize(currentSize.subtract(fileSize));
        } else {
            project.setStorageSize(BigInteger.ZERO);
        }
        projectRepository.save(project);

        resource.setKey(null);
        resource.setSize(BigInteger.ZERO);
        resource.setStatus(ResourceStatus.DELETED);

        TeamMember updater = new TeamMember();
        updater.setId(teamMemberId);
        resource.setUpdatedBy(updater);

        resourceRepository.save(resource);

        log.info("Ресурс с ID {} успешно удалён пользователем с ID {}", resourceId, teamMemberId);
    }
}
