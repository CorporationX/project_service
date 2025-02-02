package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.config.CommonMapperConfig;
import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(config = CommonMapperConfig.class)
public interface InternshipMapper {


    @Mapping(target = "mentorId", source = "mentor.id")
    @Mapping(target = "projectId", source = "mentor.id")
    @Mapping(target = "internsId", source = "interns", qualifiedByName = "internsIds")
    InternshipCreateDto toDto(Internship internship);


    @Mapping(target = "mentor", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "interns", ignore = true)
    Internship toEntity(InternshipCreateDto internshipCreateDto);

    @Mapping(target = "id", ignore = true)
    void update(InternshipUpdateDto dto, @MappingTarget Internship entity );

    @Named("internsIds")
    default List<Long> internsIds(List<TeamMember> members){
        return members.stream().map(m -> m.getId()).collect(Collectors.toList());
    }
}
