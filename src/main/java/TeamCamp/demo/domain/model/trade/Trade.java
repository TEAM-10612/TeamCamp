package TeamCamp.demo.domain.model.trade;

import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.users.BaseTimeEntity;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.users.user.address.Address;
import TeamCamp.demo.dto.TradeDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.math3.analysis.function.Add;
import org.hibernate.mapping.ToOne;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Trade extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SELLER_ID")
    private  User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUYER_ID")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private Long price;

    @Enumerated(EnumType.STRING)
    private TradeStatus tradeStatus;


    @OneToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "RETURN_ID")
    private Address returnAddress;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPPING_ID")
    private Address shippingAddress;

    @Enumerated(EnumType.STRING)
    private TransactionMethod transactionMethod;

    private String receivingTrackingNumber;

    private String forwardingTrackingNumber;

    private String returnTrackingNumber;

    private String cancelReason;



    @Builder
    public Trade(Long id,User seller, User buyer, Product product, Long price, TradeStatus tradeStatus,
                 Address returnAddress, Address shippingAddress, TransactionMethod transactionMethod) {
        this.id = id;
        this.seller = seller;
        this.buyer = buyer;
        this.product = product;
        this.price = price;
        this.tradeStatus = tradeStatus;
        this.returnAddress = returnAddress;
        this.shippingAddress = shippingAddress;
        this.transactionMethod = transactionMethod;
    }

    public void makePurchase(User buyer, Address shippingAddress){
        this.shippingAddress = shippingAddress;
        this.buyer = buyer;
        this.tradeStatus = TradeStatus.PROGRESS;
    }
    public void makeSale(User seller,Address shippingAddress){
        this.shippingAddress = shippingAddress;
        this.seller = seller;
        this.tradeStatus = TradeStatus.PROGRESS;
    }


    public void updatePrice(Long price) {

        this.price = price;
    }

    //입고 배송번호
    public void updateReceivingTrackingNumber(String receivingTrackingNumber) {
        this.receivingTrackingNumber = receivingTrackingNumber;
    }
    //출고 배송번호
    public void updateForwardingTrackingNumber(String forwardingTrackingNumber) {
        this.forwardingTrackingNumber = forwardingTrackingNumber;
    }
    //반송 배송번호
    public void updateReturnTrackingNumber(String returnTrackingNumber) {
        this.returnTrackingNumber = returnTrackingNumber;
    }
    //취소 사유
    public void updateCancelReason(String cancelReason){
        this.cancelReason = cancelReason;
    }

    public void updateStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public boolean isBuyersEmail(String email) {
        return buyer.isCurrentEmail(email);
    }

    public boolean isSellersEmail(String email) {
        return seller.isCurrentEmail(email);
    }

    public void endTrade() {
        this.tradeStatus = TradeStatus.END;
        seller.chargingPoint(this.price);
    }
    public void updateStatusShipping(String trackingNumber){
        this.forwardingTrackingNumber = trackingNumber;
        this.tradeStatus = TradeStatus.PROGRESS;
    }
}
