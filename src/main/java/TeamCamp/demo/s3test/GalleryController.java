package TeamCamp.demo.s3test;

import TeamCamp.demo.common.s3.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@RequiredArgsConstructor
@RestController
public class GalleryController {
    private final AwsS3Service s3Uploader;

    @PostMapping("/images")
    public String upload(@RequestParam("images") MultipartFile multipartFile) throws IOException {
        s3Uploader.upload(multipartFile, "cusproduct/product");
        return "test";
    }

    @DeleteMapping("/images/{key}")
    public String delete(String bucket,@PathVariable String key) throws IOException {
        s3Uploader.delete(bucket,key);
        return "test";
    }
}