package TeamCamp.demo.test;

import org.apache.poi.ss.formula.ptg.MemErrPtg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
}
