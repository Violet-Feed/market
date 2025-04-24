package violet.market.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import violet.market.common.mapper.ItemMapper;
import violet.market.common.mapper.SkuMapper;
import violet.market.common.pojo.Item;
import violet.market.common.pojo.ItemProperty;
import violet.market.common.pojo.Sku;
import violet.market.common.proto_gen.common.BaseResp;
import violet.market.common.proto_gen.common.StatusCode;
import violet.market.common.proto_gen.market.*;
import violet.market.common.service.ItemService;
import violet.market.common.utils.SnowFlake;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final SnowFlake itemIdGenerator = new SnowFlake(0, 0);
    private final SnowFlake skuIdGenerator = new SnowFlake(0, 0);
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public CreateItemResponse createItem(CreateItemRequest req) throws Exception {
        CreateItemResponse.Builder resp = CreateItemResponse.newBuilder();
        Long itemId = itemIdGenerator.nextId();
        String images = req.getImagesList().toString();
        Item item = new Item(null, itemId, req.getTitle(), req.getDescription(), images, req.getLocation(), new Date(), req.getShopId());
        itemMapper.createItem(item);
        List<ItemProperty.Property> properties = new ArrayList<>();
        for (ItemPropertyInfo property : req.getPropertiesList()) {
            List<ItemProperty.Value> values = new ArrayList<>();
            for (ItemPropertyValue value : property.getValuesList()) {
                values.add(new ItemProperty.Value(value.getValueId(), value.getName()));
            }
            properties.add(new ItemProperty.Property(property.getPropertyId(), property.getName(), values));
        }
        ItemProperty itemProperty = new ItemProperty(null, itemId, properties);
        mongoTemplate.insert(itemProperty);
        List<Sku> skus = new ArrayList<>();
        for (SkuInfo skuInfo : req.getSkusList()) {
            Long skuId = skuIdGenerator.nextId();
            skus.add(new Sku(null, skuId, skuInfo.getPropertyPath(), skuInfo.getImage(), skuInfo.getSkuId(), skuInfo.getStock(), itemId));
        }
        skuMapper.batchCreateSku(skus);
        //TODO:report
        BaseResp baseResp = BaseResp.newBuilder().setStatusCode(StatusCode.Success).build();
        return resp.setBaseResp(baseResp).build();
    }

    @Override
    public GetItemInfoResponse getItemInfo(GetItemInfoRequest req) throws Exception {
        return null;
    }

    @Override
    public GetItemListBySearchResponse getItemListBySearch(GetItemListBySearchRequest req) throws Exception {
        return null;
    }

    @Override
    public GetItemListByUserResponse getItemListByUser(GetItemListByUserRequest req) throws Exception {
        return null;
    }
}
