package violet.market.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shop {
    private Long id;
    private Long shopId;
    private String name;
    private String avatar;
    private Long userId;
}
