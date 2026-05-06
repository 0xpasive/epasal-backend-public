package epasal.com.productservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse {
    List<ProductResponse> content;
    int page;
    int size;
    long totalElements;
    int totalPages;
    boolean last;
}
