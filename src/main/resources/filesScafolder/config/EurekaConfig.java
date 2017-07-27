package com.thomson.ls.sb.kpa.sb_retrieve_units_ms.config;

import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.AmazonInfo.MetaDataKey;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.DataCenterInfo.Name;
import com.netflix.appinfo.InstanceInfo.InstanceStatus;
import com.netflix.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtils.HostInfo;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Configuration
public class EurekaConfig {

	private static final Logger log = LoggerFactory.getLogger(com.thomson.ls.sb.kpa.sb_retrieve_units_ms.config.EurekaConfig.class);
	private final String namespace = "eureka."; 
	private DataCenterInfo info;
	private HostInfo hostInfo;
	
	public EurekaConfig(InetUtils inetUtils) {
		this.hostInfo = inetUtils.findFirstNonLoopbackHostInfo();
		info = initDataCenterInfo();
	}

    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
    	AmazonInfo amazonInfo = (AmazonInfo) info;
    	String instanceId = ConfigurationManager.getConfigInstance().getString("archaius.deployment.serverId", "i-"+getRandomHexString(16));    	
        EurekaInstanceConfigBean b = new EurekaInstanceConfigBean(inetUtils);
        Map<String, String> metadataMap = new HashMap<>();
        
        ConfigurationManager.getConfigInstance().setProperty("eureka.instance.instanceId", instanceId);

        b.setMetadataMap(metadataMap);
        b.setInitialStatus(InstanceStatus.UP);
        
        metadataMap.put("instance-id", instanceId);
        b.setAppname(ConfigurationManager.getConfigInstance().getString("spring.application.name"));
        b.setInstanceId(null);
        b.setHostname(amazonInfo.get(MetaDataKey.publicHostname));
        String baseUrl = "http://"+amazonInfo.get(MetaDataKey.publicHostname)+":8077";
        b.setHealthCheckUrl(baseUrl+"/webadmin/healthcheck");
        b.setStatusPageUrl(baseUrl+"/webadmin/Status");
        
        b.setVirtualHostName(ConfigurationManager.getConfigInstance().getString("eureka.instance.virtualHostName"));
        b.setNonSecurePort(ConfigurationManager.getConfigInstance().getInt("eureka.port", 7001));
        
        b.setDataCenterInfo(info);
        b.setInstanceId(instanceId);
        
        return b;
    }
    
    private String getRandomHexString(int numchars){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < numchars){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, numchars);
    }
    
    private DataCenterInfo initDataCenterInfo() {
        DataCenterInfo info;
        try {
            info = AmazonInfo.Builder.newBuilder().autoBuild(namespace);
            log.info("Datacenter is: " + Name.Amazon);
        } catch (Throwable e) {
            log.error("Cannot initialize amazon info :", e);
            throw new RuntimeException(e);
        }
        AmazonInfo amazonInfo = (AmazonInfo) info;
        if (amazonInfo.get(MetaDataKey.instanceId) == null) {
            Map<String, String> metadataMap = new HashMap<String, String>();
            metadataMap.put(MetaDataKey.instanceId.getName(),
            		hostInfo.getIpAddress());
            metadataMap.put(MetaDataKey.publicHostname.getName(),
            		hostInfo.getHostname());
            amazonInfo.setMetadata(metadataMap);
        } else if ((amazonInfo.get(MetaDataKey.publicHostname) == null)
                && (amazonInfo.get(MetaDataKey.localIpv4) != null)) {
            amazonInfo.getMetadata().put(MetaDataKey.publicHostname.getName(),
                    (amazonInfo.get(MetaDataKey.localIpv4)));
        }
        return info;
    }

    public String getHostName(boolean refresh) {
        if (refresh) {
            refreshAmazonInfo();
        }
        return ((AmazonInfo) info).get(MetaDataKey.publicHostname);
    }

    public DataCenterInfo getDataCenterInfo() {
        return info;
    }

    /**
     * Refresh instance info - currently only used when in AWS cloud
     * as a public ip can change whenever an EIP is associated or dissociated.
     */
    public synchronized void refreshAmazonInfo() {
        try {
                AmazonInfo newInfo = AmazonInfo.Builder.newBuilder()
                .autoBuild(namespace);
                String newHostname = newInfo.get(MetaDataKey.publicHostname);
                String existingHostname = ((AmazonInfo) info)
                .get(MetaDataKey.publicHostname);
                if (newHostname != null
                        && !newHostname.equals(existingHostname)) {
                    // public dns has changed on us, re-sync it
                    log.warn("The public hostname changed from : "
                            + existingHostname + " => " + newHostname);
                    this.info = newInfo;
                }
       } catch (Throwable t) {
            log.error("Cannot refresh the Amazon Info ", t);
        }
    }

}
