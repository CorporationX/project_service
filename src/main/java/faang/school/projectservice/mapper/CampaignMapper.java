package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.campaign.CampaignDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import static faang.school.projectservice.service.CampaignService.ID_NULL_EXCEPTION;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    @Mapping(target = "project", source = "projectId")
    @Mapping(target = "isDeleted", ignore = true)
    Campaign toCampaign(CampaignDto campaignDto);

    @Mapping(target = "projectId", source = "project")
    CampaignDto toCampaignDto(Campaign campaign);

    List<Campaign> toCampaignList(List<CampaignDto> campaignDtoList);

    List<CampaignDto> toCampaignDtoList(List<Campaign> campaignList);

    default Project mapIdToProject(Long id) {
        if (id == null) {
            throw new DataValidationException(ID_NULL_EXCEPTION);
        }
        Project project = new Project();
        project.setId(id);
        return project;
    }

    default Long mapProjectToId(Project project) {
        return project != null ? project.getId() : null;
    }
}
