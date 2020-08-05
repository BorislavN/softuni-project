package bg.softuni.util.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OfferViewCounter implements HandlerInterceptor {
    private final Map<String, Set<String>> map;

    public OfferViewCounter() {
        this.map = new HashMap<>();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String principal = (request.getUserPrincipal() == null) ? request.getSession().getId() : request.getUserPrincipal().getName();
        String view = (modelAndView == null) ? "" : modelAndView.getViewName();
        String url = request.getRequestURL().substring(21);

        if ("details".equals(view)) {
            this.map.putIfAbsent(url, new HashSet<>());
            this.map.get(url).add(principal);
        }
    }

    public String getForUrl(String url) {
        long value = this.map.get(url).size();
        String message = value == 1 ? "time" : "times";

        return String.format("viewed %d %s", value, message);
    }

    public long deleteEntry(String key) {
        return this.map.remove(key).size();
    }
}