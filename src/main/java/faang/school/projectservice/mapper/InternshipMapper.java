package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@Mapper (componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(ignore = true, target ="interns")
    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "mentorId", target = "mentorId.id")
    @Mapping(source = "scheduleId",target = "schedule.id")
    Internship toInternship(InternshipDto internshipDto);

    @Mapping(target = "internsId", source = "interns", qualifiedByName = "toInternsIdDto")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "mentorId", source = "mentorId.id")
    @Mapping(target = "scheduleId", source = "schedule.id")
    InternshipDto toInternshipDto(Internship internship);


  @Named("toInternsIdDto")
    default List<Long> toInternsIdDto(List<TeamMember> interns){
        return interns.stream().map(TeamMember::getId).toList();
    }
}

