package TeamCamp.demo.dto;

import TeamCamp.demo.domain.model.trade.TradeStatus;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.UserDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class TradeDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TradeResource{

        private UserDto.TradeUserInfo tradeUserInfo;
        private ProductDto.ProductInfoByTrade productInfoByTrade;

        @Builder
        public TradeResource(UserDto.TradeUserInfo tradeUserInfo,
                           ProductDto.ProductInfoByTrade productInfoByTrade) {
            this.tradeUserInfo = tradeUserInfo;
            this.productInfoByTrade = productInfoByTrade;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TradeRequest {

        private Long tradeId;
        private Long addressId;
        private Long productId;

        @Builder
        public TradeRequest(Long tradeId, Long addressId, Long productId) {
            this.tradeId = tradeId;
            this.addressId = addressId;
            this.productId = productId;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TrackingNumberRequest{

        private String trackingNumber;

        @Builder
        public TrackingNumberRequest(String trackingNumber) {
            this.trackingNumber = trackingNumber;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChangeRequest{
        private Long tradeId;
        private Long price;

        @Builder
        public ChangeRequest(Long tradeId, Long price) {
            this.tradeId = tradeId;
            this.price = price;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    private static class SaveRequest{
        private Long productId;
        private Long addressId;
        private Long price;
        @Builder
        public SaveRequest(Long price, Long productId, Long addressId) {
            this.price = price;
            this.productId = productId;
            this.addressId = addressId;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TradeSearchCondition{
        private Long tradeId;
        private String sellerEmail;
        private String buyerEmail;

        @Builder
        public TradeSearchCondition(Long tradeId,
                                    String sellerEmail, String buyerEmail) {
            this.tradeId = tradeId;
            this.sellerEmail = sellerEmail;
            this.buyerEmail = buyerEmail;
        }

        public boolean isSearchByBuyer(){
            return (this.getBuyerEmail() != null && this.getTradeId() == null &&
                    this.getSellerEmail() == null);
        }

        public boolean isSearchBySeller(){
            return (this.getSellerEmail() != null && this.getTradeId() == null
            && this.getBuyerEmail() == null);
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TradeInfoResponse{
        private Long id;
        private TradeStatus tradeStatus;

        @Builder
        public TradeInfoResponse(Long id, TradeStatus tradeStatus) {
            this.id = id;
            this.tradeStatus = tradeStatus;
        }
    }
    @Getter
    @NoArgsConstructor
    public static class TradeCompleteInfo{

        private Long price;
        private LocalDateTime completeTime;

        @Builder
        public TradeCompleteInfo(Long price, LocalDateTime completeTime) {
            this.price = price;
            this.completeTime = completeTime;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class TradeMonthSearchCondition{
        private String year;
    }


    @Getter
    @NoArgsConstructor
    public static class MonthlyTradingVolumesResponse{
        private String date;
        private Long count;
    }
}
