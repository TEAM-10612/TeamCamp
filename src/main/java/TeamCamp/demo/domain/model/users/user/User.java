package TeamCamp.demo.domain.model.users.user;

import TeamCamp.demo.domain.model.users.UserStatus;
import TeamCamp.demo.domain.model.wishlist.ProductWishList;
import TeamCamp.demo.domain.model.wishlist.Wishlist;
import TeamCamp.demo.dto.ProductDto;
import lombok.*;
import TeamCamp.demo.domain.model.users.UserBase;
import TeamCamp.demo.domain.model.users.UserLevel;
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


    @Column(name = "USER_NICKNAME")
    private String nickname;

    private LocalDateTime nicknameModifiedDate;

    @Column(name = "USER_PHONENUMBER")
    private String phone;


    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "USER_ID")
    private List<AddressBook> addressBook = new ArrayList<>();

    @Embedded
    @Column(name = "USER_ACCOUNT")
    private Account account;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WISHLIST_ID")
    private Wishlist wishlist;

    private UserStatus userStatus;
    public UserInfoDto toUserInfoDto() {
        return UserInfoDto.builder()
                .email(this.getEmail())
                .nickname(this.getNickname())
                .phone(this.getPhone())
                .account(this.getAccount())
                .userLevel(this.userLevel)
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

    public void addAddressBook(Address address){
        this.addressBook.add(new AddressBook(address));
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
                String phone,List<AddressBook>addressBooks,UserStatus userStatus) {
        super( id, email, password, userLevel);
        this.nickname = nickname;
        this.nicknameModifiedDate = nicknameModifiedDate;
        this.phone = phone;
        this.userLevel = userLevel;
        this.addressBook = addressBooks;
        this.userStatus = userStatus;
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

    public Set<ProductDto.WishProductResponse> getWishList(){
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
}
