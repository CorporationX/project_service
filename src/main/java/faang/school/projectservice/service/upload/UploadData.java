package faang.school.projectservice.service.upload;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;

import java.math.BigInteger;

public record UploadData(
        Project project,
        TeamMember user,
        BigInteger fileSize,
        String folder
)
{}
