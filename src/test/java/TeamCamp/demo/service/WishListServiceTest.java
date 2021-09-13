package TeamCamp.demo.service;

import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.product.ProductState;
import TeamCamp.demo.domain.model.product.TransactionMethod;
import TeamCamp.demo.domain.model.users.user.User;
import TeamCamp.demo.domain.model.wishlist.ProductWishList;
import TeamCamp.demo.domain.model.wishlist.Wishlist;
import TeamCamp.demo.domain.repository.ProductRepository;
import TeamCamp.demo.domain.repository.ProductWishListRepository;
import TeamCamp.demo.domain.repository.UserRepository;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.exception.product.DuplicateProductWishListException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishListServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    ProductWishListRepository productWishListRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    WishListService wishListService;

    private UserDto.SaveRequest createUserDto() {
        UserDto.SaveRequest saveRequest = UserDto.SaveRequest.builder()
                .email("test123@test.com")
                .password("test1234")
                .phone("01011112222")
                .nickname("17171771")
                .build();
        return saveRequest;
    }

    public User createUser(){
        return createUserDto().toEntity();
    }

    private String ProductOriginImagePath = "https://TremCamp-product-origin.s3.ap-northeast-2.amazonaws.com/sample.png";
    private String ProductThumbnailImagePath = "https://TremCamp-product-thumbnail.s3.ap-northeast-2.amazonaws.com/sample.png";

    private Product createProduct(){
        return Product.builder()
                .name("텐트")
                .user(createUser())
                .salePrice("230000")
                .productDescription("good")
                .releasePrice("300000")
                .productState(ProductState.BEST)
                .transactionMethod(TransactionMethod.NON_CONTACT)
                .originImagePath(ProductOriginImagePath)
                .thumbnailImagePath(ProductThumbnailImagePath)
                .build();
    }

    @Test
    @DisplayName("중복된 상품이 아닌경우 위시리스트에 추가한다.")
    void addWishList(){
        User user =createUser();
        Product product = createProduct();
        Wishlist wishlist = new Wishlist();
        user.createWishList(wishlist);

        ProductWishList productWishList = ProductWishList.builder()
                .wishlist(wishlist)
                .product(product)
                .build();

        String email = "test123@test.com";
        ProductDto.IdRequest idRequest = ProductDto.IdRequest.builder()
                .id(2L)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(idRequest.getId())).thenReturn(Optional.of(product));
        when(productWishListRepository.save(any())).thenReturn(productWishList);

        wishListService.addWishList(email,idRequest);
        Assertions.assertThat(user.getWishLists().size()).isEqualTo(1);
        verify(userRepository, atLeastOnce()).findByEmail(email);
        verify(productRepository,atLeastOnce()).findById(idRequest.getId());
        verify(productWishListRepository,atLeastOnce()).save(any());
    }
    @Test
    @DisplayName("동일한 상품이 위시리스트에 있을경우 DuplicateWishListItemException 발생")
    void failToAddWishList()throws Exception{
        //given
        User user =createUser();
        Product product = createProduct();
        Wishlist wishlist = new Wishlist();
        user.createWishList(wishlist);

        ProductWishList productWishList = ProductWishList.builder()
                .wishlist(wishlist)
                .product(product)
                .build();

        String email = "test123@test.com";
        ProductDto.IdRequest idRequest = ProductDto.IdRequest.builder()
                .id(2L)
                .build();
        user.addWishListProduct(productWishList);

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(productRepository.findById(idRequest.getId())).thenReturn(Optional.of(product));
        when(productWishListRepository.save(any())).thenReturn(productWishList);

        //then
        assertThrows(DuplicateProductWishListException.class,
                ()->wishListService.addWishList(email,idRequest));
        verify(productRepository,atLeastOnce()).findById(idRequest.getId());
        verify(userRepository, atLeastOnce()).findByEmail(email);
        verify(productWishListRepository,atLeastOnce()).save(any());

    }

    @Test
    @DisplayName("위시리스트 조회")
    void getWishList()throws Exception{
        //given
        User user = createUser();
        Wishlist wishlist = new Wishlist();
        user.createWishList(wishlist);
        String email = "test123@test.com";

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        wishListService.getWishList(email);

        //then
        Assertions.assertThat(user.getWishLists().size()).isEqualTo(0);
        verify(userRepository,atLeastOnce()).findByEmail(email);

    }

    @Test
    @DisplayName("카트에서 제품 삭제")
    void deleteWishList()throws Exception{
        //given
        ProductDto.IdRequest idRequest = ProductDto.IdRequest.builder()
                .id(1L)
                .build();
        //when
        wishListService.deleteWishList(idRequest);

        //then
        verify(productWishListRepository,atLeastOnce()).deleteById(idRequest.getId());

    }
}