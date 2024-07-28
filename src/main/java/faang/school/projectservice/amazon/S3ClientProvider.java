package faang.school.projectservice.amazon;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3ClientProvider {
    private static final String REGION = "us-west-2";

    public static AmazonS3 getS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(REGION)
                .withCredentials(new ProfileCredentialsProvider())
                .build();
    }
}
