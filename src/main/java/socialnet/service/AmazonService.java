package socialnet.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class AmazonService {
    @Value("${s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;

    public void upload(String fileName,
                          ObjectMetadata objectMetadata,
                          InputStream inputStream) {
        try {
            amazonS3.putObject(bucket, fileName, inputStream, objectMetadata);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to upload the file", e);
        }
    }

    public InputStream downloadFileByName(String name) {
        try {
            S3Object s3object = amazonS3.getObject(bucket, name);
            return new BufferedInputStream(s3object.getObjectContent());
        } catch (AmazonServiceException e) {
            throw new IllegalStateException("Failed to download the file", e);
        }
    }

    public void delete(String name) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, name));
    }



}
