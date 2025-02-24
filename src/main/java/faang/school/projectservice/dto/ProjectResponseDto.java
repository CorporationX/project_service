package faang.school.projectservice.dto;

public record ProjectResponseDto(
        Long id,
        Long ownerId,
        String name,
        String description
)
{}
