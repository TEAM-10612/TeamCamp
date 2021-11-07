package TeamCamp.demo.service;

import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.wishlist.ProductWishList;
import TeamCamp.demo.domain.model.product.repository.ProductRepository;
import TeamCamp.demo.domain.model.wishlist.repository.ProductWishListRepository;
import TeamCamp.demo.domain.model.users.repository.UserRepository;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.exception.product.DuplicateProductWishListException;
import TeamCamp.demo.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class WishListService {

    private final ProductRepository productRepository;
    private final ProductWishListRepository productWishListRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Set<ProductDto.WishProductResponse> getWishList(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        return user.getWishLists();
    }

    @Transactional
    public void addWishList(String email, ProductDto.IdRequest idRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        Product product = productRepository.findById(idRequest.getId()).orElseThrow();
        ProductWishList productWishList = productWishListRepository.save(new ProductWishList(user.getWishlist(),product));

        if(user.checkProductDuplicate(productWishList)){
            throw new DuplicateProductWishListException("장바구니 중복");
        }
        user.addWishListProduct(productWishList);
    }

    @Transactional
    public void deleteWishList(ProductDto.IdRequest idRequest) {
        productWishListRepository.deleteById(idRequest.getId());
    }



}
