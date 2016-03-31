package lab.mars.dc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Author:yaoalong.
 * Date:2016/3/31.
 * Email:yaoalong@foxmail.com
 */
public class DCConfig {
    private static final Logger LOG = LoggerFactory.getLogger(DCConfig.class);

    protected String zooKeeperServer;
    protected String myIp;
    protected Integer port;

    protected  Integer numberOfViturlNodes;
    public void parse(String path) {
        File configFile = new File(path);

        LOG.info("Reading configuration from: " + configFile);

        try {
            if (!configFile.exists()) {
                throw new IllegalArgumentException(configFile.toString()
                        + " file is missing");
            }

            Properties cfg = new Properties();
            FileInputStream in = new FileInputStream(configFile);
            try {
                cfg.load(in);
            } finally {
                in.close();
            }

            parseProperties(cfg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parseProperties(Properties properties) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString().trim();
            String value = entry.getValue().toString().trim();
            if (key.equals("zooKeeperServer")) {
                zooKeeperServer = value;
            } else if (key.equals("server")) {
                String[] parts = value.split(":");
                myIp = parts[0];
                port = Integer.valueOf(parts[1]);
            }
            else if(key.equals("numOfVirtualNodes")){
                numberOfViturlNodes=Integer.valueOf(value);
            }
        }
    }
}



