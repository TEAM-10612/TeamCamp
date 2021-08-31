package TeamCamp.demo.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import TeamCamp.demo.domain.model.users.user.Account;
import TeamCamp.demo.domain.model.users.user.User;
import TeamCamp.demo.domain.model.users.UserLevel;
import TeamCamp.demo.domain.model.users.user.address.AddressBook;
import TeamCamp.demo.encrypt.EncryptionService;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserDto {
    @Getter
    @NoArgsConstructor
    public static class SaveRequest {
        @Email
        @NotBlank
        private String email;

        @NotBlank
        @Length(min = 8, max = 50)
        private String password;

        @NotBlank
        @Length(min = 3, max = 20)
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{3,20}$")
        private String nickname;

        @NotBlank
        @Length(min = 10, max = 11)
        private String phone;

        public void passwordEncryption(EncryptionService encryptionService) {
            this.password = encryptionService.encrypt(password);
        }
        @Builder
        public SaveRequest(String email,String password,String confirmPassword,String nickname, String phone) {
            this.email = email;
            this.password = password;
            this.nickname = nickname;
            this.phone = phone;
        }

        public User toEntity() {
            return User.builder()
                    .email(this.email)
                    .password(this.password)
                    .nicknameModifiedDate(LocalDateTime.now())
                    .nickname(this.nickname)
                    .phone(this.phone)
                    .userLevel(UserLevel.UNAUTH)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class SmsCertificationRequest {
        private String phone;
        private String certificationNumber;

        public SmsCertificationRequest(String phone, String certificationNumber) {
            this.phone = phone;
            this.certificationNumber = certificationNumber;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class EmailCertificationRequest{
        private String email;
        private String certificationNumber;

        @Builder
        public EmailCertificationRequest(String email, String certificationNumber) {
            this.email = email;
            this.certificationNumber = certificationNumber;
        }
    }


    @Getter
    @NoArgsConstructor
    public static class LoginRequest {

        private String email;
        private String password;

        @Builder
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public void passwordEncryption(EncryptionService encryptionService) {
            this.password = encryptionService.encrypt(password);
        }
    }
    @Getter
    @NoArgsConstructor
    @Builder
    public static class UserInfoDto {

        private String email;
        private String nickname;
        private String phone;
        private List<AddressBook> addressBooks;
        private Account account;
        private UserLevel userLevel;

        @Builder
        public UserInfoDto(String email, String nickname, String phone, List<AddressBook> addressBooks, Account account, UserLevel userLevel) {
            this.email = email;
            this.nickname = nickname;
            this.phone = phone;
            this.addressBooks = addressBooks;
            this.account = account;
            this.userLevel = userLevel;
        }
    }

    @Getter
    public static class FindUserResponse{
        private String email;
        private String phone;

        @Builder
        public FindUserResponse(String email, String phone) {
            this.email = email;
            this.phone = phone;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class ChangePasswordRequest{
        private String email;

        @NotBlank(message = "비밀번호를 입력하세요.")
        @Size(min = 8,max = 20,message = "비밀번호는 8자 이상 20자 이하로 입력하세요. ")
        private String passwordAfter;
        private String passwordBefore;

        public void passwordEncryption(EncryptionService encryptionService){
            this.passwordAfter = encryptionService.encrypt(passwordAfter);
            this.passwordBefore = encryptionService.encrypt(passwordBefore);
        }
        @Builder
        public ChangePasswordRequest(String email,
                                     @NotBlank(message = "비밀번호를 입력하세요.") @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력하세요. ") String passwordAfter,
                                     String passwordBefore) {
            this.email = email;
            this.passwordAfter = passwordAfter;
            this.passwordBefore = passwordBefore;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class PasswordRequest{
        private String password;

        @Builder
        public PasswordRequest(String password) {
            this.password = password;
        }
    }


    @Getter
    @NoArgsConstructor
    public static class UserListResponse{
        private Long id;
        private String email;
        private UserLevel userLevel;

        @Builder
        public UserListResponse(Long id, String email, UserLevel userLevel) {
            this.id = id;
            this.email = email;
            this.userLevel = userLevel;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserSearchCondition{
        private Long id;
        private String email;
        private UserLevel userLevel;

        @Builder
        public UserSearchCondition(Long id, String email, UserLevel userLevel) {
            this.id = id;
            this.email = email;
            this.userLevel = userLevel;
        }
    }



}
