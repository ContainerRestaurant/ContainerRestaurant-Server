package library.aws.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Disabled
public class AwsSdkTest {

    @Test
    void test() {
        // 자격증명 객체 (기본적으로 환경 변수 사용이 권장되는듯 하지만 스프링 프로필에서 직접 받아오자
        BasicAWSCredentials credentials = new BasicAWSCredentials("AKIAX6QSQLAVESGLXD47", "Nvji0XrgbzXqmZdH4y4UHEQ7aoHt3qWtl6fSWjLw");

        // 서비스 클라이언트 생성
        AmazonS3 awsS3 = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();

        for (Bucket bucket : awsS3.listBuckets()) {
            System.out.println("===============================================");
            System.out.println("bucket.getName() = " + bucket.getName());
        }

        // 읽기
        S3Object object = awsS3.getObject("imaget-test-demo", "test.txt");
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(object.getObjectContent()))) {
            StringBuilder sb = new StringBuilder("CONTENT:");
            bufferedReader.lines()
                    .forEach(l -> sb.append(l).append('\n'));
            System.out.println(sb.append(":END CONTENT"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 쓰기 / 수정
        InputStream input = getInputStream("test!!!123123");
        Assertions.assertThat(input).isNotNull();

        final PutObjectResult result = awsS3.putObject("imaget-test-demo", "testUpload", input, new ObjectMetadata());
        System.out.println("RESULT:\n" + result.getMetadata());

        // 삭제
        awsS3.deleteObject("imaget-test-demo", "testUpload");
    }

    private InputStream getInputStream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

}
