package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectCoverDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.Validator;
import faang.school.projectservice.service.project.subproject_filter.SubProjectFilter;
import faang.school.projectservice.service.project.update_subproject_param.UpdateSubProjectParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {
    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final List<UpdateSubProjectParam> updateSubProjectParams;
    private final List<SubProjectFilter> subProjectFilters;
    private final Validator validator;
    private final S3ServiceImpl s3ServiceImpl;
    private final ImageResizer imageResizer;
    private final ResourceRepository resourceRepository;


    public ProjectDto createSubProject(ProjectDto projectDto) {
        validateCreateSubProject(projectDto);
        projectDto.setStatus(ProjectStatus.CREATED);
        Project project = mapper.toEntity(projectDto);
        return mapper.toDto(repository.save(project));
    }

    public ProjectDto updateSubProject(ProjectDto projectDto, UpdateSubProjectDto updateSubProjectDto) {
        List<UpdateSubProjectParam> updateParams = updateSubProjectParams.stream()
                .filter(param -> param.isExecutable(updateSubProjectDto))
                .toList();

        for (UpdateSubProjectParam param : updateParams) {
            if (param.isExecutable(updateSubProjectDto)) {
                param.execute(projectDto, updateSubProjectDto);
            }
        }

        if (!updateParams.isEmpty()) {
            Project returnProject = repository.save(mapper.toEntity(projectDto));
            projectDto = mapper.toDto(returnProject);
        }
        return projectDto;
    }

    public List<ProjectDto> getSubProject(ProjectDto projectDto, SubProjectFilterDto subProjectFilter) {
        Project project = repository.getProjectById(projectDto.getId());
        if (project.getChildren() == null) {
            return new ArrayList<>();
        }
        List<Project> projectChildren = project.getChildren();
        List<SubProjectFilter> filters = subProjectFilters.stream()
                .filter(filter -> filter.isApplecable(subProjectFilter))
                .toList();
        for (SubProjectFilter filter : filters) {
            projectChildren = filter.apply(projectChildren, subProjectFilter);
        }

        return mapper.toDto(projectChildren);
    }

    private void validateCreateSubProject(ProjectDto projectDto) {
        if (projectDto.getParentProject() == null || projectDto.getParentProject().getId() == 0) {
            throw new IllegalArgumentException("Подпроект должен иметь родительский проект");
        }

        if (projectDto.getVisibility() == ProjectVisibility.PRIVATE
                && projectDto.getParentProject().getVisibility() == ProjectVisibility.PUBLIC
        ) {
            throw new IllegalArgumentException("Нельзя создать приватный подпроект для публичного проекта");
        }

        if (projectDto.getVisibility() == ProjectVisibility.PUBLIC
                && projectDto.getParentProject().getVisibility() == ProjectVisibility.PRIVATE
        ) {
            throw new IllegalArgumentException("Нельзя создать публичный подпроект для приватного проекта");
        }
    }

    public ProjectDto getProject(long projectId) {
        Project project = repository.getProjectById(projectId);

        return mapper.toDto(project);
    }

    public List<ProjectDto> getProjectsByIds(List<Long> ids) {
        List<Project> projects = repository.findAllByIds(ids);

        return projects.stream().map(mapper::toDto).toList();
    }

    public Project getOneOrThrow(long projectId) {
        return repository.getProjectById(projectId);
    }

    public ProjectCoverDto addCover(long projectId, MultipartFile file) {
        Project project = repository.getProjectById(projectId);
        long fileSize = file.getSize();

        Resource resource = new Resource();
        BufferedImage inputImage = convertImage(convertToInputStream(file));
        InputStream resizedImage;

        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        int maxWidth = validator.getMaxWidth();
        int maxHeight = validator.getMaxHeight();

        if (!validator.validateFileSize(fileSize)) {
            log.error("Размер файла {} превышает ограничение.", fileSize);
        } else if (validator.validatesImageSize(width, height)) {
            try {
                resizedImage = imageResizer.resizeImage(file.getInputStream(),
                        maxWidth,
                        maxHeight);
            } catch (IOException e) {
                log.error("Ошибка преобразования изображения.", e);
                throw new RuntimeException(e);
            }
            BufferedImage convertedImage = convertImage(resizedImage);
            log.info("Файл преобразован.");
            System.out.println("convertedImage.getWidth() = " + convertedImage.getWidth());
            resource = uploadFile(file, projectId, project.getName(),
                    convertedImage.getWidth(), convertedImage.getHeight());
            resourceRepository.save(resource);
        } else {
            resource = uploadFile(file, projectId, project.getName(), width, height);
            resourceRepository.save(resource);
        }
        project.setCoverImageId(resource.getKey());
        return mapper.toProjectCoverDto(repository.save(project));
    }

    private Resource uploadFile(MultipartFile multipartFile,
                                long projectId, String projectName,
                                int width,
                                int height) {

        String folder = projectId + projectName;
        Resource resource = s3ServiceImpl.uploadFile(multipartFile, folder,
                width, height);
        log.info("Файл {} загружен в облако.", multipartFile.getOriginalFilename());
        return resource;
    }

    private BufferedImage convertImage(InputStream file) {
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            log.error("Ошибка при чтении изображения.", e);
            throw new RuntimeException(e);
        }
        return image;
    }

    private InputStream convertToInputStream(MultipartFile file) {
        InputStream inputStreamFile;
        try {
            inputStreamFile = file.getInputStream();
        } catch (IOException e) {
            log.info("Не удалось конвертировать в InputStream {}.",
                    file.getOriginalFilename());
            throw new RuntimeException(e);
        }
        return inputStreamFile;
    }


}
