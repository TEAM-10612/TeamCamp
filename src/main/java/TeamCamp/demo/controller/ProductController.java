package TeamCamp.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import TeamCamp.demo.annotation.LoginCheck;
import TeamCamp.demo.domain.service.ProductService;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.ProductDto.SaveRequest;

import javax.validation.Valid;

import java.io.IOException;

import static TeamCamp.demo.util.ResponseConstants.CREATED;
import static TeamCamp.demo.util.ResponseConstants.OK;

@RequiredArgsConstructor
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @LoginCheck
    @PostMapping
    public ResponseEntity<Void>createProduct(@Valid@RequestBody SaveRequest request , @RequestPart MultipartFile productImage) throws IOException {
        productService.saveProduct(request,productImage);
        return CREATED;
    }

    @GetMapping("/{id}")
    @LoginCheck
    public ResponseEntity<ProductDto.ProductInfoResponse>getProductInfo(@PathVariable Long id){
        ProductDto.ProductInfoResponse response = productService.getProductInfo(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return OK;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void>updateProduct(@PathVariable Long id,
                                             @Valid@RequestBody SaveRequest request){
        productService.updateProduct(id, request);
        return OK;
    }
}
