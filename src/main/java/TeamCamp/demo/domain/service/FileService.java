package TeamCamp.demo.domain.service;

import TeamCamp.demo.exception.IllegalMineTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class FileService {

    public void checkImageMimeType(String mimeType) {
        if (!(mimeType.equals("image/jpg") || mimeType.equals("image/jpeg")
                || mimeType.equals("image/png") || mimeType.equals("image/gif"))) {
            throw new IllegalMineTypeException();
        }
    }

    public String addDirToSave(String fileName, String dir) {
        StringBuilder builder = new StringBuilder();
        builder.append(dir).append("/").append(fileName);
        return builder.toString();
    }
    public String fileNameConvert(String fileName) {
        StringBuilder builder = new StringBuilder();
        UUID uuid = UUID.randomUUID();
        String extension = getExtension(fileName);
        builder.append(uuid).append(".").append(extension);
        return builder.toString();
    }
    private String getExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return fileName.substring(pos + 1);
    }
}

