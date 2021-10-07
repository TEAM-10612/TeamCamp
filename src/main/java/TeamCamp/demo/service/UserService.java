package TeamCamp.demo.service;

import TeamCamp.demo.domain.repository.*;
import TeamCamp.demo.exception.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import TeamCamp.demo.domain.model.users.user.Account;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.domain.model.users.user.address.Address;
import TeamCamp.demo.domain.model.users.user.address.AddressBook;
import TeamCamp.demo.domain.repository.AddressBookRepository;
import TeamCamp.demo.service.email.EmailCertificationService;
import TeamCamp.demo.dto.AddressDto;
import TeamCamp.demo.encrypt.EncryptionService;


import java.util.List;

import static TeamCamp.demo.dto.UserDto.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final AddressBookRepository addressBookRepository;
    private final EmailCertificationService emailCertificationService;
    private final ProductRepository productRepository;
    private final WishListRepository wishListRepository;
    private final ProductWishListRepository productWishListRepository;
    private final AddressRepository addressRepository;
    private final TradeService tradeService;
    //데이터 조회용. 추후 삭제
    public List<User> findAll() {
        return userRepository.findAll();
    }

    //이메일 중복과 닉네임 중복 exception 분리하여 예외의 원인을 정확히 파악하도록 구현
    @Transactional
    public void saveUser(SaveRequest userDto){
        if(emailDuplicateCheck(userDto.getEmail())){
            throw new DuplicateEmailException();
        }

        if (nicknameDuplicateCheck(userDto.getNickname())) {
            throw new DuplicateNicknameException();
        }
        userDto.passwordEncryption(encryptionService);

        User user = userRepository.save(userDto.toEntity());

    }

    /**
     * 이메일 체크
     * @param email
     * @return
     */
    public boolean emailDuplicateCheck(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 닉네임 체크
     * @param nickname
     * @return
     */
    public boolean nicknameDuplicateCheck(String nickname) {
        return userRepository.existsByNickname(nickname);
    }


    public FindUserResponse getUserResource(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 email 입니다.")).toFindUserDto();
    }

    /**
     * 비밀 번호 변경
     * @param requestDto
     */
    @Transactional
    public void updatePasswordByForget(ChangePasswordRequest requestDto){
        String email = requestDto.getEmail();
        requestDto.passwordEncryption(encryptionService);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        user.updatePassword(requestDto.getPasswordAfter());
    }
    @Transactional
    public void updatePassword(String email , ChangePasswordRequest request){
        request.passwordEncryption(encryptionService);
        String passwordBefore = request.getPasswordBefore();
        String passwordAfter = request.getPasswordAfter();
        if(!userRepository.existsByEmailAndPassword(email,passwordBefore)){
            throw new UnauthenticatedUserException("이전 비밀번호가 일치하지 않습니다.");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UnauthenticatedUserException("Unauthenticated user"));

        user.updatePassword(passwordAfter);
    }


    @Transactional
    public void delete(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));
        if(!userRepository.existsByEmailAndPassword(email,encryptionService.encrypt(password))){
            throw new WrongPasswordException();
        }
        if(tradeService.hasUserProgressingTrade(user)){
            throw new HasProgressingTradeException("진행중인 거래 완료 후 탈퇴가 가능합니다.");
        }

        if (user.hasRemainPoints()){
            throw new HasRemainingPointException("남아있는 포인트가 존재하여 탈퇴가 불가능합니다.");
        }

        userRepository.deleteByEmail(email);
    }

    public void validToken(String token,String email){
        emailCertificationService.verifyEmail(token,email);
    }


    @Transactional
    public void updateEmailVerified(String token, String email) {
        validToken(token,email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthenticatedUserException("존재하지 않는 사용자입니다."));
        user.updateUserLevel();
    }

    @Transactional
    public void updateAccount(String email, Account account){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->new UserNotFoundException("존재하지 않는 사용자입니다."));
        user.updateAccount(account);
    }

    @Transactional(readOnly = true)
    public Account getAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("존재하지 않는 사용자입니다."));

        return user.getAccount();
    }

    @Transactional(readOnly = true)
    public List<Address> getAddressBook(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        AddressBook addressBook = user.getAddressBook();
        return addressBook.getAddressList();
    }

    @Transactional
    public void addAddress(String email, AddressDto.SaveRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        if(user.getAddressBook() == null){
            AddressBook addressBook = addressBookRepository.save(new AddressBook());

            user.createAddressBook(addressBook);
        }
        user.addAddress(request.toEntity());
    }

    @Transactional
    public void deleteAddress(String email, AddressDto.IdRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        Long addressBookId = request.getId();
        Address address = addressRepository.findById(addressBookId).orElseThrow();
        user.deleteAddress(address);
    }

    @Transactional
    public void updateAddress(AddressDto.SaveRequest request) {

        Long addressId = request.getId();
        Address address = addressRepository.findById(addressId).orElseThrow();
        address.updateAddress(request);
    }
    @Transactional
    public void updateNickname(String email, SaveRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if(nicknameDuplicateCheck(request.getNickname())){
            throw new DuplicateNicknameException();
        }
        user.updateNickname(request);
    }

}