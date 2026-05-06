package epasal.com.inventoryservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse {
    List<InventoryResponse> content;
    int page;
    int size;
    long totalElements;
    int totalPages;
    boolean last;
}
