package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.client.SubProjectDto;
import faang.school.projectservice.dto.client.UpdateSubProjectDto;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SubProjectServiceImpl implements SubProjectService {

    private final ProjectRepository projectRepository;
    private final SubProjectMapper subProjectMapper;
    private final MomentRepository momentRepository;

    @Override
    public SubProjectDto createSubProject(CreateSubProjectDto subProjectDto) {

        if (subProjectDto.parentId() == null) {
            log.error("Parent id can not be null");
            throw new IllegalArgumentException("Parent id can not be null");
        }
        Project parentProject = projectRepository.findById(subProjectDto.parentId()).orElseThrow(() -> new RuntimeException("Parent project not found"));

        ProjectVisibility parentVisibility = parentProject.getVisibility();
        if (parentVisibility == ProjectVisibility.PRIVATE) {
            log.error("Parent project is private");
            throw new RuntimeException("Parent project is private");
        }

        Project subProjectToSave = subProjectMapper.toProjectEntity(subProjectDto);
        subProjectToSave.setStatus(ProjectStatus.CREATED);
        Project projectEntity = projectRepository.save(subProjectToSave);
        return subProjectMapper.toProjectResponseDto(projectEntity);
    }

    @Override
    public void updateSubProject(UpdateSubProjectDto updateSubProjectDto) {

        if (updateSubProjectDto.id() == null) {
            log.error("Project to update id can not be null");
            throw new IllegalArgumentException("Project to update id can not be null");
        }

        Project subProject = projectRepository.findById(updateSubProjectDto.id()).orElseThrow(() -> new RuntimeException("No project found to update"));

        subProject.getChildren().forEach(project -> {
            if (project.getStatus() != subProject.getStatus()) {
                throw new RuntimeException("Project status not same as subprojects statuses");
            }
        });

        if (subProject.getChildren().stream().allMatch(project -> project.getStatus() == ProjectStatus.CANCELLED)) {

            Moment moment = new Moment();
            moment.getProjects().add(subProject);

            subProject.getTeams().forEach(participant -> moment
                    .getUserIds()
                    .add(participant.getId()));

            moment.setName("Выполнены все подпроекты");
            momentRepository.save(moment);
        }
        if (updateSubProjectDto.visibility() == ProjectVisibility.PRIVATE) {
            subProject.getChildren().forEach(child -> {
                child.setVisibility(ProjectVisibility.PRIVATE);
                projectRepository.save(child);
            });
        }
    }

    //@Override
    //public List<Project> getSubProjectByFilter(SubProjectFilterDto subProjectFilterDto) {
    /*  не очень понимаю применение фильтров и как правильно их обрабатывать
        Помогите пж :)
     */
    //}
}
