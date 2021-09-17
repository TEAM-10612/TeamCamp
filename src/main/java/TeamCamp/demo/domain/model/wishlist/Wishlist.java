package TeamCamp.demo.domain.model.wishlist;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Wishlist {

    @Id@GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "wishlist")
    private Set<ProductWishList> wishLists  = new HashSet<>();

    public void addWishListProduct(ProductWishList productWishList){
        wishLists.add(productWishList);
    }
}
