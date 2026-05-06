package epasal.com.productservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "brands")
@Data
public class Brand {
    @Id
    private String name;
}
