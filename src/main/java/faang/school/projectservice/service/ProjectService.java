package faang.school.projectservice.service;

import faang.school.projectservice.dto.SubProjectDto;
import faang.school.projectservice.dto.filter.SubProjectFilterDto;
import faang.school.projectservice.filter.subprojectfilter.SubProjectFilter;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.SubProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final SubProjectValidator subProjectValidator;
    private final SubProjectMapper subProjectMapper;
    private final ProjectRepository projectRepository;
    private final List<SubProjectFilter> subProjectFilters;

    public SubProjectDto createSubProject(SubProjectDto subProjectDto) {
        checkAllValidationsForCreateSubProject(subProjectDto);
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        Project save = projectRepository.save(subProject);

        return subProjectMapper.toDto(projectRepository.save(save));
    }

//    public SubProjectDto updateSubProject(SubProjectDto subProjectDto) {
//        checkAllValidationsForUpdateSubProject(subProjectDto);
//    }

    public List<SubProjectDto> getAllFilteredSubprojectsOfAProject(SubProjectFilterDto subProjectFilterDto,
                                                                   Long projectId) {
        Project parentProject = projectRepository.getProjectById(projectId);
        Stream<Project> projects = parentProject.getChildren().stream()
                        .filter(project -> project.getVisibility().equals(ProjectVisibility.PUBLIC));
        return subProjectFilters.stream()
                .filter(filter -> filter.isApplicable(subProjectFilterDto))
                .reduce(projects, (stream, filter) -> filter.apply(stream, subProjectFilterDto), Stream::concat)
                .map(subProjectMapper::toDto)
                .toList();
    }

    private void checkAllValidationsForCreateSubProject(SubProjectDto subProjectDto) {
        Long parentProjectId = subProjectDto.getParentProjectId();
        Long subProjectId = subProjectDto.getId();
        subProjectValidator.validateTheExistenceOfTheParenProject(parentProjectId);
        subProjectValidator.validateRootProjectHasNotParentProject(parentProjectId);
        //subProjectValidator.validateSubProjectHasParentProject(subProjectId);
        subProjectValidator.checkThePublicityOfTheProject(subProjectId, parentProjectId);
    }

    private void checkAllValidationsForUpdateSubProject(SubProjectDto subProjectDto) {
        Long subProjectId = subProjectDto.getId();
        subProjectValidator.validateCompletionOfTheStatusSubprojectsChild(subProjectDto);
        subProjectValidator.validateTheCoincidenceOfTheVisibilityOfProjectsAndSubprojects(subProjectId);
    }
}
