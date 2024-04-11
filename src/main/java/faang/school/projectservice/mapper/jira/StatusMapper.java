package faang.school.projectservice.mapper.jira;

import com.atlassian.jira.rest.client.api.domain.Status;
import faang.school.projectservice.dto.jira.StatusDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatusMapper {

    StatusDto toDto(Status status);

    Status toEntity(StatusDto statusDto);
}
