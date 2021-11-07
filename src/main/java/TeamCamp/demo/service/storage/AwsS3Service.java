package TeamCamp.demo.service.storage;

import TeamCamp.demo.common.s3.AwsProperties;
import TeamCamp.demo.common.s3.FileService;
import TeamCamp.demo.exception.product.ImageRoadFailedException;
import TeamCamp.demo.service.storage.StorageService;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service implements StorageService {

    private final AwsProperties awsProperties;

    private AmazonS3 s3Client;


    @PostConstruct
    private void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(awsProperties.getAccessKey(),
                awsProperties.getSecretKey());

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(awsProperties.getRegionStatic())
                .build();
    }

    public String uploadProductImage(MultipartFile file) {
        return upload(file, awsProperties.getProductBucket());
    }

    public String upload(MultipartFile file, String bucket) {
        String fileName = file.getOriginalFilename();
        String convertedFileName = FileService.fileNameConvert(fileName);

        try {
            String mimeType = new Tika().detect(file.getInputStream());
            ObjectMetadata metadata = new ObjectMetadata();

            FileService.checkImageMimeType(mimeType);
            metadata.setContentType(mimeType);
            s3Client.putObject(
                    new PutObjectRequest(bucket, convertedFileName, file.getInputStream(), metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException exception) {
            throw new ImageRoadFailedException();
        }

        return s3Client.getUrl(bucket, convertedFileName).toString();
    }


    public void deleteProductImage(String key) {
        delete(awsProperties.getProductBucket(), key);
    }
    @Override
    public void delete(String bucket, String key) {
        s3Client.deleteObject(bucket, key);
    }
}
