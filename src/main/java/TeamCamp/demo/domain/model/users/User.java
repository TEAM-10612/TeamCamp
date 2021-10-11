package TeamCamp.demo.domain.model.users;

import TeamCamp.demo.domain.model.point.Point;
import TeamCamp.demo.domain.model.point.PointDivision;
import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.users.user.Account;
import TeamCamp.demo.domain.model.wishlist.ProductWishList;
import TeamCamp.demo.domain.model.wishlist.Wishlist;
import TeamCamp.demo.dto.PointDto;
import TeamCamp.demo.dto.ProductDto.WishProductResponse;
import TeamCamp.demo.exception.LowPointException;
import lombok.*;
import TeamCamp.demo.domain.model.users.user.address.Address;
import TeamCamp.demo.domain.model.users.user.address.AddressBook;
import TeamCamp.demo.exception.user.UnableToChangeNicknameException;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import TeamCamp.demo.dto.UserDto.UserDetailResponse;
import static TeamCamp.demo.dto.UserDto.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends UserBase {
    private String nickname;

    private LocalDateTime nicknameModifiedDate;


    private String phone;

    @OneToMany(mappedBy = "user")
    private List<Product> products = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY/*orphanRemoval = true*/)
    @JoinColumn(name = "ADDRESSBOOK_ID")
    private AddressBook addressBook;

    @Embedded
    private Account account;

    private Long point;

    @OneToOne(fetch = FetchType.LAZY,orphanRemoval = true)
    @JoinColumn(name = "WISHLIST_ID")
    private Wishlist wishlist;

    private UserStatus userStatus;

    @OneToMany(mappedBy = "user")
    private List<Point> pointBreakdown = new ArrayList<>();

    public UserInfoDto toUserInfoDto() {
        return UserInfoDto.builder()
                .email(this.getEmail())
                .nickname(this.getNickname())
                .phone(this.getPhone())
                .userLevel(this.userLevel)
                .build();
    }

    public UserInfo toUserInfo() {
        return UserInfo.builder()
                .id(this.getId())
                .nicknameModifiedDate(this.getNicknameModifiedDate())
                .email(this.getEmail())
                .nickname(this.getNickname())
                .phone(this.getPhone())
                .userLevel(this.userLevel)
                .userStatus(this.userStatus)
                .build();
    }

    public FindUserResponse toFindUserDto(){
        return FindUserResponse.builder()
                .email(this.getEmail())
                .phone(this.getPhone())
                .build();
    }


    public void updatePassword(String password){
        this.password = password;
    }

    public void updateAccount(Account account){
        this.account = account;
    }

    public void addAddress(Address address){
        this.addressBook.addAddress(address);
    }


    public void updateNickname(SaveRequest request){
        if(canModifiedNickname()){
            throw new UnableToChangeNicknameException("닉네임은 7일에 한번만 변경할 수 있습니다.");
        }
        String nickname = request.getNickname();
        this.nickname = nickname;
        this.nicknameModifiedDate = LocalDateTime.now();
    }

    private boolean canModifiedNickname() {
        return !(this.nicknameModifiedDate.isBefore(LocalDateTime.now().minusDays(7)));
    }

   public void updateUserLevel(){
        this.userLevel = UserLevel.AUTH;
   }

    @Builder
    public User(Long id,String email, String password, UserLevel userLevel,
                String nickname, LocalDateTime nicknameModifiedDate,
                String phone,AddressBook addressBooks,UserStatus userStatus,Long point, List<Point> pointBreakdown) {
        super( id, email, password, userLevel);
        this.nickname = nickname;
        this.nicknameModifiedDate = nicknameModifiedDate;
        this.phone = phone;
        this.addressBook = addressBooks;
        this.userStatus = userStatus;
        this.point = point;
        this.pointBreakdown = pointBreakdown;
    }

    public UserDetailResponse toUserDetailsDto() {
        return UserDetailResponse.builder()
                .id(this.getId())
                .email(this.email)
                .nickname(this.nickname)
                .phoneNumber(this.phone)
                .account(this.account)
                .modifiedDate(this.getModifiedDate())
                .createDate(this.getCreateDate())
                .userLevel(this.userLevel)
                .userStatus(this.userStatus)
                .build();
    }

    public void createAddressBook(AddressBook addressBook){
        this.addressBook = addressBook;
    }

    public void deleteAddress(Address address){
        this.addressBook.deleteAddress(address);
    }

    public void updatedUserStatus(UserStatus userStatus){
        this.userStatus = userStatus;
    }

    public boolean isBan(){
        return this.userStatus == UserStatus.BAN;
    }

    public void createWishList(Wishlist wishlist){
        this.wishlist = wishlist;
    }

    public void addWishListProduct(ProductWishList productWishList){
        wishlist.addWishListProduct(productWishList);
    }

    public Set<WishProductResponse> getWishLists(){
        return wishlist.getWishLists()
                .stream()
                .map(ProductWishList :: toWishProductDto)
                .collect(Collectors.toSet());

    }

    public boolean checkProductDuplicate(ProductWishList productWishList){
        return wishlist.getWishLists()
                .stream()
                .map(ProductWishList :: getProduct)
                .anyMatch(v -> v.getId() == productWishList.getProductId());
    }


    public TradeUserInfo createTradeUserInfo(){
        return TradeUserInfo.builder()
                .account(this.account)
                .addressBook(this.addressBook)
                .build();
    }


    public Address findAddress(Long addressId){
        return addressBook.findAddress(addressId);
    }

    public void chargingPoint(Long chargeAmount) {
        this.point += chargeAmount;
    }

    public boolean isCurrentEmail(String email){
        return this.email.equals(email);
    }

    public void pointInspection(Long price){
        if(this.point < price){
            throw new LowPointException("포인트가 부족합니다.충전 후 이용해주세요");
        }
    }

    public void deductionOfPoints(Long price){
        this.point -= price;
    }
    public List<PointDto.PointHistoryDto> getDeductionHistory() {
        return pointBreakdown.stream()
                .filter(p -> p.getDivision().equals(PointDivision.WITHDRAW) || p.getDivision()
                        .equals(PointDivision.PURCHASE_DEDUCTION))
                .map(Point::toPointHistoryDto)
                .collect(Collectors.toList());
    }

    public List<PointDto.PointHistoryDto> getChargingHistory() {
        return pointBreakdown.stream()
                .filter(p -> p.getDivision().equals(PointDivision.CHARGE) || p.getDivision()
                        .equals(PointDivision.SALES_REVENUE))
                .map(Point::toPointHistoryDto)
                .collect(Collectors.toList());
    }

    public boolean hasRemainPoints(){
        return point > 0;
    }
}
