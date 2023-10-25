package com.admin.user.admin.user.jwttoken;

import com.admin.user.admin.user.common.ApiResponse;
import com.admin.user.admin.user.common.StringConstants;
import com.admin.user.admin.user.exception.TokenVerificationException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.NestedServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@Component
public class Interceptor implements HandlerInterceptor {
    @Autowired
    private GenerateToken generateToken;
    @Autowired
    private ApiResponse apiResponse;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String jwts = null;
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            jwts = token.substring(7);
        }

        if (isLoginOrSignupEndpoint(request.getRequestURI())) {
            return true;
        } else {
            try {
                Claims claims = generateToken.verifyToken(jwts);

                if (claims.getSubject().equals("admin")) {
                    if (!request.getRequestURI().contains("/admin")) {
                        throw new TokenVerificationException(StringConstants.INVALID_TOKEN);
                    }
                } else if (claims.getSubject().equals("user")) {
                    if (!request.getRequestURI().contains("/user")) {
                        throw new TokenVerificationException(StringConstants.INVALID_TOKEN);
                    }
                }
            } catch (HttpClientErrorException.Unauthorized e) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Unauthorized");
                return false;
            }
        }
        return true;
    }

    private boolean isLoginOrSignupEndpoint(String requestURI) {
        return requestURI.contains("/api/admin/signup") || requestURI.contains("/api/admin/login")
                || requestURI.contains("/api/user/signup") || requestURI.contains("/api/user/login");
    }
}