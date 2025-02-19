package hu.davidder.webapp.core.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

	private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
		return new JwtAuthenticationToken(jwt, authorities);
	}

	private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
		Map<String, Object> realmAccess = jwt.getClaim("realm_access");
		Collection<GrantedAuthority> authorities = Optional.ofNullable(realmAccess)
				.map(access -> (Collection<String>) access.get("roles")).orElse(Collections.emptyList()).stream()
				.map(SimpleGrantedAuthority::new).collect(Collectors.toList());

		authorities.addAll(defaultGrantedAuthoritiesConverter.convert(jwt));
		return authorities;
	}
}
