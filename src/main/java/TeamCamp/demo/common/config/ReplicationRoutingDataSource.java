package TeamCamp.demo.common.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {

    private CircularList<String> dataSourceList;
    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);
        dataSourceList = new CircularList<>(
                targetDataSources.keySet()
                        .stream()
                        .filter(key -> key.toString().contains("slave"))
                        .map(key -> key.toString())
                        .collect(toList()));
    }

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        if (isReadOnly){
            return dataSourceList.getOne();
        }else{
            return "master";
        }
    }
}
