package TeamCamp.demo.service;

import TeamCamp.demo.domain.model.product.Product;
import TeamCamp.demo.domain.model.wishlist.ProductWishList;
import TeamCamp.demo.domain.model.wishlist.Wishlist;
import TeamCamp.demo.domain.repository.ProductRepository;
import TeamCamp.demo.domain.repository.ProductWishListRepository;
import TeamCamp.demo.domain.repository.WishListRepository;
import TeamCamp.demo.dto.ProductDto;
import TeamCamp.demo.exception.product.DulicateProductWishListException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import TeamCamp.demo.domain.model.users.user.Account;
import TeamCamp.demo.domain.model.users.user.User;
import TeamCamp.demo.domain.model.users.user.address.Address;
import TeamCamp.demo.domain.model.users.user.address.AddressBook;
import TeamCamp.demo.domain.model.users.user.address.AddressBookRepository;
import TeamCamp.demo.domain.repository.UserRepository;
import TeamCamp.demo.service.email.EmailCertificationService;
import TeamCamp.demo.dto.AddressBookDto;
import TeamCamp.demo.encrypt.EncryptionService;
import TeamCamp.demo.exception.user.WrongPasswordException;
import TeamCamp.demo.exception.user.DuplicateEmailException;
import TeamCamp.demo.exception.user.DuplicateNicknameException;
import TeamCamp.demo.exception.user.UnauthenticatedUserException;
import TeamCamp.demo.exception.user.UserNotFoundException;


import java.util.List;
import java.util.Set;

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
        if(!userRepository.existsByEmailAndPassword(email,encryptionService.encrypt(password))){
            throw new WrongPasswordException();
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


    public Account getAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("존재하지 않는 사용자입니다."));

        return user.getAccount();
    }

    public List<AddressBook> getAddressBook(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        return user.getAddressBook();
    }

    @Transactional
    public void addAddressBook(String email, Address address) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));

        user.addAddressBook(address);
    }

    @Transactional
    public void deleteAddressBook(AddressBookDto request) {
        Long addressBookId = request.getId();
        addressBookRepository.deleteById(addressBookId);
    }

    @Transactional
    public void updateAddressBook(AddressBookDto request) {
        Long addressBookId = request.getId();
        AddressBook addressBook = addressBookRepository.findById(addressBookId)
                .orElseThrow();

        addressBook.updateAddressBook(request);
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


    public Set<ProductDto.WishProductResponse> getWishList(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자 입니다."));

        if(user.getWishList() == null){
            Wishlist wishlist = wishListRepository.save(new Wishlist());
            user.createWishList(wishlist);
        }
        return user.getWishList();
    }

    @Transactional
    public void addWishList(String email, ProductDto.IdRequest idRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 사용자입니다."));
        if(user.getWishList() == null){
            Wishlist wishlist = wishListRepository.save(new Wishlist());
            user.createWishList(wishlist);
        }

        Product product = productRepository.findById(idRequest.getId()).orElseThrow();
        ProductWishList productWishList  = productWishListRepository.save(new ProductWishList((Wishlist) user.getWishList(),product));

        if(user.checkProductDuplicate(productWishList)){
            throw new DulicateProductWishListException("장바구니 중복");
        }
        user.addWishListProduct(productWishList);
    }

    @Transactional
    public void deleteWishList(ProductDto.IdRequest idRequest) {
        productWishListRepository.deleteById(idRequest.getId());
    }



}