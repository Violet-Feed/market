package violet.market.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartGroup {
    private Long cartGroupId;
    private Long shopId;
    private Long userId;
}
