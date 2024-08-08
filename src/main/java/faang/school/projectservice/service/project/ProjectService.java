package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.Validator;
import faang.school.projectservice.service.project.subproject_filter.SubProjectFilter;
import faang.school.projectservice.service.project.update_subproject_param.UpdateSubProjectParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public String addCover(long projectId, MultipartFile file) throws IOException {
        Project project = repository.getProjectById(projectId);
        long fileSize = file.getSize();

        BufferedImage inputImage = null;
        BufferedImage outputImage = null;
        try {
            inputImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            log.error("Ошибка при чтении изображения.", e);
            throw new RuntimeException(e);
        }
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();

        if (!validator.validateFileSize(fileSize)) {
            log.error("Размер файла {} превышает ограничение.", fileSize);
            return "Файл не был загружен.";
        } else if (!validator.validatesImageSize(width, height)) {
            outputImage = imageResizer.resizeImage(inputImage,
                    validator.getMaxWidth(),
                    validator.getMaxHeight());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(outputImage, "jpg", outputStream);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            MultipartFile newFile = new MultipartFile() {
                @Override
                public String getName() {
                    return null;
                }

                @Override
                public String getOriginalFilename() {
                    return null;
                }

                @Override
                public String getContentType() {
                    return null;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public long getSize() {
                    return 0;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return new byte[0];
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return null;
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {

                }
            };
            return "Файл успешно загружен.";//переделать
        } else {
            String folder = projectId + project.getName();
            s3ServiceImpl.uploadFile(file, folder);
            log.info("Файл {} загружен в облако.", file.getOriginalFilename());
            return "Файл успешно загружен.";
        }
    }

}
