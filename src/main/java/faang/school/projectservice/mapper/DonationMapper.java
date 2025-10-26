package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.donation.CreateDonationDto;
import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DonationMapper {

    @Mapping(source = "paymentCurrency", target = "currency")
    @Mapping(target = "campaign", ignore = true)
    Donation toDonation(CreateDonationDto createDonationDto);

    @Mapping(source = "campaign.id", target = "campaignId")
    DonationDto toDonationDto(Donation donation);
}