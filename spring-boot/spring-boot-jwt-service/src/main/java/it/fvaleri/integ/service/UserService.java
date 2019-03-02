package it.fvaleri.integ.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserService {

    private InMemoryUserDetailsManager manager;

    public UserService() {
        // in memory user store just for testing
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(users.username("fvaleri").password("secret").roles("USER").build());
        manager.createUser(users.username("admin").password("admin").roles("USER", "ADMIN").build());
        this.manager = manager;
    }

    public UserDetails loadByUsername(String username) throws UsernameNotFoundException {
        return manager.loadUserByUsername(username);
    }

}
