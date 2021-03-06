package com.jufan.cloud.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class DemoProviderController {
	@Value("${server.port}")
	private String port;

	@Autowired
	private DiscoveryClient discoveryClient;

	@RequestMapping("/service-instances/{applicationName}")
	public List<ServiceInstance> serviceInstancesByApplicationName(
			@PathVariable String applicationName) {
		Arrays.stream(discoveryClient.getServices().toArray()).forEach((s) -> {
			System.out.println(s);
		});
		return this.discoveryClient.getInstances(applicationName);
	}

	@RequestMapping("/")
	public String sayhello() {
		return "b-demo-provider : " + port;
	}
}
