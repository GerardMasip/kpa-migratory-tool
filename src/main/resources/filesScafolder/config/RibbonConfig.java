package #mainPackage.config;

import com.clarivate.ribbon.client.RibbonRestTemplateFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RibbonConfig {

    public RibbonConfig() {
        initRibbonClients();
    }

    private void initRibbonClients() {

        RibbonRestTemplateFactory.addRibbonClient("SB-RETRIEVE-DISEASES");

    }
}