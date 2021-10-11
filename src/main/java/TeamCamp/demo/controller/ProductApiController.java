package TeamCamp.demo.controller;

import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.dto.ProductDto.ProductInfoResponse;
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
public class ProductApiController {

    private final ProductService productService;

    @LoginCheck(authority = UserLevel.AUTH)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@Valid@RequestPart SaveRequest requestDto ,
                              @RequestBody(required = false) MultipartFile productImage){
        productService.saveProduct(requestDto,productImage);

    }

    @GetMapping("/{id}")
    @LoginCheck
    public ProductInfoResponse getProductInfo(@PathVariable Long id){
        return productService.getProductInfo(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);

    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateProduct(@PathVariable Long id,
                              @Valid@RequestPart SaveRequest requestDto
            ,@RequestPart(required = false)MultipartFile productImage) {
        productService.updateProduct(id, requestDto,productImage);
    }
}
