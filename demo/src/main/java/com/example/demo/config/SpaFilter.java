package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class SpaFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        boolean passThroughToSpring =
                path.startsWith("/api/") ||
                path.startsWith("/ws")   ||   // SockJS / WebSocket
                path.startsWith("/h2-console") ||
                path.contains(".");           // 静的ファイル (.js, .css, .html ...)

        if (passThroughToSpring) {
            filterChain.doFilter(request, response);
        } else {
            // SPA ルートはすべて index.html にフォワード
            request.getRequestDispatcher("/index.html").forward(request, response);
        }
    }
}
