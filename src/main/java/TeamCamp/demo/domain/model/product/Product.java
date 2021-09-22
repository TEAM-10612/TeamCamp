package TeamCamp.demo.domain.model.product;

import TeamCamp.demo.dto.ProductDto;
import lombok.*;
import TeamCamp.demo.domain.model.users.BaseTimeEntity;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.dto.ProductDto.ProductInfoResponse;
import TeamCamp.demo.dto.ProductDto.SaveRequest;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor
public class Product extends BaseTimeEntity {

    @Id@GeneratedValue
    @Column(name = "PRODUCT_ID")
    private Long id;

    private String name;

    private String salePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    private String productDescription;


    @Enumerated(EnumType.STRING)
    private ProductState productState;

    private String originImagePath;
    private String thumbnailImagePath;


    public ProductInfoResponse toProductInfoResponse(){
        return ProductInfoResponse.builder()
                .id(this.id)
                .name(this.name)
                .user(this.user)
                .salePrice(this.salePrice)
                .productDescription(this.productDescription)
                .productState(this.productState)
                .originImagePath(this.originImagePath)
                .build();
    }


   public ProductDto.ProductInfoByTrade toProductInfoByTrade(User currentUser){
        return ProductDto.ProductInfoByTrade.builder()
                .id(this.id)
                .name(this.name)
                .user(this.user)
                .salePrice(this.salePrice)
                .productDescription(this.productDescription)
                .productState(this.productState)
                .build();
   }

    public void update(SaveRequest request){
        this.name =request.getName();
        this.salePrice = request.getSalePrice();
        this.productDescription = request.getProductDescription();
        this.productState = request.getProductState();
        this.originImagePath = request.getOriginImagePath();
        this.thumbnailImagePath = request.getThumbnailImagePath();
    }

}
