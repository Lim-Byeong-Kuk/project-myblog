package com.myblog.api.config;

import com.myblog.api.config.data.UserSession;
import com.myblog.api.domain.Session;
import com.myblog.api.exception.ExpiredJwt;
import com.myblog.api.exception.Unauthorized;
import com.myblog.api.repository.SessionRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Slf4j
@RequiredArgsConstructor
public class AuthResolver implements HandlerMethodArgumentResolver {

    private final SessionRepository sessionRepository;
    private final AppConfig appconfig;

    // resolver 로 지원할 타입이 맞는지 확인하는 역할
    // 여기서 parameter 는 userSession 이 넘어온다
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserSession.class); // true or false
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String jws = webRequest.getHeader("Authorization");
        if (jws == null || jws.equals("")) {
            throw new Unauthorized();
        }

        try {

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(appconfig.getJwtKey())
                    .build()
                    .parseClaimsJws(jws);

            String userId = claims.getBody().getSubject();
            Date issuedAt = claims.getBody().getIssuedAt();
            Date expiration = claims.getBody().getExpiration();

            log.info("-> issuedAt ={}",issuedAt);

            return new UserSession(Long.parseLong(userId), issuedAt, expiration);
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwt("만료된 토큰입니다.", e);
        } catch (JwtException e) {
            throw new Unauthorized(e);
        }
    }

    /**
     * 세션, 쿠키
     */
//    @Override
//    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
//        if (servletRequest == null) {
//            log.error("servletRequest null");
//            throw new Unauthorized();
//        }
//
//        Cookie[] cookies = servletRequest.getCookies();
//        if (cookies.length == 0) {
//            log.error("쿠키가 없음");
//            throw new Unauthorized();
//        }
//
//        String accessToken = cookies[0].getValue();
//
//        Session session = sessionRepository.findByAccessToken(accessToken)
//                .orElseThrow(() -> new Unauthorized());
//
//        return new UserSession(session.getUser().getId());
//    }
}
