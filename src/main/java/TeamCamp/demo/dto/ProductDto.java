package TeamCamp.demo.dto;

import TeamCamp.demo.domain.model.trade.OrderStandard;
import lombok.*;
import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.product.ProductState;
import TeamCamp.demo.domain.model.users.User;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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

        private User user;


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
                    .productDescription(this.productDescription)
                    .productState(this.productState)
                    .originImagePath(this.originImagePath)
                    .thumbnailImagePath(this.thumbnailImagePath)
                    .build();
        }

    }
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProductInfoResponse{
        private Long id;
        private String name;
        private User user;
        private String productDescription;
        private String originImagePath;
        private String thumbnailImagePath;
        private ProductState productState;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductInfoByTrade{
        private Long id;
        private String name;
        private User user;
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
    @NoArgsConstructor
    public static class WishProductResponse{
        private Long id;
        private Long productId;
        private String name;
        private Product product;

        @Builder
        public WishProductResponse(Long id, Long productId, String name,Product product) {
            this.id = id;
            this.productId = productId;
            this.product = product;
            this.name = name;
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
    public static class SearchCondition{
        private String keyword;
        private Long productId;
        private OrderStandard orderStandard;
    }



}
