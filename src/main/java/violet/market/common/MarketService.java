package violet.market.common;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import violet.market.common.proto_gen.market.*;
import violet.market.common.service.ItemService;
import violet.market.common.service.ShopService;

@GrpcService
public class MarketService extends MarketServiceGrpc.MarketServiceImplBase {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ShopService shopService;

    @Override
    public void createItem(CreateItemRequest request, StreamObserver<CreateItemResponse> responseObserver) {
        try {
            responseObserver.onNext(itemService.createItem(request));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getItemInfo(GetItemInfoRequest request, StreamObserver<GetItemInfoResponse> responseObserver) {
        try {
            responseObserver.onNext(itemService.getItemInfo(request));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getItemListBySearch(GetItemListBySearchRequest request, StreamObserver<GetItemListBySearchResponse> responseObserver) {
        try {
            responseObserver.onNext(itemService.getItemListBySearch(request));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getItemListByUser(GetItemListByUserRequest request, StreamObserver<GetItemListByUserResponse> responseObserver) {
        try {
            responseObserver.onNext(itemService.getItemListByUser(request));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void createShop(CreateShopRequest request, StreamObserver<CreateShopResponse> responseObserver) {
        try {
            responseObserver.onNext(shopService.createShop(request));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void appendItemHistory(AppendItemHistoryRequest request, StreamObserver<AppendItemHistoryResponse> responseObserver) {
        try {
            responseObserver.onNext(itemService.appendItemHistory(request));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
