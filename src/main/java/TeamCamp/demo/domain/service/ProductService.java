package TeamCamp.demo.domain.service;

import lombok.RequiredArgsConstructor;
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

import static TeamCamp.demo.util.FilePathConstants.PRODUCT_IMAGES_DIR;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final S3Service awsS3Service;

    @Transactional
    public void saveProduct(ProductDto.SaveRequest request, MultipartFile productImage) throws IOException {
        if (productImage != null){
            String imagePath = awsS3Service.upload(productImage,PRODUCT_IMAGES_DIR);
            request.setImagePath(imagePath);
        }
        productRepository.save(request.toEntity());
    }


    @Cacheable(value = "product",key = "#id")
    public ProductInfoResponse getProductInfo(Long id){
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException())
                .toProductInfoResponse();
    }
    @Cacheable(value = "product",key = "#id")
    public void deleteProduct(Long id){
        if(!productRepository.existsById(id)){
            throw new ProductNotFoundException();
        }
        productRepository.deleteById(id);
    }

    @Cacheable(value = "product",key = "#id")
    @Transactional
    public void updateProduct(Long id, ProductDto.SaveRequest request){
        Product saveProduct = productRepository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException());

        saveProduct.update(request);
    }
}
