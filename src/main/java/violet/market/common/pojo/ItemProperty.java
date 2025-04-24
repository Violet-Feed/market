package violet.market.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemProperty {
    private Long id;
    private Long item_id;
    private List<Property> properties;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Property {
        private Long property_id;
        private String name;
        private List<Value> values;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Value {
        private Long value_id;
        private String name;
    }
}
