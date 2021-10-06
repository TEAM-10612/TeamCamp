package TeamCamp.demo.domain.model.point;

import TeamCamp.demo.domain.model.users.BaseTimeEntity;
import TeamCamp.demo.domain.model.users.User;
import TeamCamp.demo.dto.PointDto;
import TeamCamp.demo.dto.PointDto.PointHistoryDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Point extends BaseTimeEntity {
    @Id@GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @Enumerated(EnumType.STRING)
    private PointDivision division;

    private Long amount;

    @Builder
    public Point(Long id, User user, PointDivision division, Long amount) {
        this.id = id;
        this.user = user;
        this.division = division;
        this.amount = amount;
    }


    public PointHistoryDto toPointHistoryDto(){
        return PointHistoryDto.builder()
                .amount(this.amount)
                .division(this.division)
                .time(this.getCreateDate())
                .build();
    }
}
