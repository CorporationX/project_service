package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.simple.ProjectSimpleDto;
import faang.school.projectservice.dto.simple.ResourceSimpleDto;
import faang.school.projectservice.dto.simple.ScheduleSimpleDto;
import faang.school.projectservice.dto.simple.TaskSimpleDto;
import faang.school.projectservice.dto.simple.TeamSimpleDto;
import faang.school.projectservice.dto.simple.StageSimpleDto;
import faang.school.projectservice.dto.simple.VacancySimpleDto;
import faang.school.projectservice.dto.simple.MomentSimpleDto;
import faang.school.projectservice.dto.simple.MeetSimpleDto;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.stage.Stage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {
        ProjectMapper.NestedMapper.class
})
public interface ProjectMapper {

    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto dto);

    List<ProjectDto> toDtoList(List<Project> projects);

    List<Project> toEntityList(List<ProjectDto> dtos);

    @Mapper(componentModel = "spring")
    interface NestedMapper {
        // вложенные простые мапперы
        default ProjectSimpleDto projectToSimple(Project p) {
            if (p == null) return null;
            return new ProjectSimpleDto(p.getId(), p.getName());
        }
        default Project projectSimpleToEntity(ProjectSimpleDto d) {
            if (d == null) return null;
            Project p = new Project();
            p.setId(d.getId());
            p.setName(d.getName());
            return p;
        }

        // Task
        default TaskSimpleDto taskToSimple(Task t) {
            if (t == null) return null;
            return TaskSimpleDto.builder().id(t.getId()).title(t.getName()).status(t.getStatus()).build();
        }
        default faang.school.projectservice.model.Task taskSimpleToEntity(TaskSimpleDto d) {
            if (d == null) return null;
            var t = new faang.school.projectservice.model.Task();
            t.setId(d.getId()); t.setName(d.getTitle()); t.setStatus(d.getStatus());
            return t;
        }

        // Resource
        default ResourceSimpleDto resourceToSimple(Resource r) {
            if (r == null) return null;
            return ResourceSimpleDto.builder().id(r.getId()).name(r.getName()).type(r.getType()).build();
        }
        default faang.school.projectservice.model.Resource resourceSimpleToEntity(ResourceSimpleDto d) {
            if (d == null) return null;
            var r = new faang.school.projectservice.model.Resource();
            r.setId(d.getId()); r.setName(d.getName()); r.setType(d.getType());
            return r;
        }

        // Team
        default TeamSimpleDto teamToSimple(Team t) {
            if (t == null) return null;
            return TeamSimpleDto.builder().id(t.getId()).build();
        }
        default Team teamSimpleToEntity(TeamSimpleDto d) {
            if (d == null) return null;
            var t = new faang.school.projectservice.model.Team();
            t.setId(d.getId());
            return t;
        }

        // Schedule
        default ScheduleSimpleDto scheduleToSimple(Schedule s) {
            if (s == null) return null;
            return ScheduleSimpleDto.builder().id(s.getId()).name(s.getName()).build();
        }
        default Schedule scheduleSimpleToEntity(ScheduleSimpleDto d) {
            if (d == null) return null;
            var s = new faang.school.projectservice.model.Schedule();
            s.setId(d.getId()); s.setName(d.getName());
            return s;
        }

        // Stage
        default StageSimpleDto stageToSimple(Stage s) {
            if (s == null) return null;
            return StageSimpleDto.builder().id(s.getStageId()).name(s.getStageName()).build();
        }
        default Stage stageSimpleToEntity(StageSimpleDto d) {
            if (d == null) return null;
            var s = new Stage();
            s.setStageId(d.getId()); s.setStageName(d.getName());
            return s;
        }

        // Vacancy
        default VacancySimpleDto vacancyToSimple(Vacancy v) {
            if (v == null) return null;
            return VacancySimpleDto.builder().id(v.getId()).name(v.getName()).build();
        }
        default Vacancy vacancySimpleToEntity(VacancySimpleDto d) {
            if (d == null) return null;
            var v = new Vacancy();
            v.setId(d.getId()); v.setName(d.getName());
            return v;
        }

        // Moment
        default MomentSimpleDto momentToSimple(Moment m) {
            if (m == null) return null;
            return MomentSimpleDto.builder().id(m.getId()).name(m.getName()).build();
        }
        default Moment momentSimpleToEntity(MomentSimpleDto d) {
            if (d == null) return null;
            var m = new Moment();
            m.setId(d.getId()); m.setName(d.getName());
            return m;
        }

        // Meet
        default MeetSimpleDto meetToSimple(Meet m) {
            if (m == null) return null;
            return MeetSimpleDto.builder().id(m.getId()).title(m.getTitle()).build();
        }
        default Meet meetSimpleToEntity(MeetSimpleDto d) {
            if (d == null) return null;
            var m = new Meet();
            m.setId(d.getId()); m.setTitle(d.getTitle());
            return m;
        }
    }
}