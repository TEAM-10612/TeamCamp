package TeamCamp.demo.domain.model.users.user;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Getter
@MappedSuperclass //테이블과 관계가 없고, 단순히 엔티티가 공통으로 사용하는 매핑 정보를 모으는 역할을 한다.
//단순히 부모 클래스를 상속 받는 자식 클래스에 매핑 정보만 제공한다.
public abstract class BaseTimeEntity {

    @CreatedDate //생성시간
    @Column(updatable = false) //insertable=false는 insert 시점에 막는 것이고, updatable는 update 시점에 막는 기능입니다.
    private LocalDateTime localDateTime;

    @LastModifiedDate //변경시간
    private LocalDateTime modifiedTime;
}
