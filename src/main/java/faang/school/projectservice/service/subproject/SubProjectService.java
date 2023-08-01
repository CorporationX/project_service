package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubProjectService {
    private final ProjectRepository projectRepository;
    public CreateProjectDto createProject(CreateProjectDto createProjectDto){

        return createProjectDto;
    }

    public boolean existsByOwnerUserIdAndName(Long ownerId, String name){
        return projectRepository.existsByOwnerUserIdAndName(ownerId,name);
    }


}
