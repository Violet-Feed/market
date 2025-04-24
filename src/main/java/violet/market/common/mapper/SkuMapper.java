package violet.market.common.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import violet.market.common.pojo.Sku;

import java.util.List;

@Mapper
public interface SkuMapper {
    @Insert("insert into sku values (null,#{skuId},#{propertyPath},#{image},#{price},#{stock},#{itemId})")
    boolean createSku(Sku sku);

    @Insert("<script>" +
            "insert into sku values " +
            "<foreach item='item' index='index' collection='skus' open='' separator=',' close=''>" +
            "(null,#{item.skuId},#{item.propertyPath},#{item.image},#{item.price},#{item.stock},#{item.itemId})" +
            "</foreach>" +
            "</script>")
    int batchCreateSku(List<Sku> skus);
}
