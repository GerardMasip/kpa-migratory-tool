package

import org.springframework.beans.factory.annotation.Autowired;#mainPackage.config;

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


@Component
public class EurekaConfig {

    private static final Logger log = LoggerFactory.getLogger(EurekaConfig.class);

    @Autowired
    public EurekaConfig(InetUtils inetUtils) {
        initInstance(inetUtils);
    }

    private void initInstance(InetUtils inetUtils) {
        EurekaClientConfig.instanceSetup(inetUtils);
    }

}
