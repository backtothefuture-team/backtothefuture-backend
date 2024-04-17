package com.backtothefuture.domain.common.util.s3;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
@RequiredArgsConstructor
public class S3AsyncUtil {

    @Value("${s3.bucket.image}")
    private String imageBucketName;

    @Value("${cloud.aws.s3.region}")
    String regionString;

    private final S3Client s3Client;

    /**
     * 업데이트 시 과거 파일을 삭제한다. 파일명이 timestamp 로 되어 있기 때문에, 정렬 후 최근 이미지 제외 모두 삭제한다.
     */
    @Async
    public void deletePastImages(String dirPath) {
        ArrayDeque<String> list = new ArrayDeque<>(getFileList(dirPath));
        list.pollFirst(); // 가장 최근 파일은 삭제하지 않는다.

        list.forEach(key -> {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(imageBucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        });
    }

    public List<String> getFileList(String dirPath) {
        // dirPath ~ 로 시작하는 key를 가진 목록 불러오기
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(imageBucketName)
                .prefix(dirPath)
                .build();

        ListObjectsResponse response = s3Client.listObjects(listObjectsRequest);

        // 더 깊은 depth의 파일이 삭제되는것을 막기 위해 depth를 센다.
        int countSlash = (int) dirPath.chars()
                .filter(ch -> ch == '/')
                .count();

        List<String> objects = response.contents().stream()
                .map(S3Object::key) // key만 추출
                .filter(key -> !key.endsWith("/")) // 파일만 필터링. 폴더는 끝이 '/'로 끝남
                .filter(key -> {
                    // 더 깊은 depth에 있는 파일은 제외한다.
                    int countKeySlash = (int) key.chars()
                            .filter(ch -> ch == '/')
                            .count();
                    return countSlash == countKeySlash;
                })
                .sorted(Comparator.reverseOrder()) // 내림차순 정렬
                .toList();
        
        return objects;
    }
}
