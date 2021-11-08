package TeamCamp.demo.common.database;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

/**
 * 여러개의 Replication DB의 DataSource 를 순서대로 로드밸런싱 하기 위해 사용하는 클래스
 * @param <T>
 */
public class ReplicationRoutingCircularList<T>{
    private List<T>list;
    private static Integer counter = 0;

    public ReplicationRoutingCircularList(List<T> list){
        this.list = list;
    }

    public T getOne(){
        int circularSize = list.size();
        if(counter +1 > circularSize){
            counter = 0;
        }
        return list.get(counter++ % circularSize);
    }
}
