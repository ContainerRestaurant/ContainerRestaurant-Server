package container.restaurant.server.domain.feed.picture;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import container.restaurant.server.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AwsS3ImageService implements ImageService {

    @Value("${aws.key.access}")
    private String accessKey;

    @Value("${aws.key.secret}")
    private String secretKey;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    private AmazonS3 awsS3;

    private final ImageRepository imageRepository;

    private void initClient() {
        if (awsS3 != null) return;

        if (accessKey == null) accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        if (secretKey == null) secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        if (bucketName == null) bucketName = System.getenv("AWS_S3_BUCKET_NAME");

        if (accessKey == null || secretKey == null || bucketName == null)
            throw new IllegalStateException("AWS 연결에 실패했습니다.");

        final BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        awsS3 = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    @Override
    public Image upload(MultipartFile imageFile) {
        initClient();

        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());

        String key = newImageUuid();
        InputStream in = getInputStream(imageFile);

        awsS3.putObject(bucketName, key, in, metadata);

        return imageRepository.save(Image.from(key));
    }

    @NotNull
    private InputStream getInputStream(MultipartFile imageFile) {
        InputStream in;

        try {
            in = imageFile.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException("요청한 파일을 읽을 수 없습니다.");
        }
        return in;
    }

    @NotNull
    private String newImageUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Override
    public Image findById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 이미지(id: " + id + ")"));
    }

    @Override
    public Boolean deleteById(Long id) {
        initClient();

        final Image image = imageRepository.findById(id).orElse(null);
        if (image == null) return false;

        imageRepository.deleteById(id);
        awsS3.deleteObject(bucketName, image.getKey());
        return true;
    }

    @Override
    public ImageFileDto getImage(String key) {
        initClient();
        final S3Object object = awsS3.getObject(bucketName, key);

        return ImageFileDto.from(object.getObjectContent(),
                object.getObjectMetadata().getContentType());
    }

}
