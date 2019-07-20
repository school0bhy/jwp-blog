package techcourse.myblog.web.Interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Optional<HttpSession> sessionOpt = Optional.ofNullable(request.getSession());
        sessionOpt.ifPresent(session -> {
                    Object user = session.getAttribute("user");

                    if (request.getRequestURI().equals("/logout") || user == null) {
                        return;
                    }

                    modelAndView.addObject("user", user);
                }
        );

    }
}
