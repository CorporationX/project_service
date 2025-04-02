package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.donation.DonationCreateRequest;
import faang.school.projectservice.dto.donation.DonationResponse;
import faang.school.projectservice.mapper.campaign.CampaignMapper;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CampaignMapper.class)
public interface DonationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentNumber", ignore = true)
    @Mapping(target = "donationTime", ignore = true)
    @Mapping(target = "campaign", ignore = true)
    Donation toEntity(DonationCreateRequest donationDto);

    DonationResponse toResponse(Donation donation);
}
