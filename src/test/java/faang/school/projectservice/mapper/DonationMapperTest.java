package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.donation.DonationDto;
import faang.school.projectservice.model.Donation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DonationMapperTest {
    private DonationMapper donationMapper;
    private DonationDto donationDto;
    private Donation donation;

    @BeforeEach
    public void setUp() {
        donationMapper = Mappers.getMapper(DonationMapper.class);
        donationDto = DonationDto.builder().build();
        donation = Donation.builder().build();
    }

    @Test
    public void testToEntity() {
        donationDto.setAmount(BigDecimal.valueOf(100));

        Donation donation = donationMapper.toEntity(donationDto);

        assertNotNull(donation);
        assertEquals(donationDto.getAmount(), donation.getAmount());
    }

    @Test
    public void testToDto() {
        donation.setAmount(BigDecimal.valueOf(100));

        DonationDto donationDto = donationMapper.toDto(donation);

        assertNotNull(donationDto);
        assertEquals(donation.getAmount(), donationDto.getAmount());
    }

    @Test
    public void testUpdateDonationFromDto() {
        Donation donation = new Donation();
        donation.setAmount(BigDecimal.valueOf(100));

        DonationDto donationDto = new DonationDto();
        donationDto.setAmount(BigDecimal.valueOf(200));

        donationMapper.updateDonationFromDto(donationDto, donation);

        assertEquals(donationDto.getAmount(), donation.getAmount());
    }

    @Test
    public void testToDtoList() {
        Donation donation = new Donation();
        donation.setAmount(BigDecimal.valueOf(100));

        List<Donation> donations = Collections.singletonList(donation);

        List<DonationDto> donationDtos = donationMapper.toDtoList(donations);

        assertNotNull(donationDtos);
        assertEquals(donations.size(), donationDtos.size());
        assertEquals(donation.getAmount(), donationDtos.get(0).getAmount());
    }
}