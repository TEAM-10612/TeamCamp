package TeamCamp.demo.controller;

import TeamCamp.demo.common.annotation.CurrentUser;
import TeamCamp.demo.common.annotation.LoginCheck;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("carts")
public class WishListApiController {

    private final WishListService wishListService;

    @GetMapping("/wishlists")
    @LoginCheck
    public Set<ProductDto.WishProductResponse> getWish(@CurrentUser String email){
        return wishListService.getWishList(email);
    }

    @LoginCheck
    @PostMapping("/wishlists")
    public void addWishList(@CurrentUser String email, @RequestBody ProductDto.IdRequest idRequest){
        wishListService.addWishList(email,idRequest);
    }

    @LoginCheck
    @DeleteMapping("/wishLists")
    public void deleteWishList(@RequestBody ProductDto.IdRequest idRequest){
        wishListService.deleteWishList(idRequest);
    }
}
