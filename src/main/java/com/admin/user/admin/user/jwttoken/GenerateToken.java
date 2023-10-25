package com.admin.user.admin.user.jwttoken;


import com.admin.user.admin.user.common.RequestMeta;
import com.admin.user.admin.user.common.StringConstants;
import com.admin.user.admin.user.dto.LoginDto;
import com.admin.user.admin.user.exception.ApplicationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;

@Component
public class GenerateToken {

    @Autowired
    RequestMeta requestMeta;

    private  static  final String  privateKey= "Auth task";
    Long duration = 3600L;

    Long takenTime = System.currentTimeMillis();
    Long expiredTime = duration * 100000L + takenTime ;
    Date takenAt = new Date(takenTime);
    Date expiredAt = new Date(expiredTime);
    public String generateTokens(LoginDto loginDto){
        Claims claims = Jwts.claims()
                .setExpiration(expiredAt)
                .setIssuedAt(takenAt)
                .setIssuer(loginDto.getUsername())
                .setSubject(loginDto.getRole());

        return Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS384,privateKey).compact();

    }



    public Claims verifyToken(String authorization) throws Exception {
        try {
            Claims claims;
            claims = Jwts.parser().setSigningKey(privateKey).parseClaimsJws(authorization).getBody();
            return claims;
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            // Handle expired token
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Token has expired", "error");
        } catch (io.jsonwebtoken.SignatureException ex) {
            // Handle signature verification failure
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Token signature is invalid", "error");
        } catch (Exception e) {
            // Handle other exceptions
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, StringConstants.INVALID_TOKEN, "error");
        }
    }



}
