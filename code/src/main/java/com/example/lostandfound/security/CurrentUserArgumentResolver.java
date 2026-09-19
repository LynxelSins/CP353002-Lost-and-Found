package com.example.lostandfound.security;

import com.example.lostandfound.exception.UnauthorizedException;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && (parameter.getParameterType().equals(UUID.class)
                || parameter.getParameterType().equals(UserPrincipal.class));
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                   ModelAndViewContainer mavContainer,
                                   NativeWebRequest webRequest,
                                   WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new UnauthorizedException("ต้องเข้าสู่ระบบก่อนจึงจะใช้งานส่วนนี้ได้");
        }

        if (parameter.getParameterType().equals(UUID.class)) {
            return principal.getId();
        }
        return principal;
    }
}