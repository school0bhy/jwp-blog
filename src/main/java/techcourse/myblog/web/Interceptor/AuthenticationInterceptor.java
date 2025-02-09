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
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean loggedIn = loggedIn(request);

        if (loggedIn && needLogout(request)) {
            response.sendRedirect("/");
            return false;
        }

        if (!loggedIn && needLogin(request)) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

    private boolean loggedIn(HttpServletRequest request) {
        Optional<HttpSession> sessionOpt = Optional.ofNullable(request.getSession());
        if (sessionOpt.isPresent()) {
            return sessionOpt.get().getAttribute("user") != null;
        }
        return false;
    }

    private boolean needLogout(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri.equals("/signup")
                || uri.equals("/login")
                || (uri.equals("/users") && request.getMethod().equals("POST"));
    }

    private boolean needLogin(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return uri.startsWith("/mypage")
                || request.getMethod().equals("DELETE")
                || uri.equals("/logout");
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Optional<HttpSession> sessionOpt = Optional.ofNullable(request.getSession());
        sessionOpt.ifPresent(session -> {
                    Object user = session.getAttribute("user");

                    if (!request.getRequestURI().equals("/logout") && user != null) {
                        modelAndView.addObject("user", user);
                    }
                }
        );
    }
}
