package TeamCamp.demo.service;

import TeamCamp.demo.common.s3.AwsS3Service;
import TeamCamp.demo.common.s3.FileService;
import lombok.RequiredArgsConstructor;
import net.sf.ehcache.util.ProductInfo;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.repository.ProductRepository;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.ProductDto.ProductInfoResponse;
import TeamCamp.demo.exception.product.ProductNotFoundException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

import static TeamCamp.demo.util.FilePathConstants.PRODUCT_IMAGES_DIR;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final AwsS3Service awsS3Service;

    @Transactional
    public void saveProduct(ProductDto.SaveRequest request, MultipartFile productImage) throws IOException {
        if (productImage != null){
            String originImagePath = awsS3Service.uploadProductImage(productImage);
            String thumbnailImagePath = FileService.toThumbnail(originImagePath);
            request.setImagePath(originImagePath,thumbnailImagePath);
        }
        productRepository.save(request.toEntity());
    }


    @Cacheable(value = "product",key = "#id")
    public ProductInfoResponse getProductInfo(Long id){
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException())
                .toProductInfoResponse();
    }
    @Cacheable(value = "product",key = "#id")
    @Transactional
    public void deleteProduct(Long id){
       Product product = productRepository.findById(id)
                       .orElseThrow(ProductNotFoundException::new);
       String path = product.getOriginImagePath();
       String key  = FileService.getFileName(path);
       productRepository.deleteById(id);
       awsS3Service.deleteProductImage(key);

    }

    @Cacheable(value = "product",key = "#id")
    @Transactional
    public void updateProduct(Long id, ProductDto.SaveRequest updateProduct,@Nullable MultipartFile productImage){
        Product saveProduct = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException());
        String savedImagePath = saveProduct.getOriginImagePath();
        String updateImagePath = updateProduct.getOriginImagePath();

        if (isDeleteSavedImage(savedImagePath,updateImagePath,productImage)){
            String key = FileService.getFileName(savedImagePath);
            awsS3Service.deleteProductImage(key);
            updateProduct.deleteImagePath();
        }
        if((productImage != null)){
            String originImagePath = awsS3Service.uploadProductImage(productImage);
            String thunmbnailImagePath = FileService.toThumbnail(originImagePath);
            updateProduct.setImagePath(originImagePath,thunmbnailImagePath);
        }
        saveProduct.update(updateProduct);
    }

    private boolean isDeleteSavedImage(String savedImagePath, String updatedImagePath,
                                       MultipartFile brandImage) {
        return ((updatedImagePath == null && savedImagePath != null) ||
                (updatedImagePath != null && brandImage != null));
    }
}
