package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.project.ProjectCreateDto;
import faang.school.projectservice.dto.client.project.ProjectFilterDto;
import faang.school.projectservice.dto.client.project.ProjectUpdateDto;
import faang.school.projectservice.dto.client.project.ProjectViewDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.FilterService;
import faang.school.projectservice.util.project.ProjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final UserContext userContext;
    private final FilterService<Project, ProjectFilterDto> filterService;
    private final S3Client s3Client;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    @Transactional
    public ProjectViewDto createProject(ProjectCreateDto projectDto) {
        boolean isHaveProjectWithSameName =
                repository.existsByOwnerIdAndName(userContext.getUserId(), projectDto.name());

        if (isHaveProjectWithSameName) {
            throw new RuntimeException("Пользователь пытается создать уже имеющийся у него проект");
        }

        Project project = mapper.toEntity(projectDto);

        project.setOwnerId(userContext.getUserId());
        project.setCreatedAt(LocalDateTime.now());
        log.info("Проект {} был создан.", projectDto.name());
        return mapper.toViewDto(repository.save(project));
    }

    @Override
    @Transactional
    public ProjectViewDto updateProject(long id, ProjectUpdateDto projectDto) {
        Project project = repository.getByIdOrThrow(id);
        if (!ProjectUtil.isAvailable(project, userContext.getUserId())) {
            throw new RuntimeException("У пользователя нет доступа к указанному проекту");
        }

        project.setUpdatedAt(LocalDateTime.now());
        mapper.update(projectDto, project);
        log.info("Проект с id = {} был обновлен входными данными", id);
        return mapper.toViewDto(repository.save(project));
    }

    @Override
    @Transactional
    public List<ProjectViewDto> getByFilters(ProjectFilterDto projectFilterDto) {
        List<Project> projectList = repository.findAll();
        List<Project> filteredList = filterService.getFilteredList(projectList, projectFilterDto);

        log.info("Получения списка всех проектов с фильтрами");
        return filteredList.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    @Transactional
    public ProjectViewDto getProjectById(long id) {
        Project project = repository.getByIdOrThrow(id);
        if (!ProjectUtil.isAvailable(project, userContext.getUserId())) {
            throw new RuntimeException("У пользователя нет доступа к указанному проекту");
        }
        log.info("Получение проекта по id = {}", id);
        return mapper.toViewDto(project);
    }

    @Override
    public ProjectViewDto linkCover(Long id, MultipartFile file) {
        final long MAX_FILE_SIZE = 5 * 1024 * 1024;
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new DataValidationException("Размер файла не должен превышать 5 Мб");
        }
        Project project = repository.getByIdOrThrow(id);
        if (!ProjectUtil.isAvailable(project, userContext.getUserId())) {
            throw new ForbiddenException("У пользователя нет доступа к указанному проекту");
        }
        try {
            BufferedImage image = correctFormat(file);
            saveImageInS3(file, image, project);
            return mapper.toViewDto(repository.save(project));
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при обработке файла", e);
        }
    }

    private BufferedImage correctFormat(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new DataValidationException("Некорректный формат изображения");
        }
        int width = image.getWidth();
        int height = image.getHeight();

        boolean isSquare = Math.abs(width - height) <= 1;
        int targetWidth = 1080;
        int targetHeight = isSquare ? 1080 : 566;

        if (width > targetWidth || height > targetHeight) {
            double widthRatio = (double) targetWidth / width;
            double heightRatio = (double) targetHeight / height;
            double scaleFactor = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (width * scaleFactor);
            int newHeight = (int) (height * scaleFactor);

            BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, image.getType());
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
            g2d.dispose();

            image = scaledImage;
            return image;
        }
        return image;
    }

    private void saveImageInS3(MultipartFile file, BufferedImage image, Project project) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = getExtension(file.getOriginalFilename());
        ImageIO.write(image, formatName, baos);
        byte[] imageBytes = baos.toByteArray();
        String fileId = UUID.randomUUID().toString();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileId)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromBytes(imageBytes)
        );
        project.setCoverImageId(fileId);
    }

    private String getExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return switch (extension) {
            case "jpg", "jpeg" -> "jpg";
            case "png" -> "png";
            case "bmp" -> "bmp";
            case "gif" -> "gif";
            default -> throw new DataValidationException("Не поддерживаемый формат изображения");
        };
    }
}
