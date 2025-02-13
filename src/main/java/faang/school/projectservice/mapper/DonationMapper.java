package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.DonationDto;
import faang.school.projectservice.model.Donation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DonationMapper {
    DonationMapper INSTANCE = Mappers.getMapper(DonationMapper.class);

    DonationDto toDto(Donation donation);

    Donation toEntity(DonationDto donationDto);
}
