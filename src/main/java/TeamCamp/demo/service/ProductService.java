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
    public void updateProduct(Long id, ProductDto.SaveRequest updateProduct,MultipartFile productImage){
        Product saveProduct = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException());
        String saveImagePath = saveProduct.getOriginImagePath();

        if((productImage != null)){
            String originImagePath = awsS3Service.uploadProductImage(productImage);
            String thunmbnailImagePath = FileService.toThumbnail(originImagePath);
            updateProduct.setImagePath(originImagePath,thunmbnailImagePath);
        }
        saveProduct.update(updateProduct);
    }

    private boolean isDeleteImage(boolean imageDeleteCheck,MultipartFile productImage,String savedImagePath){
        if(imageDeleteCheck || ((productImage != null)&& (savedImagePath != null))){
            return true;
        }else {
            return false;
        }
    }

}
