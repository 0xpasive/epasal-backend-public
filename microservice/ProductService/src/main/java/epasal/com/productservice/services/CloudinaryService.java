package epasal.com.productservice.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Data
@Slf4j
public class CloudinaryService {

    private static final String IMAGE_FOLDER = "epasal_products";
    private static final String RESOURCE_TYPE = "image";

    private final Cloudinary cloudinary;


    public Map<String, Object> uploadImage(MultipartFile file, String name) throws Exception {
        log.debug("Uploading image with public_id: {}", name);

        Map<String, Object> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", IMAGE_FOLDER,
                        "resource_type", RESOURCE_TYPE,
                        "public_id", name
                )
        );

        log.debug("Image uploaded successfully with public_id: {}", name);
        return result;
    }

    public void deleteImage(String publicId) throws Exception {
        log.debug("Deleting image with public_id: {}", publicId);
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("invalidate", true));
        log.debug("Image deleted successfully with public_id: {}", publicId);
    }


}
