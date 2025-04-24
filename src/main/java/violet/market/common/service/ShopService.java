package violet.market.common.service;

import violet.market.common.proto_gen.market.CreateShopRequest;
import violet.market.common.proto_gen.market.CreateShopResponse;

public interface ShopService {
    CreateShopResponse createShop(CreateShopRequest req) throws Exception;
//    JSONObject updateShop(JSONObject req) throws Exception;
//    JSONObject deleteShop(JSONObject req) throws Exception;
//    JSONObject getShopById(JSONObject req) throws Exception;
//    JSONObject getShopListBySearch(JSONObject req) throws Exception;
}
