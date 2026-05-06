package epasal.com.productservice.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dnrzrlhn7",
                "api_key", "761557239227233",
                "api_secret", "6fmvwEHx5PMtF_APHfLcoS79zjc",
                "secure", true
        ));
    }
}
