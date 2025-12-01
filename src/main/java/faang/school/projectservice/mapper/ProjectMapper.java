package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.mapper.simple.MeetMapper;
import faang.school.projectservice.mapper.simple.MomentMapper;
import faang.school.projectservice.mapper.simple.ProjectSimpleMapper;
import faang.school.projectservice.mapper.simple.ResourceSimpleMapper;
import faang.school.projectservice.mapper.simple.ScheduleMapper;
import faang.school.projectservice.mapper.simple.StageMapper;
import faang.school.projectservice.mapper.simple.TaskMapper;
import faang.school.projectservice.mapper.simple.TeamMapper;
import faang.school.projectservice.mapper.simple.VacancyMapper;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {
        MeetMapper.class,
        MomentMapper.class,
        ProjectSimpleMapper.class,
        ResourceSimpleMapper.class,
        ScheduleMapper.class,
        StageMapper.class,
        TaskMapper.class,
        TeamMapper.class,
        VacancyMapper.class
})
public interface ProjectMapper {
    ProjectDto toDto(Project project);
    Project toEntity(ProjectDto dto);
}