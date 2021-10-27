package TeamCamp.demo.controller;

import TeamCamp.demo.common.annotation.CurrentUser;
import TeamCamp.demo.common.annotation.LoginCheck;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.ProductDto.WishProductResponse;
import TeamCamp.demo.service.WishListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlists")
public class WishListApiController {

    private final WishListService wishListService;

    @GetMapping
    @LoginCheck
    public Set<WishProductResponse> getWish(@CurrentUser String email){
        return wishListService.getWishList(email);
    }

    @LoginCheck
    @PostMapping
    public void addWishList(@CurrentUser String email, @RequestBody ProductDto.IdRequest idRequest){
        wishListService.addWishList(email,idRequest);
    }

    @LoginCheck
    @DeleteMapping
    public void deleteWishList(@RequestBody ProductDto.IdRequest idRequest){
        wishListService.deleteWishList(idRequest);
    }
}
