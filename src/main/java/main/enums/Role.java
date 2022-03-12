package main.enums;

import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

public enum Role {
    USER(Set.of(Authority.WRITE)),
    MODERATOR(Set.of(Authority.MODERATE, Authority.WRITE));

    private final Set<GrantedAuthority> authorities;

    Role(Set<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public Set<GrantedAuthority> getAuthorities(){
        return authorities;
    }

    private enum Authority implements GrantedAuthority {
        WRITE,
        MODERATE;

        @Override
        public String getAuthority() {
            return this.toString();
        }
    }
}
