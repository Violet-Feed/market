package violet.market.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sku {
    private Long id;
    private Long skuId;
    private String propertyPath;
    private String image;
    private Long price;
    private Long stock;
    private Long itemId;
}
