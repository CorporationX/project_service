package faang.school.projectservice.dto.resource;

public record CreateResourceDto(
        String originalFilename,
        String uploadedKey,
        long size,
        String contentType,
        Long teamMemberId,
        Long projectId
) {
}
