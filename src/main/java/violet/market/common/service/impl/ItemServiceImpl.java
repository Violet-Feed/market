package violet.market.common.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.hubspot.jackson.datatype.protobuf.ProtobufModule;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import violet.market.common.mapper.ItemMapper;
import violet.market.common.mapper.ShopMapper;
import violet.market.common.mapper.SkuMapper;
import violet.market.common.pojo.Item;
import violet.market.common.pojo.ItemProperty;
import violet.market.common.pojo.Shop;
import violet.market.common.pojo.Sku;
import violet.market.common.proto_gen.common.BaseResp;
import violet.market.common.proto_gen.common.StatusCode;
import violet.market.common.proto_gen.market.*;
import violet.market.common.proto_gen.recommend.*;
import violet.market.common.service.ItemService;
import violet.market.common.utils.SnowFlake;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    private final SnowFlake itemIdGenerator = new SnowFlake(0, 0);
    private final SnowFlake skuIdGenerator = new SnowFlake(0, 0);
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @GrpcClient("recommend")
    private RecommendServiceGrpc.RecommendServiceBlockingStub recommendStub;


    @Override
    public CreateItemResponse createItem(CreateItemRequest req) throws Exception {
        //TODO:事务
        CreateItemResponse.Builder resp = CreateItemResponse.newBuilder();
        Long itemId = itemIdGenerator.nextId();
        String images = JSONObject.toJSONString(req.getImagesList());
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
        JSONObject extra = new JSONObject();
        extra.put("item_id", itemId);
        extra.put("title", req.getTitle());
        extra.put("image", req.getImagesList().get(0));
        EmbeddingReport embeddingReport = EmbeddingReport.newBuilder()
                .setOptType(1)
                .addTexts(req.getTitle())
                .addImages(req.getImagesList().get(0))
                .setExtra(extra.toJSONString())
                .build();
        ReportMessage reportMessage = ReportMessage.newBuilder()
                .setReportType(ReportType.Embedding_VALUE)
                .setNamespace("item")
                .setEmbeddingReport(embeddingReport)
                .build();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ProtobufModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        String reportJson = objectMapper.writeValueAsString(reportMessage);
        rocketMQTemplate.convertAndSend("recommend", reportJson);
        BaseResp baseResp = BaseResp.newBuilder().setStatusCode(StatusCode.Success).build();
        return resp.setBaseResp(baseResp).build();
    }

    @Override
    public GetItemInfoResponse getItemInfo(GetItemInfoRequest req) throws Exception {
        GetItemInfoResponse.Builder resp = GetItemInfoResponse.newBuilder();
        List<Item> items = itemMapper.selectByItemIds(Collections.singletonList(req.getItemId()));
        if (items.isEmpty()) {
            BaseResp baseResp = BaseResp.newBuilder().setStatusCode(StatusCode.Not_Found_Error).build();
            return resp.setBaseResp(baseResp).build();
        }
        Item item = items.get(0);
        ItemProperty itemProperty = mongoTemplate.findById(item.getItemId(), ItemProperty.class);
        List<Sku> skus = skuMapper.selectSkuByItemId(item.getItemId());
        Shop shop = shopMapper.selectByShopId(item.getShopId());

        ItemInfo itemInfo = ItemInfo.newBuilder()
                .setItemId(item.getItemId())
                .setTitle(item.getTitle())
                .setDescription(item.getDescription())
                .setLocation(item.getLocation())
                .setCreateTime(item.getCreateTime().getTime())
                .setShopId(item.getShopId())
                .build();
        for (ItemProperty.Property property : itemProperty.getProperties()) {
            ItemPropertyInfo.Builder propertyInfo = ItemPropertyInfo.newBuilder()
                    .setPropertyId(property.getPropertyId())
                    .setName(property.getName());
            for (ItemProperty.Value value : property.getValues()) {
                ItemPropertyValue propertyValue = ItemPropertyValue.newBuilder()
                        .setValueId(value.getValueId())
                        .setName(value.getName())
                        .build();
                propertyInfo.addValues(propertyValue);
            }
            resp.addProperties(propertyInfo.build());
        }
        for (Sku sku : skus) {
            SkuInfo skuInfo = SkuInfo.newBuilder()
                    .setSkuId(sku.getSkuId())
                    .setPropertyPath(sku.getPropertyPath())
                    .setImage(sku.getImage())
                    .setPrice(sku.getPrice())
                    .setStock(sku.getStock())
                    .setItemId(sku.getItemId())
                    .build();
            resp.addSkus(skuInfo);
        }
        ShopInfo shopInfo = ShopInfo.newBuilder()
                .setShopId(shop.getShopId())
                .setName(shop.getName())
                .setAvatar(shop.getAvatar())
                .setUserId(shop.getUserId())
                .build();
        BaseResp baseResp = BaseResp.newBuilder().setStatusCode(StatusCode.Success).build();
        return resp.setItemInfo(itemInfo).setShop(shopInfo).setBaseResp(baseResp).build();
    }

    @Override
    public GetItemListBySearchResponse getItemListBySearch(GetItemListBySearchRequest req) throws Exception {
        GetItemListBySearchResponse.Builder resp = GetItemListBySearchResponse.newBuilder();
        SearchRequest searchRequest = SearchRequest.newBuilder()
                .setNamespace("item")
                .setKeyword(req.getKeyword())
                .setPage(req.getPage())
                .build();
        SearchResponse searchResponse = recommendStub.search(searchRequest);
        if (searchResponse.getBaseResp().getStatusCode() != StatusCode.Success) {
            log.error("[getItemListBySearch] Search rpc err, err = {}", searchResponse.getBaseResp());
        }
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ProtobufModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        List<ItemCard> itemCardList = objectMapper.readValue(searchResponse.getResults(), new TypeReference<List<ItemCard>>() {});
        BaseResp baseResp = BaseResp.newBuilder().setStatusCode(StatusCode.Success).build();
        return resp.addAllItems(itemCardList).setBaseResp(baseResp).build();
    }

    @Override
    public GetItemListByUserResponse getItemListByUser(GetItemListByUserRequest req) throws Exception {
        GetItemListByUserResponse.Builder resp = GetItemListByUserResponse.newBuilder();
        RecommendRequest recommendRequest = RecommendRequest.newBuilder()
                .setNamespace("item")
                .setUserId(req.getUserId())
                .build();
        RecommendResponse recommendResponse = recommendStub.recommend(recommendRequest);
        if (recommendResponse.getBaseResp().getStatusCode() != StatusCode.Success) {
            log.error("[getItemListByUser] Recommend rpc err, err = {}", recommendResponse.getBaseResp());
        }
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new ProtobufModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        List<ItemCard> itemCardList = objectMapper.readValue(recommendResponse.getResults(), new TypeReference<List<ItemCard>>() {});
        BaseResp baseResp = BaseResp.newBuilder().setStatusCode(StatusCode.Success).build();
        return resp.addAllItems(itemCardList).setBaseResp(baseResp).build();
    }
}
