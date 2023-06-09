package org.catenax.atp.config.security;

import org.catenax.atp.utils.constant.AppConstants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;

public class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter;
    private final String resourceId;

    public CustomAuthenticationConverter(String resourceId) {
        this.resourceId = resourceId;
        this.grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Collection<GrantedAuthority> authorities = new HashSet<>((this.grantedAuthoritiesConverter.convert(source)));
        authorities.addAll(this.extractResourceRoles(source));
        return new JwtAuthenticationToken(source, authorities);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        List<String> groupNames = jwt.getClaim(AppConstants.CLAIMS_GROUP_NAME);
        if (groupNames != null) {
            return groupNames.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        } else {
            return Set.of();
        }
    }
}
