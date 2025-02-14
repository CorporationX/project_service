package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Named;

import java.util.List;

public interface InternshipMapper {
    @Named("mapToIds")
    default List<Long> mapToIds(List<TeamMember> interns) {
        return interns.stream().map(TeamMember::getId).toList();
    }
}
