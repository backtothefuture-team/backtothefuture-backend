package com.backtothefuture.domain.common.util;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class S3Util {

    @Value("${s3.bucket.image}")
    private String imageBucketName;

    @Value("${cloud.aws.s3.region}")
    String regionString;

    private final S3Client s3Client;

    public String uploadImageToS3(String bucketName, String key, MultipartFile multipartFile) throws IOException {
        // put request setting
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        // MultipartFile to File
        File tempFile = File.createTempFile("upload_", multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);

        // upload
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile));
        tempFile.delete();

        // return url
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, regionString, key);
    }

    /**
     * Product thumbnail
     * <storeId>/<productId>/<timestamp>
     *
     * @param storeId   가게아이디
     * @param productId 상품아이디
     * @param file      업로드 할 이미지 파일
     * @return 업로드 한 s3 이미지 url
     * @throws IOException
     */
    public String uploadProductThumbnail(String storeId, String productId, MultipartFile file) throws IOException {
        // 경로 및 파일명 설정
        StringBuilder sb = new StringBuilder();
        String key = sb.append(String.join("/", List.of(storeId, productId, getCurrentTimestamp())))
                .append(".")
                .append(getImageExtension(file)) // 확장자 설정
                .toString();

        // 업로드
        return uploadImageToS3(imageBucketName, key, file);
    }

    /**
     * Store thumbnail
     * <storeId>/<timestamp>
     *
     * @param storeId 가게아이디
     * @param file    업로드 할 이미지 파일
     * @return 업로드 한 s3 이미지 url
     * @throws IOException
     */
    public String uploadStoreThumbnail(String storeId, MultipartFile file) throws IOException {
        StringBuilder sb = new StringBuilder();
        String key = sb.append(String.join("/", List.of(storeId, getCurrentTimestamp())))
                .append(".")
                .append(getImageExtension(file)) // 확장자 설정
                .toString();
        // 업로드
        return uploadImageToS3(imageBucketName, key, file);
    }

    public String getCurrentTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        return dateFormat.format(timestamp);
    }

    public String getImageExtension(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        String extension = "";
        //파일의 Content Type 이 있을 경우 Content Type 기준으로 확장자 확인
        if (StringUtils.hasText(contentType)) {
            switch (contentType) {
                case "image/jpeg" -> extension = "jpg";
                case "image/png" -> extension = "png";
                case "image/gif" -> extension = "gif";
            }
        } else {
            throw new IllegalArgumentException();
        }
        return extension;
    }
}
