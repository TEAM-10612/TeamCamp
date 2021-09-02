package TeamCamp.demo.domain.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Service {

    private final FileService fileService;
    private AmazonS3 s3Client;
    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;


//
//    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
//        File uploadFile = convert(multipartFile)  // 파일 변환할 수 없으면 에러
//                .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
//
//        return upload(uploadFile, dirName);
//    }

    @PostConstruct
    private void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    public String upload(MultipartFile file, String dir) {
        String fileName = file.getOriginalFilename();
        String convertedFileName = fileService.fileNameConvert(fileName);
        String fileNameWithPath = fileService.addDirToSave(convertedFileName, dir);

        try {
            String mimeType = new Tika().detect(file.getInputStream());
            ObjectMetadata metadata = new ObjectMetadata();

            fileService.checkImageMimeType(mimeType);
            metadata.setContentType(mimeType);
            s3Client.putObject(
            new PutObjectRequest(bucket, fileNameWithPath, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return s3Client.getUrl(bucket, fileNameWithPath).toString();
    }
}
