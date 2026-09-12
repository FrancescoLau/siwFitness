package it.uniroma3.siw.config;

import javax.sql.DataSource;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import it.uniroma3.siw.model.Credentials;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final DataSource dataSource;

  public SecurityConfiguration(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Bean
  public UserDetailsService userDetailsService() {
    JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
    manager.setUsersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
    manager.setAuthoritiesByUsernameQuery("SELECT username, role FROM credentials WHERE username=?");
    return manager;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  protected SecurityFilterChain configure(final HttpSecurity httpSecurity) throws Exception {
	  httpSecurity.csrf(csrf -> csrf.disable());
	// BLOCCO 1: Autorizzazioni e Permessi
	    httpSecurity.authorizeHttpRequests(authorize -> {
	        // Permetti le richieste Pre-flight OPTIONS inviate dal browser per CORS
	        authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

	        // Risorse pubbliche REST per React e Swagger
	        authorize.requestMatchers("/rest/**", "/api/**").permitAll();
	        authorize.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll();

	        // Risorse statiche e pagine pubbliche tradizionali
	        authorize.requestMatchers(HttpMethod.GET, "/", "/index", "/register", "/login", "/css/**", "/images/**",   
	                                                  "/favicon.ico", "/error", "/allenamentiConsigliati", "/allenamentoConsigliato/**",
	                                                  "/assets/**", "/index.html", "/allenamento/*/recensioni").permitAll();
	        authorize.requestMatchers(HttpMethod.POST, "/register", "/login").permitAll();

	        // Sezione protetta ADMIN
	        authorize.requestMatchers("/admin/**").hasAnyAuthority(Credentials.ADMIN_ROLE);
	        
	        // ⚠️ Sezione protetta DEFAULT + ADMIN: /deleteAllenamento/** ora è accessibile a ENTRAMBI i ruoli!
	        authorize.requestMatchers("/deleteAllenamento/**").hasAnyAuthority(Credentials.DEFAULT_ROLE, Credentials.ADMIN_ROLE);
	        
	        // Altre rotte riservate agli utenti registrati
	        authorize.requestMatchers("/allenamenti", "/allenamento/**", "/formNewAllenamento", "/editAllenamento/**", "/user/**").hasAnyAuthority(Credentials.DEFAULT_ROLE);

	        // Tutte le altre richieste richiedono autenticazione
	        authorize.anyRequest().authenticated();
	    });

    // BLOCCO 2: Form di Login
    httpSecurity.formLogin(form -> {
        form.loginPage("/login").permitAll();
        form.defaultSuccessUrl("/", true);
        form.failureUrl("/login?error=true");
    });

    // BLOCCO 3: Logout
    httpSecurity.logout(logout -> {
        logout.logoutUrl("/logout");
        logout.logoutSuccessUrl("/");
        logout.invalidateHttpSession(true);
        logout.deleteCookies("JSESSIONID");
        logout.clearAuthentication(true);
        logout.clearAuthentication(true);
        logout.permitAll();
    });

    return httpSecurity.build();
  }
  
  // Bean per autorizzare le richieste da Vite/React (localhost:5173)
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOrigins(List.of("http://localhost:5173"));
      configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
      configuration.setAllowedHeaders(List.of("*"));
      
      configuration.setAllowCredentials(true); 
      
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
  }
}