package com.jufan.cloud.sso.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private RedisConnectionFactory connectionFactory;
	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public RedisTokenStore tokenStore() {//TODO 设置序列化策略
		RedisTokenStore redisTokenStore = new RedisTokenStore(connectionFactory);
		redisTokenStore.setPrefix("cloud:cloud_sso:");
		return redisTokenStore;
	}


	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {//TODO 添加自定义clientDetailService
		endpoints
				.authenticationManager(authenticationManager)
				.userDetailsService(userDetailsService)//若无，refresh_token会有UserDetailsService is required错误
				.tokenStore(tokenStore());
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {//TODO 添加token验证代码
		security
				.tokenKeyAccess("permitAll()")
				.checkTokenAccess("isAuthenticated()");
	}
//	@Override
//	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
//		oauthServer.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_TRUSTED_CLIENT')").checkTokenAccess(
//				"hasAuthority('ROLE_TRUSTED_CLIENT')");
//	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient("sso_client")
					.scopes("xx")//此处的scopes是无用的，可以随意设置
					.secret("sso_client_pwd")
					.authorizedGrantTypes("password", "authorization_code", "refresh_token")
					.and()
				.withClient("gateway")
					.scopes("xx")//此处的scopes是无用的，可以随意设置
					.authorizedGrantTypes("implicit");
	}
}
