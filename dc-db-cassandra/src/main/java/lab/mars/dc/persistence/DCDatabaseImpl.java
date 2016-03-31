package lab.mars.dc.persistence;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import lab.mars.dc.reflection.ResourceReflection;
import lab.mars.dc.server.ResourceServiceDO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class DCDatabaseImpl implements DCDatabaseInterface {
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
    public ResourceServiceDO retrieve(String id) {
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
    public Long create(Object object) {
        ResourceServiceDO m2mDataNode = (ResourceServiceDO) object;
        Map<String, Object> map = ResourceReflection.serialize(m2mDataNode);
        Insert insert = query().insertInto(keyspace, table);
        map.forEach(insert::value);
        session.execute(insert);
        return 1L;
    }

    @Override
    public Long delete(String id) {
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
    public Long update(String id, ResourceServiceDO resourceServiceDO) {
        try {
            ResourceServiceDO pre = retrieve(id);

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
