package TeamCamp.demo.domain.model.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import TeamCamp.demo.domain.model.users.user.BaseTimeEntity;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class UserBase extends BaseTimeEntity {

    @Id@GeneratedValue
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USER_EMAIL")
    protected String email;

    @Column(name = "USER_PASSWORD")
    protected String password;

    @Enumerated(EnumType.STRING)
    protected UserLevel userLevel;

    public UserBase( String email, String password,UserLevel userLevel) {
        this.email = email;
        this.password = password;
        this.userLevel = userLevel;
    }
}
