package TeamCamp.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import TeamCamp.demo.common.annotation.LoginCheck;
import TeamCamp.demo.service.ProductService;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.ProductDto.SaveRequest;

import javax.validation.Valid;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @LoginCheck
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@Valid@RequestBody SaveRequest request ,
                              @RequestPart MultipartFile productImage) throws IOException {
        productService.saveProduct(request,productImage);

    }

    @GetMapping("/{id}")
    @LoginCheck
    public ResponseEntity<ProductDto.ProductInfoResponse>getProductInfo(@PathVariable Long id){
        ProductDto.ProductInfoResponse response = productService.getProductInfo(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);

    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProduct(@PathVariable Long id,
                                             @Valid@RequestBody SaveRequest request,@RequestPart(required = false)MultipartFile productImage) {
        productService.updateProduct(id, request,productImage);
    }
}
