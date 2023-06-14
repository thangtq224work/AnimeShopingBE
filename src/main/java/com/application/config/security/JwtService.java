package com.application.config.security;

import com.application.entity.Account;
import com.application.inject.JwtConstant;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    @Autowired
    private JwtConstant jwtConstant;
    public String getUsernameFromJwtToken(String token,boolean isAccessToken) {
        try {
            return extractAllClaims(token,isAccessToken).getSubject();
        }
        catch (Exception exception){
            throw exception;
        }
    }
    public Date getExpiryDate(String token) {
        Claims claims = extractAllClaims(token,true);
        return claims.getExpiration();
    }

    public Claims extractAllClaims(String token,boolean isAccessToken) {
        log.info( " token {}",token );
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder().setSigningKey(isAccessToken?getSignInkey():getSignInkeyRefreshToken()).build().parseClaimsJws(token).getBody();

        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            throw ex;
        } catch (SignatureException ex) {
	        log.error("Unsupported signature algorithm");
            throw ex;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw ex;
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            throw ex;
        }
        return claims;
    }


    public boolean isValidToken(String token, UserDetails userDetails) {
        String username = getUsernameFromJwtToken(token,true);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean validateRefreshToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignInkeyRefreshToken()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            throw ex;
        } catch (SignatureException ex) {
            log.error("Unsupported signature algorithm");
            throw ex;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token ");
            throw ex;
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            throw ex;
        }
    }

    private boolean isTokenExpired(String token) {
        return getExpiryDate(token).before(new Date());
    }

    public String generateToken(String username, Authentication authentication) {
		String[] scopes;
        scopes = authentication.getAuthorities().stream().map((authority)->authority.getAuthority()).toArray(size ->new String[size]);
//				.map(GrantedAuthority::getAuthority)
//				.collect(Collectors.joining(" "));
//        String scopes[] = scope.split(" ");
        for (int i = 0; i < scopes.length; i++) {
            scopes[i] = scopes[i].substring("ROLE_".length());
        }
//        log.error(Arrays.toString(scopes));
//        Object principal = authentication.getPrincipal();
//        if (principal instanceof UserDetails) {
//            String us = ((UserDetails)principal). getUsername();
//            log.info("User login (UserDetails) : {}",us);
//        }
//        else{
//            log.info("User login : {}",principal.toString());
//        }
        Date current = new Date();
        Date expiryDate = new Date(current.getTime() + jwtConstant.getJwtExpiration());
//		https://stackoverflow.com/questions/40252903/static-secret-as-byte-key-or-string
        return Jwts.builder().setSubject(username).setIssuedAt(current).setExpiration(expiryDate)
				.claim("ROLE", scopes)
                .signWith(getSignInkey(), SignatureAlgorithm.HS256).compact();
//		return Jwts.builder().setSubject(username).setIssuedAt(current).setExpiration(expiryDate).signWith(SignatureAlgorithm.ES512, jwtConstant.getJwtSecret()).compact();
    }
    public String generateToken(Account account,String[] roles) {
        Date current = new Date();
        Date expiryDate = new Date(current.getTime() + jwtConstant.getJwtExpiration());
        return Jwts.builder().setSubject(account.getUsername()).setIssuedAt(current).setExpiration(expiryDate)
                .claim("ROLE", roles)
                .signWith(getSignInkey(), SignatureAlgorithm.HS256).compact();}
    public String generateRefreshToken(String username) {
        Date current = new Date();
        Date expiryDate = new Date(current.getTime() + jwtConstant.getRefresh().getJwtExpiration());
        return Jwts.builder().setSubject(username).setIssuedAt(current).setExpiration(expiryDate)
                .signWith(getSignInkeyRefreshToken(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignInkey() {
        byte[] data = Decoders.BASE64.decode(jwtConstant.getJwtSecret());
        return Keys.hmacShaKeyFor(data);
    }
    private Key getSignInkeyRefreshToken() {
        byte[] data = Decoders.BASE64.decode(jwtConstant.getRefresh().getJwtSecret());
        return Keys.hmacShaKeyFor(data);
    }
}
