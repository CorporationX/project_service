package faang.school.projectservice.service.internship;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
class InternshipData {
    @NonNull
    private Project project;
    @NonNull
    private TeamMember mentor;
    @NonNull
    private List<TeamMember> interns;
}