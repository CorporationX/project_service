package faang.school.projectservice.util;

import faang.school.projectservice.model.Project;

import java.math.BigInteger;

public class ProjectMock {

    public static BigInteger twoGB = BigInteger.valueOf(2L).multiply(BigInteger.valueOf(1L << 30));

    public static Project generateProject(long id, BigInteger storageSize, BigInteger maxStorageSize) {
        Project project = new Project();

        project.setId(id);
        project.setStorageSize(storageSize);
        project.setMaxStorageSize(maxStorageSize);

        return project;
    };

    public static Project generateProject(long id) {
        return ProjectMock.generateProject(id, BigInteger.ZERO, twoGB);
    };

    public static Project generateProject(long id, BigInteger storageSize) {
        return ProjectMock.generateProject(id, storageSize, twoGB);
    };
}
