package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DonationMapper {

    @Mapping(target = "campaign.id", source = "campaignId")
    Donation toEntity(DonationDto dto);

    @Mapping(target = "campaignId", source = "campaign.id")
    DonationDto toDto(Donation donation);

    List<DonationDto> toDto(List<Donation> donations);
}
