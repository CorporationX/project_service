package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubProjectServiceImpl implements SubProjectService {
    private final SubProjectMapper subProjectMapper;
    private final ProjectRepository projectRepository;

    @Override
    public SubProjectDto create(long creatorId, CreateSubProjectDto createSubProjectDto) {
        Project subProject = subProjectMapper.toSubProject(createSubProjectDto);
        Project parentProject = projectRepository.findById(createSubProjectDto.parentProjectId()).get();
        subProject.setParentProject(parentProject);
        List<Team> teams ;
        Schedule schedule;
        List<Stage> stages;
        List<Vacancy> vacancies;
        List<Meet> meets;



        List<Long> teamIds,
        Long scheduleId,
        List<Long> stageIds,
        List<Long> vacancyIds,
        List<Long> meetIds,







        return null;
    }

    @Override
    public SubProjectDto update(long requesterId, UpdateSubProjectDto updateSubProjectDto) {
        return null;
    }

    @Override
    public boolean complete(long requesterId, long subprojectId) {
        return false;
    }

    @Override
    public SubProjectDto getById(long subprojectId) {
        return null;
    }

    @Override
    public List<SubProjectDto> getAllByParentProject(long parentProjectId) {
        return List.of();
    }

    @Override
    public boolean delete(long requesterId, long subprojectId) {
        return false;
    }






    //OwnerId нужно подставлять из UserContext.
    //И проверяй: подпроект нельзя создавать от чужого проекта.
    //Нельзя создать публичный подпроект для приватного родительского проекта.
    //Нельзя закрыть проект, если у него есть все ещё открытые подпроекты. Нужно сначала закрывать все подпроекты, и только потом родительский проект
    //Проверить, и если у проекта закрылись все его подпроекты, тогда получаем Moment, а участники проекта становятся участниками момента. Получается, что у проекта есть момент “Выполнены все подпроекты”.
    //Если подпроект становится приватным, то и все его подпроекты должны стать приватными.
    //Не забудьте проверить видимость проекта. Может быть такое, что публичный проект имеет секретный подпроект, тогда его нельзя показывать другим участникам.

}
