package by.bsuir.videohosting.config;

import by.bsuir.videohosting.security.jwt.JwtConfigurer;
import by.bsuir.videohosting.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;

    private static final String ADMIN_ENDPOINT = "/api/admin/**";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";

    @Autowired
    public WebSecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(ADMIN_ENDPOINT).hasRole("ADMIN")
                .antMatchers("/api/auth/check").authenticated()
                .antMatchers("/api/users").authenticated()
                .antMatchers(HttpMethod.POST, "/api/file/videos").authenticated()
                .anyRequest().permitAll()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }
}
