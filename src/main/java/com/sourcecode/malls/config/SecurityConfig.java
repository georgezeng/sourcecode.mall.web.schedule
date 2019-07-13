package com.sourcecode.malls.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.sourcecode.malls.properties.SuperAdminProperties;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private UserDetailsService userDetailsService;

	@Autowired
	private PasswordEncoder pwdEncoder;

	@Autowired
	protected SuperAdminProperties adminProperties;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.httpBasic().disable();
		http.formLogin().permitAll();
		http.userDetailsService(userDetailsService);
		http.authorizeRequests().antMatchers("/actuator/health").permitAll();
		http.authorizeRequests().anyRequest().authenticated();
	}

	@PostConstruct
	public void init() {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(User.withUsername(adminProperties.getUsername())
				.password(pwdEncoder.encode(adminProperties.getPassword())).roles("AUTH_USER").build());
		userDetailsService = manager;
	}

}
