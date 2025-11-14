package com.vibenotes.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	private SecretKey getSigningKey() {
		if (secret == null || secret.length() < 64) {
			throw new IllegalArgumentException("JWT secret must be at least 64 characters long");
		}
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(String username) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);

		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(getSigningKey(), SignatureAlgorithm.HS512)
				.compact();
	}

	public String getUsernameFromToken(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();

		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			if (token == null || token.isEmpty()) {
				return false;
			}
			Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			return false;
		} catch (io.jsonwebtoken.MalformedJwtException e) {
			return false;
		} catch (io.jsonwebtoken.security.SignatureException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}

}

