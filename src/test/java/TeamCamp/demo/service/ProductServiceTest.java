package TeamCamp.demo.service;

import TeamCamp.demo.common.s3.AwsS3Service;
import TeamCamp.demo.common.s3.FileService;
import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.product.ProductState;
import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.domain.model.users.UserStatus;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.repository.ProductRepository;
import TeamCamp.demo.domain.repository.UserRepository;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.ProductDto.SaveRequest;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.exception.product.ImageRoadFailedException;
import TeamCamp.demo.exception.product.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    AwsS3Service awsS3Service;

    @InjectMocks
    ProductService productService;


    public User toEntity() {
        return User.builder()
                .email("rddd@naver.com")
                .password("12222333")
                .nicknameModifiedDate(LocalDateTime.now())
                .nickname("ryu")
                .phone("01022334455")
                .userLevel(UserLevel.UNAUTH)
                .userStatus(UserStatus.NORMAL)
                .build();
    }
    private UserDto.UserInfo userInfo(){
        return UserDto.UserInfo.builder()
                .id(1L)
                .phone("01022334455")
                .nickname("ryu")
                .email("rddd@naver.com")
                .userLevel(UserLevel.UNAUTH)
                .build();
    }
    User user = toEntity();


    private String ProductOriginImagePath = "https://TremCamp-product-origin.s3.ap-northeast-2.amazonaws.com/sample.png";
    private String ProductThumbnailImagePath = "https://TremCamp-product-thumbnail.s3.ap-northeast-2.amazonaws.com/sample.png";
    private String ProductChangedOriginImagePath = "https://TremCamp-product-origin.s3.ap-northeast-2.amazonaws.com/sample.png";
    private String ProductChangedThumbnailImagePath = "https://TremCamp-product-thumbnail.s3.ap-northeast-2.amazonaws.com/sample.png";

    private Product createProduct(){
        return Product.builder()
                .name("텐트")
                .user(user)
                .productDescription("good")
                .productState(ProductState.BEST)
                .originImagePath(ProductOriginImagePath)
                .thumbnailImagePath(ProductThumbnailImagePath)
                .build();
    }


    private ProductDto.SaveRequest  createProduct2(){
        return ProductDto.SaveRequest.builder()
                .name("텐트")
                .userInfo(user.toUserInfo())
                .productDescription("good")
                .productState(ProductState.BEST)
                .originImagePath(ProductOriginImagePath)
                .thumbnailImagePath(ProductThumbnailImagePath)
                .build();
    }

    private Product createProductWithOutImage(){
        return Product.builder()
                .name("텐트")
                .user(user)
                .productDescription("good")
                .productState(ProductState.BEST)
                .build();
    }


    private List<Product>createListProduct(){
        List<Product>productList = new ArrayList<>();
        productList.add(createProduct());
        productList.add(createProductWithOutImage());

        return productList;
    }

    private SaveRequest createProductRequest(){
        return SaveRequest .builder()
                .name("텐트")
                .userInfo(userInfo())
                .productDescription("good")
                .productState(ProductState.BEST)
                .originImagePath(ProductOriginImagePath)
                .thumbnailImagePath(ProductThumbnailImagePath)
                .build();
    }


    private SaveRequest createProductWithOutImageRequest(){
        return SaveRequest.builder()
                .name("텐트")
                .userInfo(userInfo())
                .productDescription("good")
                .productState(ProductState.BEST)
                .build();
    }

    private SaveRequest updateProductWithOutImageRequest(){
        return SaveRequest.builder()
                .name("화구")
                .userInfo(userInfo())
                .productDescription("good")
                .productState(ProductState.BEST)
                .build();
    }
    private MultipartFile createImageFile() {
        return new MockMultipartFile("sample", "sample.png", MediaType.IMAGE_PNG_VALUE,
                "sample".getBytes());
    }


    @Test
    @DisplayName("특정 id를 가진 제품 조회 성공 ")
    void getProductInfo_O ()throws Exception{
        //given
        Product product =createProduct();
        Long id = product.getId();

        //when
        given(productRepository.findById(id)).willReturn(Optional.of(product));
        ProductDto.ProductInfoResponse productInfoResponse = productService.getProductInfo(id);
        //then

        assertThat(productInfoResponse.getId()).isEqualTo(id);
        assertThat(productInfoResponse.getName()).isEqualTo(product.getName());
        //assertThat(productInfoResponse.getUser().toEntity()).isEqualTo(product.getUser());
        assertThat(productInfoResponse.getProductDescription()).isEqualTo(product.getProductDescription());

        assertThat(productInfoResponse.getProductState()).isEqualTo(product.getProductState());
        assertThat(productInfoResponse.getOriginImagePath()).isEqualTo(product.getOriginImagePath());
        assertThat(productInfoResponse.getThumbnailImagePath()).isEqualTo(product.getThumbnailImagePath());
        Mockito.verify(productRepository,Mockito.times(1)).findById(id);

        System.out.println(productInfoResponse.getUser().getEmail());
        System.out.println(product.getUser().getEmail());
    }

    @Test
    @DisplayName("특정 id를 가진 브랜드가 존재하지 않아 조회에 실패한다.")
    void getProductInfo_X()throws Exception{
        //given
        Long id = 1L;

        //when
        given(productRepository.findById(id)).willReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, () -> productService.getProductInfo(id));
        Mockito.verify(productRepository, Mockito.times(1)).findById(id);

    }

    @Test
    @DisplayName("이미지 없이 제품 등록에 성공한다.")
    void saveProductWithOutImage()throws Exception{
        //given
        SaveRequest product = createProductRequest();
        //when
        productService.saveProduct(product,null);
        //then
        Mockito.verify(productRepository,Mockito.times(1)).save(any());

    }

    @Test
    @DisplayName("모든 제품 조회 성공")
    void getProductInfo()throws Exception{
        //given
        List<Product> productList = createListProduct();

        //when
        given(productRepository.findAll()).willReturn(productList);
        List<ProductDto.ProductInfoResponse> productInfoResponses =  productService.getProductInfos();

        //then
        assertThat(productInfoResponses.size()).isEqualTo(productList.size());
        Mockito.verify(productRepository,times(1)).findAll();


    }
    @Test
    @DisplayName("이미지와 함께 제품등록에 성공한다.")
    void saveProductWithImage()throws Exception{
        //given
        SaveRequest product = createProductWithOutImageRequest();
        MultipartFile file = createImageFile();

        //when
        BDDMockito.given(awsS3Service.uploadProductImage(file)).willReturn(ProductOriginImagePath);
        productService.saveProduct(product,file);

        //then
        assertThat(product.getOriginImagePath()).isEqualTo(ProductOriginImagePath);
        assertThat(product.getThumbnailImagePath()).isEqualTo(ProductThumbnailImagePath);
        verify(awsS3Service,times(1)).uploadProductImage(file);
        verify(productRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("이미지 업로드 실패로 제품 등록에 실패한다.")
    void ImageUploadFailProductSave()throws Exception{
        //given
        SaveRequest product = createProductWithOutImageRequest();
        MultipartFile file = createImageFile();

        //when
        given(awsS3Service.uploadProductImage(file)).willThrow(ImageRoadFailedException.class);

        //then
        assertThrows(ImageRoadFailedException.class, () -> productService.saveProduct(product,file));
        assertThat(product.getOriginImagePath()).isNull();
        assertThat(product.getThumbnailImagePath()).isNull();
        verify(awsS3Service, times(1)).uploadProductImage(file);
        verify(productRepository, never()).save(any());

    }

    @Test
    @DisplayName("이미지가 없는 제품 삭제 성공")
    void deleteProductWithOutImage()throws Exception{
        //given
        Product product = createProductWithOutImage();
        Long id = product.getId();

        //when
        given(productRepository.findById(id)).willReturn(Optional.of(product));
        productService.deleteProduct(id);

        //then
        verify(productRepository,times(1)).deleteById(id);

    }
    @Test
    @DisplayName("제품이 존재하지 않아 제품 살제에 실패한다.")
    void failToDeleteProductNotExist()throws Exception{
        //given
        Long id = 1L;

        //when
        given(productRepository.findById(id)).willReturn(Optional.empty());

        //then
        assertThrows(ProductNotFoundException.class, ()-> productService.deleteProduct(id));
        verify(productRepository, never()).deleteById(id);

    }

    @Test
    @DisplayName("이미지와 함께 제품 삭제 성공")
    void deleteProductWithImage()throws Exception{
        //given
        Product product = createProduct();
        Long id = product.getId();

        //when
        given(productRepository.findById(id)).willReturn(Optional.ofNullable(product));
        productService.deleteProduct(id);

        //then
       verify(productRepository, times(1)).deleteById(id);
       verify(awsS3Service, times(1)).deleteProductImage(any());

    }

    @Test
    @DisplayName("이미지 없이 업데이트 성공 ")
    void updateProductWithoutImage()throws Exception{
        //given
        Product product = createProduct();
        SaveRequest updateProductDto = createProductWithOutImageRequest();
        Long id = product.getId();

        //when
        given(productRepository.findById(id)).willReturn(Optional.of(product));
        productService.updateProduct(id,updateProductDto,null);

        //then

        assertThat(updateProductDto.getOriginImagePath()).isNull();


    }


    @Test
    @DisplayName("기존 이미지 삭제 성공")
    void updateProductWithDeleteImage()throws Exception{
        //given
        Product saveProduct = createProduct();
        SaveRequest updateProduct = createProductWithOutImageRequest();
        Long id = saveProduct.getId();
        String key = FileService.getFileName(saveProduct.getOriginImagePath());
        given(productRepository.findById(id)).willReturn(Optional.of(saveProduct));
        productService.updateProduct(id,updateProduct,null);
        assertThat(saveProduct.getThumbnailImagePath()).isNull();
        assertThat(saveProduct.getOriginImagePath()).isNull();
    }

    @Test
    @DisplayName("이미지가 없는 제품에 이미지 업데이트")
    void updateProductAddImage()throws Exception{
        //given
        Product product = createProductWithOutImage();
        SaveRequest updateProduct = updateProductWithOutImageRequest();
        MultipartFile file = createImageFile();
        Long id = product.getId();

        //when
        given(productRepository.findById(id)).willReturn(Optional.of(product));
        given(awsS3Service.uploadProductImage(file)).willReturn(ProductOriginImagePath);
        productService.updateProduct(id,updateProduct,file);

        //then
        assertThat(product.getOriginImagePath()).isEqualTo(ProductOriginImagePath);
        assertThat(product.getThumbnailImagePath()).isEqualTo(ProductThumbnailImagePath);
        verify(awsS3Service,times(1)).uploadProductImage(file);

    }

    @Test
    @DisplayName("기존 이미지 삭제 후 새로운 이미지 업데이트")
    void updateImage()throws Exception{
        //given
        Product product = createProduct();
        SaveRequest updateProduct = createProductRequest();
        MultipartFile file = createImageFile();
        String key = FileService.getFileName(product.getOriginImagePath());
        Long id = product.getId();

        //when

        given(productRepository.findById(id)).willReturn(Optional.of(product));
        given(awsS3Service.uploadProductImage(file)).willReturn(ProductOriginImagePath);

        productService.updateProduct(id, updateProduct,file);

        //then
        assertThat(product.getOriginImagePath()).isEqualTo(ProductChangedOriginImagePath);
        assertThat(product.getThumbnailImagePath()).isEqualTo(ProductChangedThumbnailImagePath);
        verify(awsS3Service, times(1)).deleteProductImage(key);
        verify(awsS3Service,times(1)).uploadProductImage(file);

    }

    @Test
    @DisplayName("브랜드가 존재하지 않아서 업데이트에 실패한다.")
    void failToUpdateProductNotExist()throws Exception{
        //given
        SaveRequest updateProductDto = createProductWithOutImageRequest();
        Long id =1L;
        //when
        given(productRepository.findById(id)).willReturn(Optional.empty());
        //then
        assertThrows(ProductNotFoundException.class ,
                ()->productService.updateProduct(id,updateProductDto,null)
                );

    }
}