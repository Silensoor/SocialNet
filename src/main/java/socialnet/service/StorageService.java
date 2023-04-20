package socialnet.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import socialnet.api.response.CommonRs;
import socialnet.model.Person;
import socialnet.model.Storage;
import socialnet.repository.StorageRepository;
import socialnet.service.amazon.AmazonService;
import socialnet.service.users.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.http.entity.ContentType.*;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final UserService userService;
    private final StorageRepository storageRepository;
    private final AmazonService amazonService;

    public CommonRs photoUpload(String fileType, MultipartFile file) throws IOException {
        if (file.getSize() == 0) return null;
        if (!isImage(file)) return null;

        UUID uuid = UUID.randomUUID();
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        Person person = userService.getAuthPerson();

        String virtualPath = String.format("%S/%S", "users_photo", uuid);

        Storage storage = new Storage(
                person.getId(),
                fileName,
                file.getSize(),
                "IMAGE",
                LocalDateTime.now()
        );

        storageRepository.insertStorage(storage);

        //Загрузка фотографии в облако
        uploadFile(file);

        return new CommonRs<>(storage);
        //return WrapperMapper.wrap(StorageMapper.mapStorageToStorageDto(storage), true);
    }

    private boolean isImage(MultipartFile file) {
        return Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_BMP.getMimeType(),
                IMAGE_GIF.getMimeType(),
                IMAGE_JPEG.getMimeType()).contains(file.getContentType());
    }

    private void uploadFile(MultipartFile file) throws IOException {
        Map<String, String> metadata = new HashMap<>();

        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setUserMetadata(metadata);

        String fileName = file.getOriginalFilename();
        amazonService.upload(fileName, objectMetadata, file.getInputStream());
    }



}
