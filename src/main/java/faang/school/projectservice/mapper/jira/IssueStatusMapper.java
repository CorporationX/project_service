package faang.school.projectservice.mapper.jira;

import faang.school.projectservice.dto.jira.IssueStatusTransition;
import faang.school.projectservice.dto.jira.IssueStatusUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface IssueStatusMapper {

    @Mapping(target = "transition", source = "status", qualifiedByName = "toTransition")
    IssueStatusTransition toTransition(IssueStatusUpdateDto issueStatusUpdateDto);

    @Named("toTransition")
    default IssueStatusTransition.Transition toTransitionId(String status) {
        if (status.equalsIgnoreCase("to do")) {
            return new IssueStatusTransition.Transition("11");
        } else if (status.equalsIgnoreCase("in progress")) {
            return new IssueStatusTransition.Transition("21");
        } else if (status.equalsIgnoreCase("done")) {
            return new IssueStatusTransition.Transition("31");
        }
        return null;
    }
}
