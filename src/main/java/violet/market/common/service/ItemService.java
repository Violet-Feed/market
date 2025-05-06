package violet.market.common.service;

import violet.market.common.proto_gen.market.*;

public interface ItemService {
    CreateItemResponse createItem(CreateItemRequest req) throws Exception;

    //    JSONObject updateItem(JSONObject req) throws Exception;
//    JSONObject deleteItem(JSONObject req) throws Exception;
    GetItemInfoResponse getItemInfo(GetItemInfoRequest req) throws Exception;

    GetItemListBySearchResponse getItemListBySearch(GetItemListBySearchRequest req) throws Exception;

    GetItemListByUserResponse getItemListByUser(GetItemListByUserRequest req) throws Exception;
//    JSONObject getItemListByShop(JSONObject req) throws Exception;
    AppendItemHistoryResponse appendItemHistory(AppendItemHistoryRequest req) throws Exception;
}
