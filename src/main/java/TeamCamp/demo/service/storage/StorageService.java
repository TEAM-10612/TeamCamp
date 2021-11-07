package TeamCamp.demo.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String upload(MultipartFile multipartFile ,String bucket);
    void delete(String bucket, String key);
}
