package violet.market.common.mapper;

import org.apache.ibatis.annotations.*;
import violet.market.common.pojo.Item;

import java.util.List;

@Mapper
public interface ItemMapper {
    @Insert("insert into item values (null,#{itemId},#{title},#{description},#{images},#{location},#{createTime},#{shopId})")
    boolean createItem(Item item);

    @Update("update item set title = #{title}, description = #{description}, images = #{images}, location = #{location} where item_id = #{itemId}")
    boolean updateItem(Item item);

    @Delete("delete from item where item_id = #{itemId}")
    boolean deleteItem(Long itemId);

    @Select("select * from item where item_id in " +
            "<foreach item='item' index='index' collection='itemIds' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>")
    List<Item> selectByItemIds(@Param("itemIds") List<Long> itemIds);

    @Select("select * from item where shop_id = #{shopId} order by create_time desc limit #{offset}, #{limit}")
    List<Item> selectByShopId(@Param("shopId") Long shopId, @Param("offset") int offset, @Param("limit") int limit);
}
