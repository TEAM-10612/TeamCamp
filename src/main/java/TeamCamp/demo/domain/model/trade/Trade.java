package TeamCamp.demo.domain.model.trade;

import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.users.BaseTimeEntity;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.users.user.address.Address;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;


    @Enumerated(EnumType.STRING)
    private TradeStatus tradeStatus;


    @OneToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "RETURN_ID")
    private Address returnAddress;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHIPPING_ID")
    private Address shippingAddress;

    @Builder
    public Trade(User seller, User buyer,
                 Product product, TradeStatus tradeStatus,
                 Address returnAddress, Address shippingAddress) {
        this.seller = seller;
        this.buyer = buyer;
        this.product = product;
        this.tradeStatus = tradeStatus;
        this.returnAddress = returnAddress;
        this.shippingAddress = shippingAddress;
    }

    public void makePurchase(User buyer,Address shippingAddress){
        this.shippingAddress = shippingAddress;
        this.buyer = buyer;
        this.tradeStatus = TradeStatus.PROGRESS;
    }

    public void ImmediateSale(User seller,Address shippingAddress){
        this.shippingAddress = shippingAddress;
        this.seller = seller;
        this.tradeStatus = TradeStatus.PROGRESS;
    }
}
