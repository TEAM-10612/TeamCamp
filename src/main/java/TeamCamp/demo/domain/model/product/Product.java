package TeamCamp.demo.domain.model.product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import TeamCamp.demo.domain.model.users.user.BaseTimeEntity;
import TeamCamp.demo.domain.model.users.user.User;
import TeamCamp.demo.dto.ProductDto.ProductInfoResponse;
import TeamCamp.demo.dto.ProductDto.SaveRequest;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Product extends BaseTimeEntity {

    @Id@GeneratedValue
    private Long id;

    private String name;

    private String salePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String productDescription;

    private String releasePrice;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    @Enumerated(EnumType.STRING)
    private ProductState productState;

    private String imagePath;

    @Enumerated(EnumType.STRING)
    private TransactionMethod transactionMethod;

    @Builder
    public Product(Long id, String name, String salePrice,User user, String productDescription, String releasePrice,
                   TransactionStatus transactionStatus, ProductState productState, TransactionMethod transactionMethod,String imagePath) {
        this.id = id;
        this.name = name;
        this.user = user;
        this.salePrice = salePrice;
        this.productDescription = productDescription;
        this.releasePrice = releasePrice;
        this.transactionStatus = transactionStatus;
        this.productState = productState;
        this.transactionMethod = transactionMethod;
        this.imagePath = imagePath;
    }

    public ProductInfoResponse toProductInfoResponse(){
        return ProductInfoResponse.builder()
                .id(this.id)
                .name(this.name)
                .user(this.user)
                .salePrice(this.salePrice)
                .productDescription(this.productDescription)
                .releasePrice(this.releasePrice)
                .transactionStatus(this.transactionStatus)
                .productState(this.productState)
                .imagePath(this.imagePath)
                .transactionMethod(this.transactionMethod)
                .build();
    }

    public void update(SaveRequest request){
        this.name = name;
        this.salePrice = salePrice;
        this.productDescription = productDescription;
        this.releasePrice = releasePrice;
        this.transactionStatus = transactionStatus;
        this.productState = productState;
        this.transactionMethod = transactionMethod;
    }

}
