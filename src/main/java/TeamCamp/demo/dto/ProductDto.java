package TeamCamp.demo.dto;

import TeamCamp.demo.domain.model.trade.OrderStandard;
import lombok.*;
import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.product.ProductState;
import TeamCamp.demo.domain.model.users.User;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class ProductDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaveRequest{

        @NotBlank(message = "제품명을 입력해주세요.")
        private String name;

        @NotBlank(message = "제품에 대한 설명을 입력해주세요.")
        @Size(max = 200,message = "200자 이내로 입력해주세요.")
        private String productDescription;

        private UserDto.UserInfo userInfo;


        @NotNull(message = "제품의 상태를 선택해주세요.")
        private ProductState productState;

        private String originImagePath;
        private String thumbnailImagePath;

        public void setImagePath(String originImagePath,String thumbnailImagePath){
            this.originImagePath = originImagePath;
            this.thumbnailImagePath = thumbnailImagePath;
        }

        public void deleteImagePath(){
            setImagePath(null,null);
        }



        public Product toEntity(){
            return TeamCamp.demo.domain.model.product.Product.builder()
                    .name(this.name)
                    .user(this.userInfo.toEntity())
                    .productDescription(this.productDescription)
                    .productState(this.productState)
                    .originImagePath(this.originImagePath)
                    .thumbnailImagePath(this.thumbnailImagePath)
                    .build();
        }

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Builder
    public static class ProductInfoResponse{
        private Long id;
        private String name;
        private UserDto.UserInfo user;
        private String productDescription;
        private ProductState productState;
        private String originImagePath;
        private String thumbnailImagePath;
        private List<TradeDto.TradeCompleteInfo> tradeCompleteInfos = new ArrayList<>();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInfoByTrade{
        private Long id;
        private String name;
        private UserDto.UserInfo user;
        private Long buyPrice;
        private Long sellPrice;
        private String productDescription;
        private ProductState productState;

    }
    @Getter
    @NoArgsConstructor
    public static class IdRequest{
        private Long id;

        @Builder
        public IdRequest(Long id) {
            this.id = id;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class WishProductResponse{
        private Long id;
        private Long productId;
        private String name;
        private User user;

        @Builder
        public WishProductResponse(Long id, Long productId, String name, User user) {
            this.id = id;
            this.productId = productId;
            this.name = name;
            this.user = user;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ThumbnailResponse{
        private Long id;
        private String productThumbnailImagePath;
        private String name;
        private Long lowerPrice;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCondition{
        private String keyword;
        private OrderStandard orderStandard;
    }



}
