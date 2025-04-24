package violet.market.common.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import violet.market.common.pojo.Shop;

@Mapper
public interface ShopMapper {
    @Insert("insert into shop values (null,#{shopId},#{name},#{avatar},#{userId})")
    boolean createShop(Shop shop);
}
