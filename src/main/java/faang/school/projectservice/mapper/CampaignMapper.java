package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.campaign.CampaignCreateDto;
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
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Campaign toCampaign(CampaignCreateDto campaignCreateDto);

    @Mapping(target = "projectId", source = "project.id")
    CampaignCreateDto toCampaignDto(Campaign campaign);

    List<Campaign> toCampaignList(List<CampaignCreateDto> campaignCreateDtoList);

    List<CampaignCreateDto> toCampaignDtoList(List<Campaign> campaignList);

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
