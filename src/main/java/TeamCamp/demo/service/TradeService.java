package TeamCamp.demo.service;


import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.trade.Trade;
import TeamCamp.demo.domain.model.trade.TradeStatus;
import TeamCamp.demo.domain.model.trade.repository.TradeRepository;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.users.user.address.Address;
import TeamCamp.demo.domain.repository.AddressRepository;
import TeamCamp.demo.domain.repository.ProductRepository;
import TeamCamp.demo.domain.repository.UserRepository;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.dto.TradeDto;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.exception.user.NotAuthorizedException;
import TeamCamp.demo.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static TeamCamp.demo.dto.TradeDto.*;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TradeRepository tradeRepository;
    private final AddressRepository addressRepository;
    private final PointService pointService;

    @Transactional(readOnly = true)
    public TradeResource getResource(String email,Long productId){
        User user  = userRepository.findByEmail(email)
                .orElseThrow(()->new UserNotFoundException("존재하지 않는 사용자입니다."));

        Product product = productRepository.findById(productId)
                .orElseThrow();

        return makeTradeResource(user,product);
    }
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
        buyer.deductionOfPoints(trade.getPrice());
        pointService.purchasePointPayment(buyer, trade.getPrice());
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

    @Transactional
    public void updateTrade(ChangeRequest request) {
        Trade trade = tradeRepository.findById(request.getTradeId()).orElseThrow();
        trade.updatePrice(request.getPrice());
    }

    @Transactional
    public void deleteTrade(ChangeRequest request) {
        Trade trade = tradeRepository.findById(request.getTradeId()).orElseThrow();
        pointService.purchasePointReturn(trade.getBuyer(),trade.getPrice());
        tradeRepository.deleteById(request.getTradeId());
    }
    @Transactional
    public void updateReceivingTrackingNumber(Long tradeId,String email,String trackingNumber){
        Trade trade = tradeRepository.findById(tradeId).orElseThrow();
        User seller = userRepository.findByEmail(email).orElseThrow(()->new UserNotFoundException("존재하지 않는 사용자입니다."));
        if (!trade.getSeller().getId().equals(seller.getId())){
            throw new NotAuthorizedException("접근 권한이 없는 사용자입니다.");
        }

        trade.updateReceivingTrackingNumber(trackingNumber);

        //거래 상태 다시 수정
        trade.updateStatus(TradeStatus.PROGRESS);
    }


    @Transactional
    public boolean hasUserProgressingTrade(User user){
        return tradeRepository.existProgressingByUser(user);
    }

    @Transactional
    public void confirmPurchase(Long tradeId, String email) {
        Trade trade =  tradeRepository.findById(tradeId).orElseThrow();
        if(!trade.isBuyersEmail(email)){
            throw new NotAuthorizedException("해당 구매자만 접근이 가능합니다.");
        }

        trade.endTrade();
        pointService.salesPointReceive(trade.getSeller(), trade.getPrice());
    }

    @Transactional
    public void updateTrackingNumber(Long tradeId,String trackingNumber){
        Trade trade = tradeRepository.findById(tradeId).orElseThrow();

        trade.updateStatusShipping(trackingNumber);
    }


}
