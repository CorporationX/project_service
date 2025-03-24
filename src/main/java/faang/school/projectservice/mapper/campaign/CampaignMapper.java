package faang.school.projectservice.mapper.campaign;

import faang.school.projectservice.dto.client.Campaign.CampaignDto;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import faang.school.projectservice.model.Project;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CampaignMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "status", target = "status")
    CampaignDto toDto(Campaign campaign);

    @Mapping(source = "campaignDto.id", target = "id")
    @Mapping(source = "campaignDto.description", target = "description")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "campaignDto.createdAt", target = "createdAt")
    @Mapping(source = "campaignDto.updatedAt", target = "updatedAt")
    @Mapping(source = "project", target = "project")
    Campaign toEntity(CampaignDto campaignDto, Project project, CampaignStatus status);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "amountRaised", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "project", source = "project")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedBy", source = "updaterId")
    @Mapping(target = "status" ,source = "status")
    @Mapping(target = "description",source = "campaignDto.description")
    void updateEntity(@MappingTarget Campaign campaign, CampaignDto campaignDto, Project project, long updaterId, CampaignStatus status);
}