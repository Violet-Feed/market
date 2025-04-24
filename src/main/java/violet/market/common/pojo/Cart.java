package violet.market.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private Long cartId;
    private Long skuId;
    private Long quantity;
    private Long cartGroupId;
}
