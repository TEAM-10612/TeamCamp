package TeamCamp.demo.domain.model.wishlist;

import TeamCamp.demo.domain.model.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductWishList {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * CART에는 여러 PRODUCT를 담을 수 있고, PRODUCT 또한 여러 CART에 포함될 수 있다. 따라서 ManyToMany를 형성한다.
     * ManyToMany의 경우 정규화를 통해 1:N , N:1로 처리해야 한다.  따라서 중간테이블인 CartProduct라는 중간테이블을 생성해야 한다.
     */

    @ManyToOne
    @JoinColumn(name = "WISHLIST_ID")
    private Wishlist wishlist;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;
}
