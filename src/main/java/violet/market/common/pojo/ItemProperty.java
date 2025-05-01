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
    private Long itemId;
    private List<Property> properties;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Property {
        private Long propertyId;
        private String name;
        private List<Value> values;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Value {
        private Long valueId;
        private String name;
    }
}
