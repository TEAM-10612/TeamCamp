package TeamCamp.demo.domain.model.trade;


import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.users.user.address.Address;
import TeamCamp.demo.domain.repository.AddressRepository;
import TeamCamp.demo.domain.repository.ProductRepository;
import TeamCamp.demo.domain.repository.UserRepository;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static TeamCamp.demo.domain.model.trade.TradeDto.*;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TradeRepository tradeRepository;
    private final AddressRepository addressRepository;

    public TradeResource makeTradeResource(User user, Product product){
        ProductDto.ProductInfoByTrade productInfoByTrade = product.toProductInfoByTrade(user);
        UserDto.TradeUserInfo tradeUserInfo = user.createTradeUserInfo();

        return TradeResource.builder()
                .tradeUserInfo(tradeUserInfo)
                .productInfoByTrade(productInfoByTrade)
                .build();
    }

    @Transactional
    @Cacheable(value = "product" , key = "#request.productId")
    public void purchase(String email , TradeRequest request){
        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        Address shippingAddress =  addressRepository.findById(request.getAddressId()).orElseThrow();

        Trade trade = tradeRepository.findById(request.getTradeId())
                .orElseThrow();
        trade.makePurchase(buyer,shippingAddress);
    }

    @Transactional
    @Cacheable(value = "product", key = "request.productId")
    public void sale(String email, TradeRequest request){
        User seller = userRepository.findByEmail(email)
                .orElseThrow( () -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        Address returnAddress = addressRepository.findById(request.getAddressId()).orElseThrow();


        Trade trade = tradeRepository
                .findById(request.getTradeId()).orElseThrow();


        trade.makeSale(seller,returnAddress);
    }
}
