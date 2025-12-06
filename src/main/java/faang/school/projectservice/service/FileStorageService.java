package faang.school.projectservice.service;

import faang.school.projectservice.dto.FileDownloadResponse;
import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.exception.StorageLimitExceededException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ResourceRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {
    private static final long BYTES_PER_MB = 1_000_000L;
    private static final String PROJECT_KEY_TEMPLATE = "project-%d/%s-%s-%s";
    private static final String SANITIZE_PATTERN = "[^a-zA-Z0-9.-]";
    private static final String SANITIZE_DUPLICATE_PATTERN = "_{2,}";
    private static final String UNDERSCORE_REPLACEMENT = "_";

    private final MinioClient minioClient;
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final Tika tika;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${file-storage.max-file-size}")
    private long maxFileSize;

    @Value("${file-storage.blocked-extensions}")
    private String blockedExtensionsString;

    @Value("${file-storage.presigned-url-expiry-seconds}")
    private int presignedUrlExpirySeconds;

    @Value("${file-storage.uuid-substring-length}")
    private int uuidSubstringLength;

    @Value("${file-storage.default-content-type}")
    private String defaultContentType;

    private Set<String> blockedExtensions;
    private long maxFileSizeMb;

    @PostConstruct
    private void init() {
        blockedExtensions = Arrays.stream(blockedExtensionsString.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        maxFileSizeMb = maxFileSize / BYTES_PER_MB;
    }

    @Transactional
    public Resource uploadFile(MultipartFile file, Long projectId, Long teamMemberId, Set<TeamRole> allowedRoles) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        validateFile(file);

        if (allowedRoles == null) {
            allowedRoles = Set.of();
        }

        Project project = findProjectById(projectId);
        validateStorageLimit(project, file.getSize());

        TeamMember teamMember = findTeamMemberById(teamMemberId);
        List<TeamRole> teamRoleList = getAllowedTeamRoles(allowedRoles, teamMember);

        try {
            String key = generateStorageKey(projectId, file.getOriginalFilename());
            uploadToMinio(file, key);

            String contentType = detectContentType(file);
            Resource resource = buildResource(file, key, contentType, teamRoleList, project, teamMember);

            resource = resourceRepository.save(resource);
            updateProjectStorageSize(project.getId());

            log.info("File uploaded successfully: {} for project {}", key, projectId);
            return resource;

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error uploading file {} to project {}", file.getOriginalFilename(), projectId, e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    public FileDownloadResponse downloadFile(Long resourceId, Long projectId, Long teamMemberId) 
            throws AccessDeniedException {
        log.info("Downloading resource {} from project {} for member {}", resourceId, projectId, teamMemberId);

        Resource resource = findResourceByProjectId(resourceId, projectId);
        validateAccess(resource, teamMemberId);

        if (resource.getStatus() != ResourceStatus.ACTIVE) {
            throw new IllegalStateException(
                    String.format(
                            "Resource %d is not active (status: %s) in project %d",
                            resourceId, resource.getStatus(), projectId));
        }

        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(resource.getKey())
                            .build()
            );

            return FileDownloadResponse.builder()
                    .fileName(resource.getName())
                    .size(resource.getSize() != null ? resource.getSize().longValue() : null)
                    .contentType(resource.getContentType())
                    .inputStream(response)
                    .build();

        } catch (Exception e) {
            log.error("Failed to download file: resourceId={}, projectId={}", resourceId, projectId, e);
            throw new RuntimeException(
                    String.format("Failed to download file: resourceId=%d, projectId=%d", resourceId, projectId), e);
        }
    }

    @Transactional
    public void deleteFile(Long resourceId, Long projectId, Long teamMemberId) 
            throws AccessDeniedException {
        log.info("Deleting resource {} from project {} by member {}", resourceId, projectId, teamMemberId);

        Resource resource = findResourceByProjectId(resourceId, projectId);
        TeamMember teamMember = findTeamMemberById(teamMemberId);
        validateDeletePermission(resource, teamMember);

        if (resource.getStatus() == ResourceStatus.DELETED) {
            log.warn("Resource {} is already deleted in project {}", resourceId, projectId);
            return;
        }

        try {
            if (resource.getKey() != null) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(resource.getKey())
                                .build()
                );
                log.info("File removed from MinIO: {}", resource.getKey());
            }

            resource.setKey(null);
            resource.setSize(BigInteger.ZERO);
            resource.setStatus(ResourceStatus.DELETED);
            resource.setUpdatedBy(teamMember);
            resourceRepository.save(resource);

            updateProjectStorageSize(resource.getProject().getId());

            log.info("Resource {} deleted successfully from project {}", resourceId, projectId);

        } catch (Exception e) {
            log.error("Failed to delete file: resourceId={}, projectId={}", resourceId, projectId, e);
            throw new RuntimeException(
                    String.format("Failed to delete file: resourceId=%d, projectId=%d", resourceId, projectId), e);
        }
    }

    public Page<ResourceDto> getProjectFiles(Long projectId, Long teamMemberId, Pageable pageable) {
        TeamMember teamMember = teamMemberRepository
                .findByIdAndProjectId(teamMemberId, projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Team member %d is not a member of project %d", teamMemberId, projectId)));

        Page<Resource> resources = resourceRepository
                .findByProjectIdAndStatus(projectId, ResourceStatus.ACTIVE, pageable);

        return resources.map(this::toDto);
    }

    public String generatePresignedUrl(Long resourceId, Long projectId, Long teamMemberId) 
            throws AccessDeniedException {
        Resource resource = findResourceByProjectId(resourceId, projectId);
        validateAccess(resource, teamMemberId);

        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(resource.getKey())
                            .expiry(presignedUrlExpirySeconds, TimeUnit.SECONDS)
                            .build()
            );

            log.info("Generated presigned URL for resource {} in project {}", resourceId, projectId);
            return url;

        } catch (Exception e) {
            log.error("Failed to generate presigned URL: resourceId={}, projectId={}", resourceId, projectId, e);
            throw new RuntimeException(
                    String.format(
                            "Failed to generate download URL: resourceId=%d, projectId=%d",
                            resourceId, projectId), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    String.format("File size %d bytes exceeds maximum allowed size of %d MB (%d bytes)",
                            file.getSize(), maxFileSizeMb, maxFileSize));
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (blockedExtensions.contains(extension)) {
            throw new IllegalArgumentException(
                    String.format("File type is not allowed: %s. File: %s", extension, file.getOriginalFilename()));
        }
    }

    private void validateStorageLimit(Project project, long fileSize) {
        BigInteger currentSize = project.getStorageSize() != null
                ? project.getStorageSize()
                : BigInteger.ZERO;
        BigInteger newSize = currentSize.add(BigInteger.valueOf(fileSize));
        BigInteger maxSize = project.getMaxStorageSize() != null
                ? project.getMaxStorageSize()
                : BigInteger.ZERO;

        if (newSize.compareTo(maxSize) > 0) {
            long currentSizeMb = currentSize.longValue() / BYTES_PER_MB;
            long maxSizeMb = maxSize.longValue() / BYTES_PER_MB;
            throw new StorageLimitExceededException(
                    String.format("Storage limit exceeded. Current: %d MB, Limit: %d MB",
                            currentSizeMb, maxSizeMb));
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    private String generateStorageKey(Long projectId, String fileName) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String uuid = UUID.randomUUID().toString().substring(0, uuidSubstringLength);
        String sanitizedFileName = sanitizeFileName(fileName);

        return String.format(PROJECT_KEY_TEMPLATE, projectId, timestamp, uuid, sanitizedFileName);
    }

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll(SANITIZE_PATTERN, UNDERSCORE_REPLACEMENT)
                .replaceAll(SANITIZE_DUPLICATE_PATTERN, UNDERSCORE_REPLACEMENT)
                .toLowerCase();
    }

    private String detectContentType(MultipartFile file) {
        try {
            String detected = tika.detect(file.getInputStream());
            if (detected != null && !detected.isBlank()) {
                return detected;
            }
            return getDefaultContentType();
        } catch (Exception e) {
            String fileContentType = file.getContentType();
            return fileContentType != null && !fileContentType.isBlank()
                    ? fileContentType : getDefaultContentType();
        }
    }

    private String getDefaultContentType() {
        if (defaultContentType != null && !defaultContentType.isBlank()) {
            return defaultContentType;
        }
        return MediaType.APPLICATION_OCTET_STREAM_VALUE;
    }


    private void uploadToMinio(MultipartFile file, String key) throws Exception {
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(bucketName)
                .object(key)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());
    }

    private void updateProjectStorageSize(Long projectId) {
        Long totalSize = resourceRepository.calculateProjectStorageSize(projectId);
        totalSize = totalSize != null ? totalSize : 0L;
        projectRepository.updateStorageSize(projectId, totalSize);
        log.debug("Updated project {} storage size to {}", projectId, totalSize);
    }

    private void validateAccess(Resource resource, Long teamMemberId) 
            throws AccessDeniedException {
        TeamMember teamMember = teamMemberRepository
                .findByIdAndProjectId(teamMemberId, resource.getProject().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Team member %d is not a member of project %d",
                                teamMemberId, resource.getProject().getId())));

        if (resource.getAllowedRoles() == null || resource.getAllowedRoles().isEmpty()) {
            throw new IllegalStateException(
                    String.format("Resource %d has no allowed roles configured in project %d",
                            resource.getId(), resource.getProject().getId()));
        }

        boolean hasAccess = teamMember.getRoles().stream()
                .anyMatch(resource.getAllowedRoles()::contains);

        if (!hasAccess) {
            throw new AccessDeniedException(
                    String.format("Team member %d does not have permission to access resource %d in project %d",
                            teamMemberId, resource.getId(), resource.getProject().getId()));
        }
    }

    private void validateDeletePermission(Resource resource, TeamMember teamMember) 
            throws AccessDeniedException {
        boolean isCreator = resource.getCreatedBy().getId().equals(teamMember.getId());
        boolean isManager = teamMember.getRoles().contains(TeamRole.MANAGER);
        boolean canDelete = isCreator || isManager;

        if (!canDelete) {
            throw new AccessDeniedException(
                    String.format(
                            "Team member %d cannot delete resource %d in project %d. "
                                    + "Only file creator or project manager can delete files",
                            teamMember.getId(), resource.getId(), resource.getProject().getId()));
        }
    }

    private ResourceDto toDto(Resource resource) {
        Long size = resource.getSize() != null
                ? resource.getSize().longValue()
                : null;

        return ResourceDto.builder()
                .id(resource.getId())
                .name(resource.getName())
                .size(size)
                .type(resource.getType())
                .contentType(resource.getContentType())
                .createdBy(resource.getCreatedBy().getNickname())
                .createdAt(resource.getCreatedAt())
                .build();
    }

    private List<TeamRole> convertRolesToTeamRoles(Set<TeamRole> roles) {
        return roles.stream()
                .map(role -> {
                    try {
                        return TeamRole.valueOf(role.name());
                    } catch (IllegalArgumentException e) {
                        log.warn("Role {} cannot be converted to TeamRole, skipping", role);
                        return null;
                    }
                })
                .filter(teamRole -> teamRole != null)
                .collect(Collectors.toList());
    }

    private Project findProjectById(Long projectId) {
        return projectRepository.findByIdWithLock(projectId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Project not found: projectId=%d", projectId)));
    }

    private TeamMember findTeamMemberById(Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Team member not found: teamMemberId=%d", teamMemberId)));
    }

    private Resource findResourceByProjectId(Long resourceId, Long projectId) {
        return resourceRepository.findByIdAndProjectId(resourceId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Resource %d not found or doesn't belong to project %d",
                                resourceId, projectId)));
    }

    private List<TeamRole> getAllowedTeamRoles(Set<TeamRole> allowedRoles, TeamMember teamMember) {
        if (allowedRoles == null || allowedRoles.isEmpty()) {
            return new ArrayList<>(teamMember.getRoles());
        } else {
            return convertRolesToTeamRoles(allowedRoles);
        }
    }

    private Resource buildResource(MultipartFile file, String key, String contentType,
                                  List<TeamRole> teamRoleList, Project project, TeamMember teamMember) {
        return Resource.builder()
                .name(file.getOriginalFilename())
                .key(key)
                .size(BigInteger.valueOf(file.getSize()))
                .contentType(contentType)
                .type(ResourceType.getResourceType(contentType))
                .status(ResourceStatus.ACTIVE)
                .allowedRoles(teamRoleList)
                .project(project)
                .createdBy(teamMember)
                .updatedBy(teamMember)
                .build();
    }

}
