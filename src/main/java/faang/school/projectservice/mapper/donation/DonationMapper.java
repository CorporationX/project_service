package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DonationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentNumber", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "campaign", ignore = true)
    Donation dtoToEntity(DonationDto dto);

    @Mapping(target = "campaignId", source = "campaign", qualifiedByName = "mapCampaignToCampaignId")
    DonationDto entityToDto(Donation entity);

    List<DonationDto> entityListToDtoList(List<Donation> entityList);

    @Named("mapCampaignToCampaignId")
    default Long mapCampaignToCampaignId(Campaign campaign) {
        return campaign != null ? campaign.getId() : null;
    }
}
