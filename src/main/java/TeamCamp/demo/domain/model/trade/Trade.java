package TeamCamp.demo.domain.model.trade;

import TeamCamp.demo.domain.model.users.BaseTimeEntity;
import TeamCamp.demo.domain.model.users.User;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Trade extends BaseTimeEntity {

    @Id@GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PUBLISHER_ID")
    private User publisher;

    private User seller;

    private User buyer;


}
