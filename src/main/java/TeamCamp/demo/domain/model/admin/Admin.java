package TeamCamp.demo.domain.model.admin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import TeamCamp.demo.domain.model.users.UserBase;
import TeamCamp.demo.domain.model.users.UserLevel;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends UserBase {

    public Admin(String email, String password, UserLevel userLevel) {
        this.email = email;
        this.password =password;
        this.userLevel = userLevel;
    }
}
