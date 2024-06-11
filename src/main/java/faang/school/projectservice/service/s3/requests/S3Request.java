package faang.school.projectservice.service.s3.requests;

import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import faang.school.projectservice.dto.resource.MultipartFileResourceDto;

public interface S3Request {
    PutObjectRequest putRequest(MultipartFileResourceDto multipartFileResource);
    
    String createKey(MultipartFileResourceDto multipartFileResource);
    
    DeleteObjectRequest deleteRequest(String key);
    
    default String getDefaultFolderDelimiter() {
        return "_";
    }
    
    default ObjectMetadata createObjectMetadata(MultipartFileResourceDto multipartFileResource) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFileResource.getSize());
        objectMetadata.setContentType(multipartFileResource.getContentType());
        return objectMetadata;
    }
}
