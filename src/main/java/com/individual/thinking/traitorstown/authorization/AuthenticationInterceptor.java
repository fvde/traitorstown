package com.individual.thinking.traitorstown.authorization;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.user.User;
import com.individual.thinking.traitorstown.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("authenticationInterceptor")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    private static final String TOKEN = "token";
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        String requestUrl = request.getRequestURI();
        String token = request.getHeader(TOKEN);

        if (StringUtils.isEmpty(token)) {
            log.error("Request {} failed. Missing token!", requestUrl);
            throw new MissingTokenException("No token supplied. Obtain a token for logging into the API.");
        }

        User user = userService.getUserByToken(token);
        request.setAttribute(Configuration.AUTHENTICATION_KEY, user.getPlayer());
        return true;
    }

}
