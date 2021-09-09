package TeamCamp.demo.domain.model.wishlist;

import TeamCamp.demo.domain.model.users.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
public class Wishlist {

    @Id@GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "user")
    private User user;

    @OneToMany(mappedBy = "wishlist")
    private Set<ProductWishList> wishLists  = new HashSet<>();

    public void addWishListProduct(ProductWishList productWishList){
        wishLists.add(productWishList);
    }
}
