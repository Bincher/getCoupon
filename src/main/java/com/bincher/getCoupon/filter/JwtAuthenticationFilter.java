package com.bincher.getCoupon.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bincher.getCoupon.provider.JwtProvider;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    
    private final JwtProvider jwtProvider; // Reguired로 필수 생성자로 설정 가능

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
            try{
            // 토큰을 꺼내옴
            String token = parseBearerToken(request);

            // 토큰이 없으면 검증 실패이므로 다음 필터로 넘김
            if(token == null){
                filterChain.doFilter(request, response);
                return;
            }
                        
            // 토큰의 아이디를 꺼내옴
            Claims claims = jwtProvider.validate(token);
            String id = claims.getSubject();
            String role = claims.get("role", String.class);

            // 역시 다음 필터로 넘김(토큰 기간 만료 또는 사인 키 안맞음)
            if(id == null){
                filterChain.doFilter(request, response);
                return;
            }

            // 권한 설정
            List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(role) // "ROLE_ADMIN" 또는 "ROLE_USER"
            );
            
            // 사용자 아이디와 비밀번호(null) 그리고 권한을 넘겨줌
            AbstractAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(id, null, authorities);
            // 웹인증 세부 소스
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Context에 등록하여 외부에 사용할 수 있도록 함
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authenticationToken);

            SecurityContextHolder.setContext(securityContext);

            }catch(Exception exception){
            exception.printStackTrace();
            }
            
            filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request){
            String authorization = request.getHeader("Authorization");

        boolean hasAuthorization = StringUtils.hasText(authorization);
        if (!hasAuthorization) return null;

        boolean isBearer = authorization.startsWith("Bearer");
        if (!isBearer) return null;

            // Bearer가 맞으면 토큰을 받아옴
        String token = authorization.substring(7);
        return token;
    }
}
