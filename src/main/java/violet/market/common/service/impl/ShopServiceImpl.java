package violet.market.common.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import violet.market.common.mapper.ShopMapper;
import violet.market.common.pojo.Shop;
import violet.market.common.proto_gen.common.BaseResp;
import violet.market.common.proto_gen.common.StatusCode;
import violet.market.common.proto_gen.market.CreateShopRequest;
import violet.market.common.proto_gen.market.CreateShopResponse;
import violet.market.common.service.ShopService;
import violet.market.common.utils.SnowFlake;

@Service
public class ShopServiceImpl implements ShopService {
    private final SnowFlake shopIdGenerator = new SnowFlake(0, 0);
    @Autowired
    private ShopMapper shopMapper;

    @Override
    public CreateShopResponse createShop(CreateShopRequest req) throws Exception {
        CreateShopResponse.Builder resp = CreateShopResponse.newBuilder();
        Shop shop = new Shop(null, shopIdGenerator.nextId(), req.getName(), "", req.getUserId());
        shopMapper.createShop(shop);
        BaseResp baseResp = BaseResp.newBuilder().setStatusCode(StatusCode.Success).build();
        return resp.setBaseResp(baseResp).build();
    }
}
