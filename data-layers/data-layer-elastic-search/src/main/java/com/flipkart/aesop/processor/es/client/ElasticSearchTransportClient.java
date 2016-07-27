/*******************************************************************************
 *
 * Copyright 2012-2015, the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obta a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *******************************************************************************/
package com.flipkart.aesop.processor.es.client;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.elasticsearch.client.transport.TransportClient;
//ankit import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.trpr.platform.core.impl.logging.LogFactory;
import org.trpr.platform.core.spi.logging.Logger;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Initiates ElasticSearchTransport Client , uses the TransportClient of elasticSearch master server discovery
 * @author Pratyay Banerjee
 */
public class ElasticSearchTransportClient extends ElasticSearchClient {

    private static final Logger LOGGER = LogFactory.getLogger(ElasticSearchTransportClient.class);

    @Override
    void init() {
        this.config = ConfigFactory.parseFile(new File(elasticSearchConfig.getConfig()));
        String hostname;
        try{
            hostname  = InetAddress.getLocalHost().getHostName();
        }
        catch(UnknownHostException e)
        {
            LOGGER.info("Unknown HostException Thrown - UpsertDatalayer");
            throw new RuntimeException("FATAL Error: Unable To get Host Information");
        }
        //ankit ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder()
        Settings.Builder settings = Settings.settingsBuilder()
                .put("cluster.name", config.getString("cluster.name"))
                .put("node.name", hostname.replace('.', '-'))
                .put("network.host", hostname)
                .put("node.data",false)
                .put("node.local",false)
                .put("network.host", hostname)
                .put("client.transport.sniff", config.getString("isTransportSniff"));

        //ankit TransportClient client = new TransportClient(settings);
        client  = TransportClient.builder().settings(settings).build();
        
        for(Config confItem : config.getConfigList("transport.hosts"))
        {
        	//ankit client.addTransportAddress(new InetSocketTransportAddress(confItem.getString("host"),confItem.getInt("port")));
        	try {
				((TransportClient)client).addTransportAddress(new InetSocketTransportAddress( InetAddress.getByName( confItem.getString("host")), confItem.getInt("port")));
			} catch (UnknownHostException e) 
			{
	            LOGGER.info("Unknown HostException Thrown - UpsertDatalayer");
	            throw new RuntimeException("FATAL Error: Unable To convert config file string IP to InetAddress type.");
			}
        }
        this.client=client;
    }
}
