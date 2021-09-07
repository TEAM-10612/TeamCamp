package TeamCamp.demo.controller;



import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import TeamCamp.demo.service.loginservice.userlogin.SessionLoginService;
import TeamCamp.demo.domain.model.users.user.Account;
import TeamCamp.demo.domain.model.users.user.address.Address;
import TeamCamp.demo.domain.model.users.user.address.AddressBook;
import TeamCamp.demo.common.annotation.CurrentUser;
import TeamCamp.demo.common.annotation.LoginCheck;
import TeamCamp.demo.service.UserService;
import TeamCamp.demo.service.email.EmailCertificationService;
import TeamCamp.demo.service.sms.SmsCertificationService;
import TeamCamp.demo.dto.AddressBookDto;

import javax.validation.Valid;

import java.util.List;

import static TeamCamp.demo.dto.UserDto.*;
import static TeamCamp.demo.util.ResponseConstants.CREATED;
import static TeamCamp.demo.util.ResponseConstants.OK;

/**
 * 휴대폰 인증시 전송받은 인증번호를 session에 저장하여 User가 입력한 인증번호화 일치하는지 확인 인증번호 일치 여부에 따라 200 또는 400 반환 회원가입 완료 후
 * 마이페이지로 이동할 수 있는 URI를 HEADER의 Location에 제공하기 위해 HATEOAS 사용
 *
 */

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserApiController {

    private final SessionLoginService sessionLoginService;

    private final UserService userService;

    private final SmsCertificationService smsCertificationService;

    private final EmailCertificationService emailCertificationService;


    /**
     * 이메일 검증
     * @param email
     * @return
     */
    @GetMapping("/user-emails/{email}/exists")
    public ResponseEntity<Boolean> checkEmailDuplicate(@PathVariable String email) {
        return ResponseEntity.ok(userService.emailDuplicateCheck(email));
    }

    /**
     * 닉네임 검증
     * @param nickname
     * @return
     */
    @GetMapping("/user-nicknames/{nickname}/exists")
    public ResponseEntity<Boolean> checkNicknameDuplicate(@PathVariable String nickname) {
        return ResponseEntity.ok(userService.nicknameDuplicateCheck(nickname));
    }

    /**
     * 유저 가입
     * @param requestDto
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> createUser(@Valid @RequestBody SaveRequest requestDto) {
        userService.saveUser(requestDto);
        emailCertificationService.sendEmailForEmailCheck(requestDto.getEmail());
        return CREATED;
    }

    @GetMapping("/email-check-token")
    public void emailCheck(String token ,String email){
        userService.updateEmailVerified(token,email);
    }

    /**
     * 인증번호 전송
     * @param requestDto
     * @return
     */
    @PostMapping("/sms-certification/sends")
    public ResponseEntity<Void> sendSms(@RequestBody SmsCertificationRequest requestDto) {
        smsCertificationService.sendSms(requestDto.getPhone());
        return CREATED;
    }

    @PostMapping("/resend-email-token")
    public void resendEmailCheck(@CurrentUser String email){
        emailCertificationService.sendEmailForCertification(email);
    }

    /**
     * 인증번호 확인
     * @param requestDto
     * @return
     */
    @PostMapping("/sms-certification/confirms")
    public ResponseEntity<Void> SmsVerification(@RequestBody SmsCertificationRequest requestDto) {
       smsCertificationService.verifySms(requestDto);
        return OK;
    }

    //비밀번호 찾기 : 이메일 입력시, 존재하는 이메일이면 휴대폰인증과 이메일인증 중 택1 하도록 구현
    //휴대폰 인증 선택시 : sendSms / SmsVerification 핸들러
    //이메일 인증 선택시 : sendEmail / emailVerification 핸들러
    //이메일로 비밀번호 찾기
    @GetMapping("/find/{email}")
    public ResponseEntity<FindUserResponse> forgot_password(@PathVariable String email){
        FindUserResponse findUserResponse= userService.getUserResource(email);
        return ResponseEntity.ok(findUserResponse);
    }

    //이메일로 인증번호 전송
    @PostMapping("/email-certification/sends")
    public ResponseEntity sendEmail(@RequestBody EmailCertificationRequest request){
        emailCertificationService.sendEmailForCertification(request.getEmail());
        return CREATED;
    }

    //이메일 인증
    @PostMapping("/email-certification/confirms")
    public void emailVerification(@RequestBody EmailCertificationRequest request){
        emailCertificationService.verifyEmail(request);
    }

    //비밀번호 변경(로그인 안한 상태)
    @PatchMapping("/forgot/password")
    public void changePassword(
            @Valid @RequestBody ChangePasswordRequest request){
        userService.updatePasswordByForget(request);
    }
    //정보수정에서 비밀번호 변경
    @PatchMapping("/password")
    public void changePassword(@CurrentUser String email,
                                         @Valid@RequestBody ChangePasswordRequest request){
        userService.updatePassword(email,request);
    }

    /**
     * 로그인
     * @param loginRequest
     * @return
     */
    @PostMapping("/login")
    public void login(@RequestBody LoginRequest loginRequest) {
        sessionLoginService.login(loginRequest);
    }

    /**
     * 로그 아웃
     * @return
     */
    @LoginCheck
    @DeleteMapping("/logout")
    public void logout() {
        sessionLoginService.logout();
    }
    /**
     * 내 정보
     * @param email
     * @return
     */
    @LoginCheck
    @GetMapping("/my-infos")
    public ResponseEntity<UserInfoDto> myPage(@CurrentUser String email) {
        UserInfoDto loginUser = sessionLoginService.getCurrentUser(email);
        return ResponseEntity.ok(loginUser);
    }

    @DeleteMapping
    @LoginCheck
    public ResponseEntity<Void>UserRemove(@RequestBody PasswordRequest requestDto,
                                          @CurrentUser String email){
        String password = requestDto.getPassword();
        userService.delete(email,password);
        sessionLoginService.logout();
        return OK;
    }

    /**
     * 환급 계좌 등록
     * @param email
     * @param account
     * @return
     */
    @LoginCheck
    @PatchMapping("/account")
    public void changeAccount(@CurrentUser String email,@RequestBody Account account){
        userService.updateAccount(email,account);

    }

    @LoginCheck
    @GetMapping("/account")
    public ResponseEntity<Account> getAccountResource(@CurrentUser String email){
        Account account = userService.getAccount(email);
        return OK;
    }

    @LoginCheck
    @PostMapping("/addressBook")
    public void addAddressBook(@CurrentUser String email, @RequestBody Address address){
        userService.addAddressBook(email,address);
    }

    @LoginCheck
    @GetMapping("/addressBook")
    public ResponseEntity<List<AddressBook>> getAddressBookResource(@CurrentUser String email){
        List<AddressBook> addressBook = userService.getAddressBook(email);
        return OK;
    }

    @LoginCheck
    @DeleteMapping("/addressBook")
    public void deleteAddressBook(@RequestBody AddressBookDto request){
        userService.deleteAddressBook(request);
    }

    @LoginCheck
    @PatchMapping("/addressBook")
    public void updateAddressBook(@RequestBody AddressBookDto request){
        userService.updateAddressBook(request);
    }

    @PatchMapping("/nickname")
    public void updateNickname(@CurrentUser String email,@RequestBody SaveRequest request){
        userService.updateNickname(email,request);

    }
}