package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.util.ChildrenNotFinishedException;
import org.springframework.stereotype.Service;


public interface SubProjectService {

    public CreateSubProjectDto createSubProject(Project subProjectDto);

    public CreateSubProjectDto refreshSubProject(Project subProjectDto) throws ChildrenNotFinishedException;

}
