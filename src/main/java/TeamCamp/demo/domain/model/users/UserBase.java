package TeamCamp.demo.domain.model.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(unique = true)
    protected String email;


    protected String password;

    @Enumerated(EnumType.STRING)
    protected UserLevel userLevel;

}
