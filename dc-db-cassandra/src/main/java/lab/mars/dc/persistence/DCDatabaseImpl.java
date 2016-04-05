package lab.mars.dc.persistence;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static lab.mars.dc.exception.DCException.Code.PARAM_ERROR;
import static lab.mars.dc.exception.DCException.Code.RESOURCE_NOT_EXISTS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lab.mars.dc.exception.DCException;
import lab.mars.dc.reflection.ResourceReflection;
import lab.mars.dc.server.ResourceServiceDO;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.apache.commons.lang3.StringUtils;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class DCDatabaseImpl implements DCDatabaseService {
    private String keyspace;
    private String table;
    private String node;
    private Cluster cluster;
    private Session session;
    private boolean clean = false;

    public DCDatabaseImpl() {
        this(false, "tests", "dc", "192.168.10.124");
    }

    public DCDatabaseImpl(boolean clean, String keyspace, String table, String node) {
        this.clean = clean;
        this.keyspace = keyspace;
        this.table = table;
        this.node = node;
        connect();
    }

    public void connect() {
        cluster = Cluster.builder().addContactPoint(node).build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n",
                metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
                    host.getDatacenter(), host.getAddress(), host.getRack());
        }
        session = cluster.connect();
        if (clean) {
            session.execute("use " + keyspace + ";");
            session.execute("truncate " + table + ";");
        }
    }

    /**
     * 参数校验
     *
     * @param resourceServiceDO
     * @throws DCException
     */
    private void checkParam(ResourceServiceDO resourceServiceDO) throws DCException {

        if (resourceServiceDO == null || resourceServiceDO.getId() == null || resourceServiceDO.getData() == null) {
            throw new DCException(PARAM_ERROR);
        }
    }

    private QueryBuilder query() {
        return new QueryBuilder(cluster);
    }

    private List<ResourceServiceDO> getResourceServices(ResultSet resultSet) {
        List<ResourceServiceDO> resourceServiceDOs = new ArrayList<>();
        Map<String, Object> result = new HashMap<String, Object>();
        for (Row row : resultSet.all()) {
            ColumnDefinitions columnDefinitions = resultSet
                    .getColumnDefinitions();
            columnDefinitions.forEach(d -> {
                String name = d.getName();
                Object object = row.getObject(name);
                result.put(name, object);
            });
            resourceServiceDOs.add(ResourceReflection.deserialize(
                    ResourceServiceDO.class, result));

            result.clear();
        }
        return resourceServiceDOs;
    }

    @Override
    public ResourceServiceDO retrieve(String id) throws DCException {
        if (StringUtils.isBlank(id)) {
            throw new DCException(PARAM_ERROR);
        }
        try {
            Select.Selection selection = query().select();
            Select select = selection.from(keyspace, table);
            select.where(eq("id", id));
            select.allowFiltering();
            ResultSet resultSet = session.execute(select);
            if (resultSet == null) {
                return null;
            }
            List<ResourceServiceDO> resourceServiceDOs = getResourceServices(resultSet);
            if (resourceServiceDOs.size() == 0) {
                return null;
            }
            return resourceServiceDOs.get(0);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Long create(ResourceServiceDO resourceServiceDO) throws DCException {
        checkParam(resourceServiceDO);
        ResourceServiceDO serviceDO=retrieve(resourceServiceDO.getId());
        if(serviceDO!=null){
            throw new DCException(DCException.Code.RESOURCE_EXISTS,resourceServiceDO.getId());
        }
        try {
            Map<String, Object> map = ResourceReflection.serialize(resourceServiceDO);
            Insert insert = query().insertInto(keyspace, table);
            map.forEach(insert::value);
            session.execute(insert);
            return 1L;
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }

    }

    @Override
    public Long delete(String id) throws DCException {
        ResourceServiceDO resourceServiceDO = retrieve(id);
        if (resourceServiceDO == null) {
            throw new DCException(RESOURCE_NOT_EXISTS, id);
        }
        checkParam(resourceServiceDO);
        try {

            Statement delete = query().delete().from(keyspace, table)
                    .where(eq("id", id));
            session.execute(delete);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Long.valueOf(0);
        }
        return Long.valueOf(1);
    }

    @Override
    public Long update(String id, ResourceServiceDO resourceServiceDO) throws DCException {

        if (StringUtils.isBlank(id)) {
            throw new DCException(PARAM_ERROR);
        }
        ResourceServiceDO pre = retrieve(id);
        checkParam(pre);
        try {
            delete(id);
            pre.setData(resourceServiceDO.getData());
            create(pre);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0L;
        }
        return 1L;
    }

    @Override
    public void close() {
        if (session != null) {
            session.close();
        }
        if (cluster != null) {
            cluster.close();
        }
    }
}
