package project.campshare.domain.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import project.campshare.domain.model.users.user.Account;
import project.campshare.domain.model.users.user.User;
import project.campshare.dto.AddressBookDto;
import project.campshare.dto.UserDto;
import project.campshare.dto.UserDto.ChangePasswordRequest;
import project.campshare.dto.UserDto.SaveRequest;
import project.campshare.domain.model.users.user.address.Address;
import project.campshare.domain.model.users.user.address.AddressBook;
import project.campshare.domain.model.users.user.address.AddressBookRepository;
import project.campshare.domain.repository.UserRepository;
import project.campshare.encrypt.EncryptionService;
import project.campshare.exception.user.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;

    @Mock
    AddressBookRepository addressBookRepository;

    @Mock
    EncryptionService encryptionService;

    private SaveRequest createUserDto(){
        SaveRequest saveRequest = SaveRequest.builder()
                .email("rdj10149@gmail.com")
                .nickname("ryu")
                .phone("01000001111")
                .password("ryu11")
                .build();

        return saveRequest;
    }

    @Test
    @DisplayName("이메일과 닉네임이 중복되지 않으면 회원가입에 성공")
    void signUp_Success()throws Exception{
        SaveRequest saveRequest =  createUserDto();
        when(userRepository.existsByEmail("rdj10149@gmail.com")).thenReturn(false);
        when(userRepository.existsByNickname("ryu")).thenReturn(false);

        userService.saveUser(saveRequest);
        //userRepository에서 save를 한번이상 호출 했는지 체크
        verify(userRepository, Mockito.atLeastOnce()).save(any());

    }

    @Test
    @DisplayName("이메일 중복으로 인해 가입 실패")
    void emailDuplicate()throws Exception{
        //given
        SaveRequest saveRequest = createUserDto();
        //when
        when(userRepository.existsByEmail("rdj10149@gmail.com")).thenReturn(true);
        //then
        Assertions.assertThrows(DuplicateEmailException.class, () -> userService.saveUser(saveRequest));
        verify(userRepository, Mockito.atLeastOnce()).existsByEmail("rdj10149@gmail.com");
    }

    @Test
    @DisplayName("닉네임 중복으로 인해 가입 실패")
    void nicknameDuplicate()throws Exception{
        //given
        SaveRequest saveRequest = createUserDto();
        //when
        when(userRepository.existsByNickname("ryu")).thenReturn(true);
        //then
        Assertions.assertThrows(DuplicateNicknameException.class , ()->userService.saveUser(saveRequest));
        verify(userRepository, Mockito.atLeastOnce()).existsByNickname("ryu");


    }


    /**
     * 비밀번호 찾기 관련 테스트 코드
     */
    @Test
    @DisplayName("비밀번호 찾기 성공, 전달받은 객체가 회원이라면 비밀번호 변경에 성공한다.")
    void findPassword()throws Exception{
        //given
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .email("rdj10149@gmail.com")
                .passwordAfter("ryu11")
                .build();
        User user = createUserDto().toEntity();

        //when
        when(userRepository.findByEmail(changePasswordRequest.getEmail())).thenReturn(Optional.of(user));
        userService.updatePasswordByForget(changePasswordRequest);

        //then
        assertThat(user.getPassword()).isEqualTo(changePasswordRequest.getPasswordAfter());
        verify(userRepository, Mockito.atLeastOnce()).findByEmail(changePasswordRequest.getEmail());


    }

    @Test
    @DisplayName("가입된 이메일 입력시 비밀번호 찾기에 필요한 리소스 반환")
    void SuccessToGetUserResource()throws Exception{
        //given
        String email = "rdj10149@gmail.com";
        User user = createUserDto().toEntity();


        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        UserDto.FindUserResponse userResource = userService.getUserResource(email);

        //then
        assertThat(userResource.getEmail()).isEqualTo(user.getEmail());
        assertThat(userResource.getPhone()).isEqualTo(user.getPhone());



    }


    @Test
    @DisplayName("존재하지 않는 이메일 입력시 비밀번호 찾기에 필요한 리소스 리턴에 실패한다.")
    void failToGetUserResource()throws Exception{
        //given
        String email = "rdj10149@gmail.com";
        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(UserNotFoundException.class,()-> userService.getUserResource("rdj10149@gmail.com"));
        verify(userRepository, Mockito.atLeastOnce()).findByEmail(email);


    }


    @Test
    @DisplayName("비밀번호가 일치하지 않아 회원 탈퇴 실패")
    void deleteFailure()throws Exception{
        //given
        SaveRequest saveRequest = createUserDto();
        String email = saveRequest.getEmail();
        String password = saveRequest.getPassword();

        //when
        when(userRepository.existsByEmailAndPassword(email,encryptionService.encrypt(password))).thenReturn(false);
        //then

        Assertions.assertThrows(WrongPasswordException.class, ()->userService.delete(email,password));
        verify(userRepository,never()).deleteByEmail(email);


    }

    @Test
    @DisplayName("비밀번호가 일치해 회원 탈퇴 성공")
    void deleteSuccess()throws Exception{
        //given
        SaveRequest saveRequest = createUserDto();
        String email = saveRequest.getEmail();
        String password = saveRequest.getPassword();

        //when
        when(userRepository.existsByEmailAndPassword(email,encryptionService.encrypt(password))).thenReturn(true);
        userService.delete(email,password);

        //then
        verify(userRepository, Mockito.atLeastOnce()).deleteByEmail(email);


    }


    @Test
    @DisplayName("비밀번호 변경 - 이전 비밀번호와 일치하면 비밀번호 변경에 성공한다.")
    void updatePassword()throws Exception{
        //given
        User user = createUserDto().toEntity();

        ChangePasswordRequest changePasswordRequest
                = ChangePasswordRequest.builder()
                .email(null)
                .passwordBefore("ryu1014")
                .passwordAfter("ryu11")
                .build();

        String email = "rdj10149@gmail.com";

        String passwordBefore = encryptionService
                .encrypt(changePasswordRequest.getPasswordBefore());

        String passwordAfter =  encryptionService
                .encrypt(changePasswordRequest.getPasswordAfter());

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndPassword(email,passwordBefore)).thenReturn(true);

        userService.updatePassword(email,changePasswordRequest);


        //then

        assertThat(user.getPassword()).isEqualTo(passwordAfter);
        verify(userRepository,atLeastOnce()).findByEmail(email);
        verify(userRepository,atLeastOnce()).existsByEmailAndPassword(email,passwordBefore);

    }
    @Test
    @DisplayName("비밀번호 변경 - 이전 비밀번호가 일치하지 않으면 비밀번호 변경에 실패한다.")
    void failUpdatePassword()throws Exception{
        //given
        ChangePasswordRequest changePasswordRequest = ChangePasswordRequest.builder()
                .email(null)
                .passwordBefore("ryu1014")
                .passwordAfter("ryu11")
                .build();
        String email = "rdj10149@gmail.com";
        String passwordBefore = encryptionService.encrypt(changePasswordRequest.getPasswordBefore());

        //when
        when(userRepository.existsByEmailAndPassword(email,passwordBefore)).thenReturn(false);

        //then
        Assertions.assertThrows(UnauthenticatedUserException.class, () ->userService.updatePassword(email,changePasswordRequest));
        verify(userRepository,atLeastOnce()).existsByEmailAndPassword(email,passwordBefore);


    }

    @Test
    @DisplayName("계좌 설정 - 환급받을 계좌 번호 설정 성공")
    void updateAccount()throws Exception{
        //given
        Account account = new Account("SC제일은행","010021013","동자이");
        User user = createUserDto().toEntity();
        String email = "rdj10149@gmail.com";

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        userService.updateAccount(email,account);
        //then
        assertThat(user.getAccount().getAccountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(user.getAccount().getBankName()).isEqualTo(account.getBankName());
        assertThat(user.getAccount().getDepositor()).isEqualTo(account.getDepositor());

        verify(userRepository, atLeastOnce()).findByEmail(email);
    }
    @Test
    @DisplayName("유저 주소록 조회")
    void getAddressBooks()throws Exception{
        //given
        ArrayList<AddressBook> addressBooks = new ArrayList<>();
        User user = User.builder()
                .email("rdj10149@gmail.com")
                .password("ryu11")
                .nickname("ryu")
                .phone("01011110000")
                .addressBooks(addressBooks)
                .build();
        String email = "rdj1014@gmail.com";
        Address address1 = new Address("house1","street1","1","1001");
        Address address2 = new Address("house2","street2","2","1002");
        Address address3 = new Address("house3","street3","3","1003");

        //when
        user.addAddressBook(address1);
        user.addAddressBook(address2);
        user.addAddressBook(address3);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        List<AddressBook> addressBookList = userService.getAddressBook(email);
        //then
        assertThat(addressBookList.size()).isEqualTo(3);
        verify(userRepository, atLeastOnce()).findByEmail(email);


    }

    @Test
    @DisplayName("주소록 수정- 주소록에 등록된 주소들 중 하나를 선택하여 수정한다.")
    void updateAddressBook()throws Exception{
        //given
        Address address =new Address("seoul","guro","103","111");
        AddressBook addressBook = new AddressBook(address);
        AddressBookDto addressBookDto =new  AddressBookDto(2L,"seoul","gangnam","222","123");
        //when
        when(addressBookRepository.findById(addressBookDto.getId()))
                .thenReturn(Optional.of(addressBook));
        userService.updateAddressBook(addressBookDto);
        //then
        assertThat(addressBook.getAddress().getAddressName())
                .isEqualTo(addressBookDto.getAddressName());

        assertThat(addressBook.getAddress().getDetailAddress())
                .isEqualTo(addressBookDto.getDetailAddress());

        assertThat(addressBook.getAddress().getRoadAddress())
                .isEqualTo(addressBookDto.getRoadNameAddress());

        assertThat(addressBook.getAddress().getPostalCode())
                .isEqualTo(addressBookDto.getPostalCode());
        verify(addressBookRepository, atLeastOnce()).findById(any());

    }

    @Test
    @DisplayName("주소록 추가 - 올바른 주소 입력시 주소록 추가에 성공한다.")
    void addAddressBook()throws Exception{
        //given
        ArrayList<AddressBook> addressBooks = new ArrayList<>();
        User user = User.builder()
                .email("rdj10149@gmail.com")
                .password("ryu11")
                .nickname("ryu")
                .phone("01000001111")
                .addressBooks(addressBooks)
                .build();

        String email = "rdj10149@gmail.com";
        Address address1 = new Address("house1","street1","1","1001");
        Address address2 = new Address("house2","street2","2","1002");
        Address address3 = new Address("house3","street3","3","1003");

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        userService.addAddressBook(email,address1);
        userService.addAddressBook(email,address2);
        userService.addAddressBook(email,address3);

        //then
        assertThat(user.getAddressBook().size()).isEqualTo(3);

    }

    @Test
    @DisplayName("중복되지 않는 닉네임을 사용하며 변경한지 7일이 초과되었을 경우 닉네암 변경 성공")
    void UpdateNickname()throws Exception{
        //given
        User user = User.builder()
                .nickname("ryu11")
                .email("rdj10149@gmail.com")
                .nicknameModifiedDate(LocalDateTime.now().minusDays(8))
                .build();
        String email = "rdj1014@gmail.com";
        SaveRequest request = SaveRequest.builder()
                .nickname("ryu")
                .build();

        String nicknameAfter = request.getNickname();

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname(nicknameAfter)).thenReturn(false);
        userService.updateNickname(email,request);

        //then
        assertThat(user.getNickname()).isEqualTo(request.getNickname());
        verify(userRepository,atLeastOnce()).findByEmail(email);
        verify(userRepository, atLeastOnce()).existsByNickname(nicknameAfter);


    }

    @Test
    @DisplayName("닉네임 중복으로 변경 실패")
    void failToUpdateNicknameByDuplicate()throws Exception{
        //given
        User user = createUserDto().toEntity();
        String email = "rdj10149@gmail.com";
        SaveRequest request = SaveRequest.builder()
                .nickname("ryu")
                .build();

        String nicknameAfter = request.getNickname();

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname(nicknameAfter)).thenReturn(true);

        //then
        Assertions.assertThrows(DuplicateNicknameException.class,
                ()-> userService.updateNickname(email,request));
        verify(userRepository, atLeastOnce()).existsByNickname(nicknameAfter);
        verify(userRepository, atLeastOnce()).findByEmail(email);

    }


    @Test
    @DisplayName("닉네임 변경 후 7일이 지나지 않아 닉네임 변경 실패")
    void failToUpdateNicknameByTerm()throws Exception{
        //given
        User user  = createUserDto().toEntity();
        SaveRequest request = SaveRequest.builder()
                .nickname("ryu").build();
        String email = "rdj10149@gmail.com";
        String nicknameAfter = request.getNickname();
        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.existsByNickname(nicknameAfter)).thenReturn(false);
        //then
        Assertions.assertThrows(UnableToChangeNicknameException.class,
                () -> userService.updateNickname(email,request));
        verify(userRepository,atLeastOnce()).findByEmail(email);
        verify(userRepository,atLeastOnce()).existsByNickname(nicknameAfter);

    }

}