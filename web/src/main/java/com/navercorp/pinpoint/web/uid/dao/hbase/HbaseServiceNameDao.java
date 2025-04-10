package com.navercorp.pinpoint.web.uid.dao.hbase;

import com.navercorp.pinpoint.common.hbase.HbaseColumnFamily;
import com.navercorp.pinpoint.common.hbase.HbaseOperations;
import com.navercorp.pinpoint.common.hbase.HbaseTables;
import com.navercorp.pinpoint.common.hbase.RowMapper;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.common.server.uid.ServiceUid;
import com.navercorp.pinpoint.web.uid.dao.ServiceNameDao;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.CheckAndMutate;
import org.apache.hadoop.hbase.client.CheckAndMutateResult;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.Objects;

// serviceUid -> serviceName
@Repository
@ConditionalOnProperty(name = "pinpoint.web.v4.enable", havingValue = "true")
public class HbaseServiceNameDao implements ServiceNameDao {

    private static final HbaseColumnFamily NAME = HbaseTables.SERVICE_NAME;

    private final HbaseOperations hbaseOperations;
    private final TableNameProvider tableNameProvider;

    private final RowMapper<String> serviceNameMapper;

    public HbaseServiceNameDao(HbaseOperations hbaseOperations, TableNameProvider tableNameProvider,
                               @Qualifier("serviceNameMapper") RowMapper<String> serviceNameMapper) {
        this.hbaseOperations = Objects.requireNonNull(hbaseOperations, "hbaseOperations");
        this.tableNameProvider = Objects.requireNonNull(tableNameProvider, "tableNameProvider");
        this.serviceNameMapper = Objects.requireNonNull(serviceNameMapper, "serviceNameMapper");
    }

    @Override
    public String selectServiceName(ServiceUid serviceUid) {
        byte[] rowKey = Bytes.toBytes(serviceUid.getUid());

        Get get = new Get(rowKey);
        get.addFamily(NAME.getName());

        TableName serviceInfoTableName = tableNameProvider.getTableName(NAME.getTable());

        return hbaseOperations.get(serviceInfoTableName, get, serviceNameMapper);
    }

    @Override
    public boolean insertServiceNameIfNotExists(ServiceUid serviceUid, String serviceName) {
        byte[] rowKey = Bytes.toBytes(serviceUid.getUid());

        Put put = new Put(rowKey);
        put.addColumn(NAME.getName(), NAME.getName(), Bytes.toBytes(serviceName));

        CheckAndMutate.Builder builder = CheckAndMutate.newBuilder(rowKey);
        builder.ifNotExists(NAME.getName(), NAME.getName());
        CheckAndMutate checkAndMutate = builder.build(put);

        TableName serviceInfoTableName = tableNameProvider.getTableName(NAME.getTable());
        CheckAndMutateResult checkAndMutateResult = hbaseOperations.checkAndMutate(serviceInfoTableName, checkAndMutate);
        return checkAndMutateResult.isSuccess();
    }

    @Override
    public void deleteServiceName(ServiceUid serviceUid) {
        byte[] rowKey = Bytes.toBytes(serviceUid.getUid());
        Delete delete = new Delete(rowKey);

        TableName ServiceInfoTableName = tableNameProvider.getTableName(NAME.getTable());
        hbaseOperations.delete(ServiceInfoTableName, delete);
    }

}
