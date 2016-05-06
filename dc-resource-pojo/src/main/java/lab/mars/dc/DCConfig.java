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
    public void parse(String path) throws ConfigException {
        File configFile = new File(path);

        LOG.info("Reading configuration from: " + configFile);
        System.out.println(configFile.getAbsolutePath());
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
        }catch (IllegalArgumentException e){

            throw new ConfigException("Error processing "+path,e);
        }
        catch (Exception e) {
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
            else{
                System.setProperty("dc."+key,value);
            }

        }
        if(zooKeeperServer==null){
            throw  new IllegalArgumentException("zooKeeperServer is not set");
        }
        if(myIp==null){
            throw  new IllegalArgumentException("myIp is not set");
        }
        if(port==null){
            throw new IllegalArgumentException("port is not set");
        }
    }
    public static class ConfigException extends Exception {
        private static final long serialVersionUID = 4792316690956946187L;

        public ConfigException(String msg) {
            super(msg);
        }

        public ConfigException(String msg, Exception e) {
            super(msg, e);
        }
    }

}



