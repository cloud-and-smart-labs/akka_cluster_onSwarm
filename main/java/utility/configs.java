package utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class configs {
	public static Config getConfig(String role, String configFile) {
	        final Map<String, Object> properties = new HashMap<>();
	
	        if (role != null && !role.isEmpty()) {
	            properties.put("akka.cluster.roles", Arrays.asList(role));
	        }
	
	        Config baseConfig = ConfigFactory.load(configFile);
	        return ConfigFactory.parseMap(properties)
	                .withFallback(baseConfig);
	}
}