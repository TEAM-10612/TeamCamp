package TeamCamp.demo.service;

import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.product.ProductState;
import TeamCamp.demo.domain.model.users.user.Account;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.users.user.address.Address;
import TeamCamp.demo.domain.model.users.user.address.AddressBook;
import TeamCamp.demo.domain.repository.*;
import TeamCamp.demo.dto.AddressDto;
import TeamCamp.demo.dto.UserDto;
import TeamCamp.demo.encrypt.EncryptionService;
import TeamCamp.demo.exception.user.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @ExtendWith : Junit5의 확장 어노테이션을 사용할 수 있다.
 * @Mock : mock 객체를 생성한다.
 * @InjectMock : @Mock이 붙은 객체를 @InjectMock이 붙은 객체에 주입시킬 수 있다.
 */

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EncryptionService encryptionService;

    @Mock
    AddressRepository addressRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    WishListRepository wishListRepository;

    @InjectMocks
    UserService userService;


    public User createUser(){
        return createUserDto().toEntity();
    }

    private UserDto.SaveRequest createUserDto() {
        UserDto.SaveRequest saveRequest = UserDto.SaveRequest.builder()
                .email("test123@test.com")
                .password("test1234")
                .phone("01011112222")
                .nickname("17171771")
                .build();
        return saveRequest;
    }


    private String ProductOriginImagePath = "https://TremCamp-product-origin.s3.ap-northeast-2.amazonaws.com/sample.png";
    private String ProductThumbnailImagePath = "https://TremCamp-product-thumbnail.s3.ap-northeast-2.amazonaws.com/sample.png";
    private Product createProduct(){
        return Product.builder()
                .name("텐트")
                .user(createUser())
                .productDescription("good")
                .productState(ProductState.BEST)
                .originImagePath(ProductOriginImagePath)
                .thumbnailImagePath(ProductThumbnailImagePath)
                .build();
    }

    @Test
    @DisplayName("이메일과 닉네임이 중복되지 않으면 회원가입에 성공한다.")
    void SignUp_success() throws Exception{
        UserDto.SaveRequest saveRequest = createUserDto();

        when(userRepository.existsByNickname("17171771")).thenReturn(false);
        when(userRepository.existsByEmail("test123@test.com")).thenReturn(false);

         userService.saveUser(saveRequest);

        verify(userRepository, atLeastOnce()).save(any());
    }

    @Test
    void saveUser() {

    }

    @Test
    @DisplayName("이메일 중복으로 회원가입에 실패한다.")
    void emailDuplicateCheck() {
        UserDto.SaveRequest saveRequest = createUserDto();
        when(userRepository.existsByEmail("test123@test.com")).thenReturn(true);
        Assertions.assertThrows(DuplicateEmailException.class, () -> userService.saveUser(saveRequest));
        verify(userRepository,atLeastOnce()).existsByEmail("test123@test.com");
    }

    @Test
    @DisplayName("닉네임 중복으로 회원가입에 실패한다.")
    void nicknameDuplicateCheck() {
        UserDto.SaveRequest saveRequest = createUserDto();

        when(userRepository.existsByNickname("17171771")).thenReturn(true);

        Assertions.assertThrows(DuplicateNicknameException.class, () -> userService.saveUser(saveRequest));

        verify(userRepository,atLeastOnce()).existsByNickname("17171771");
    }

    @Test
    @DisplayName("가입된 이메일 입력시 비밀번호 찾기(재설정)에 필요한 리소스를 리턴한다.")
    void getUserResource() {
        String email = "test123@test.com";
        User user = createUserDto().toEntity();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDto.FindUserResponse userResponse = userService.getUserResource(email);
        assertThat(userResponse.getEmail()).isEqualTo(user.getEmail());
        assertThat(userResponse.getPhone()).isEqualTo(user.getPhone());


    }
    @Test
    @DisplayName("존재하지 않는 이메일 입력시 비밀번호 찾기(재설정)에 필요한 리소스 리턴에 실패한다.")
    public void failToGetUserResource() {
        String email = "rere@ads.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()->
                userService.getUserResource("rere@ads.com"));

        verify(userRepository,atLeastOnce()).findByEmail(email);
    }

    @Test
    @DisplayName("비밀번호 찾기 성공 - 전달받은 객체(이메일)가 회원이라면 비밀번호 변경에 성공한다.")
    void updatePasswordByForget() {
        UserDto.ChangePasswordRequest changePasswordRequest = UserDto.ChangePasswordRequest.builder()
                .email("test123@test.com")
                .passwordAfter("test12345")
                .build();
        User user = createUserDto().toEntity();

        when(userRepository.findByEmail(changePasswordRequest.getEmail())).thenReturn(Optional.of(user));

        userService.updatePasswordByForget(changePasswordRequest);

        assertThat(user.getPassword()).isEqualTo(changePasswordRequest.getPasswordAfter());

        verify(userRepository,atLeastOnce()).findByEmail(changePasswordRequest.getEmail());
    }

    @Test
    @DisplayName("비밀번호 변경 - 이전 비밀번호와 일치하면 비밀번호 변경에 성공한다.")
    void updatePassword() {
        User user = createUserDto().toEntity();
        UserDto.ChangePasswordRequest changePasswordRequest = UserDto.ChangePasswordRequest.builder()
                .email(null)
                .passwordBefore("test123")
                .passwordAfter("test1234")
                .build();

        String email = "test123@test.com";
        String passwordBefore = encryptionService.encrypt(changePasswordRequest.getPasswordBefore());
        String passwordAfter = encryptionService.encrypt(changePasswordRequest.getPasswordAfter());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndPassword(email,passwordBefore)).thenReturn(true);

        userService.updatePassword(email,changePasswordRequest);

        assertThat(user.getPassword()).isEqualTo(passwordAfter);

        verify(userRepository, atLeastOnce()).findByEmail(email);
        verify(userRepository,atLeastOnce()).existsByEmailAndPassword(email,passwordBefore);

    }
    @Test
    @DisplayName("비밀번호 변경 - 이전 비밀번호가 일치하지 않으면 비밀번호 변경에 실패한다.")
    public void failToUpdatePassword() {
        User user = createUserDto().toEntity();
        UserDto.ChangePasswordRequest changePasswordRequest = UserDto.ChangePasswordRequest.builder()
                .email(null)
                .passwordBefore("test123")
                .passwordAfter("test1234")
                .build();

        String email = "test123@test.com";
        String passwordBefore = encryptionService.encrypt(changePasswordRequest.getPasswordBefore());


        when(userRepository.existsByEmailAndPassword(email,passwordBefore)).thenReturn(false);

        assertThrows(UnauthenticatedUserException.class, ()->
                userService.updatePassword(email,changePasswordRequest));

        verify(userRepository,atLeastOnce()).existsByEmailAndPassword(email,passwordBefore);
    }

    @Test
    @DisplayName("계좌 설정")
    void updateAccount() {
        Account account = new Account("SC","1212334","가나다");
        User user = createUserDto().toEntity();
        String email = "test123@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userService.updateAccount(email,account);

        assertThat(user.getAccount().getAccountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(user.getAccount().getBankName()).isEqualTo(account.getBankName());
        assertThat(user.getAccount().getDepositor()).isEqualTo(account.getDepositor());

        verify(userRepository, atLeastOnce()).findByEmail(email);
    }

    @Test
    @DisplayName("주소록 수정 - 주소록에 등록된 주소들 중 하나를 선택하여 수정")
    void updateAddressBook() {
        Address address =
                new Address("우리집","하늘로12","122-2033","12234");
        AddressDto.SaveRequest request = AddressDto.SaveRequest.builder()
                .id(1L)
                .addressName("new House")
                .roadAddress("가나로 123")
                .detailAddress("1동 1001호")
                .postalCode("123323")
                .build();
        when(addressRepository.findById(request.getId()))
                .thenReturn(Optional.of(address));
        userService.updateAddress(request);

        assertThat(address.getAddressName()).isEqualTo(request.getAddressName());
        assertThat(address.getRoadAddress()).isEqualTo(request.getRoadAddress());
        assertThat(address.getDetailAddress()).isEqualTo(request.getDetailAddress());
        assertThat(address.getPostalCode()).isEqualTo(request.getPostalCode());

        verify(addressRepository, atLeastOnce()).findById(any());
    }

    @Test
    @DisplayName("주소록 추가 - 올바른 주소 입력시 주소록 추가에 성공한다.")
    void addAddressBook() {
        AddressBook addressBooks =new AddressBook();
        User user = User.builder()
                .email("test123@tesst.com")
                .password("test123")
                .nickname("12121212")
                .phone("01098765432")
                .addressBooks(addressBooks)
                .build();

        AddressDto.SaveRequest request = AddressDto.SaveRequest.builder()
                .id(1L)
                .addressName("새집")
                .roadAddress("김수로 123")
                .detailAddress("101동 102호")
                .postalCode("11223")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        userService.addAddress(user.getEmail(),request);

        assertThat(user.getAddressBook().getAddressList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("주소록 조회")
    void getAddressBook() {
        AddressBook addressBook = new AddressBook();

        AddressDto.SaveRequest  request = AddressDto.SaveRequest.builder()
                .id(1L)
                .addressName("새 집")
                .roadAddress("새집로 123")
                .detailAddress("789동 123호")
                .postalCode("23456")
                .build();

        addressBook.addAddress(request.toEntity());

        User user = User.builder()
                .email("test123@test.com")
                .password("test123")
                .nickname("12121122")
                .phone("01099887766")
                .addressBooks(addressBook)
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        List<Address>addressList = userService.getAddressBook(user.getEmail());

        assertThat(addressList.size()).isEqualTo(1);
        assertThat(addressList.get(0).getAddressName()).isEqualTo(request.getAddressName());
        assertThat(addressList.get(0).getRoadAddress()).isEqualTo(request.getRoadAddress());
        assertThat(addressList.get(0).getDetailAddress()).isEqualTo(request.getDetailAddress());
        assertThat(addressList.get(0).getPostalCode()).isEqualTo(request.getPostalCode());

        verify(userRepository, atLeastOnce()).findByEmail(user.getEmail());
    }

   @Test
   @DisplayName("닉네임 변경 성공 - 중복되지 않은 닉네임을 사용하여 닉네임을 변경한지 7일이 초과되었을 경우 닉네임 변경에 성공한다.")
   void successToUpdateNickname()throws Exception{
       //given
       User user = User.builder()
               .email("test123@test.com")
               .nickname("12222222")
               .nicknameModifiedDate(LocalDateTime.now().minusDays(8))
               .build();
       String email = "test123@test.com";
       UserDto.SaveRequest saveRequest = UserDto.SaveRequest.builder()
               .nickname("1223323423")
               .build();
       String nicknameAfter = saveRequest.getNickname();

       //when
       when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
       when(userRepository.existsByNickname(nicknameAfter)).thenReturn(false);

       userService.updateNickname(email,saveRequest);

       //then
        assertThat(user.getNickname()).isEqualTo(saveRequest.getNickname());
        verify(userRepository, atLeastOnce()).findByEmail(email);
        verify(userRepository, atLeastOnce()).existsByNickname(nicknameAfter);

   }

   @Test
   @DisplayName("닉네임 중복으로 닉네임 변경에 실패한다.")
   void failToUpdateNicknameByDuplicate()throws Exception{
       //given
       User user = createUserDto().toEntity();
       String email = "test123@test.com";
       UserDto.SaveRequest request = UserDto.SaveRequest.builder()
               .nickname("ryu")
               .build();
       String nicknameAfter = request.getNickname();

       //when
       when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
       when(userRepository.existsByNickname(nicknameAfter)).thenReturn(true);

       //then
       assertThrows(DuplicateNicknameException.class, ()->
               userService.updateNickname(email,request));
       verify(userRepository,atLeastOnce()).findByEmail(email);
       verify(userRepository,atLeastOnce()).existsByNickname(nicknameAfter);


   }
   @Test
   @DisplayName("닉네인 변경 실패 - 닉네임을 변경하고 7일이 지나지 않았다면 닉네임 변경에 실패한다.")
   void failToUpdateNicknameByTerm()throws Exception{
       //given
        User user = createUserDto().toEntity();
        UserDto.SaveRequest request = UserDto.SaveRequest.builder()
                .nickname("ryu")
                .build();
        String email = "test123@test.com";
        String nicknameAfter = request.getNickname();

        //when
       when(userRepository.existsByNickname(nicknameAfter)).thenReturn(false);
       when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

       //then
        assertThrows(UnableToChangeNicknameException.class,
                ()->userService.updateNickname(email,request));
        verify(userRepository,atLeastOnce()).findByEmail(email);
        verify(userRepository,atLeastOnce()).existsByNickname(nicknameAfter);

   }

   @Test
   @DisplayName("비밀번호가 일치하여 탈퇴에 성공한다.")
   void deleteSuccess()throws Exception{
       //given
       User user = createUser();
       String email = user.getEmail();
       String password = user.getPassword();

       //when
       BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
       BDDMockito.given(userRepository.existsByEmailAndPassword(any(),any())).willReturn(true);
       userService.delete(email,password);

       //then
       verify(userRepository,atLeastOnce()).deleteByEmail(email);


   }
   
   @Test
   @DisplayName("비밀번호가 일치하지 않아 회원탈퇴에 실패한다.")
   void failDeleteMissMatchPassword()throws Exception{
       //given
       User user = createUser();
       String email = user.getEmail();
       String password = user.getPassword();

       //when
       BDDMockito.given(userRepository.findByEmail(email)).willReturn(Optional.of(user));
       BDDMockito.given(userRepository.existsByEmailAndPassword(any(),any())).willReturn(false);

       //then
       assertThrows(WrongPasswordException.class,
               () -> userService.delete(email,password));
       verify(userRepository,never()).deleteByEmail(email);
   
   }
}