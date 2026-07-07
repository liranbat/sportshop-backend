package com.java.sadna.backend.sportshop.config;

import com.java.sadna.backend.sportshop.common.constants.ApiHeaderConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TraceIdResponseHeaderFilter extends OncePerRequestFilter {

    private static final String MDC_TRACE_ID = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String traceId = MDC.get(MDC_TRACE_ID);
        if (traceId != null && !traceId.isEmpty()) {
            response.setHeader(ApiHeaderConstants.X_TRACE_ID, traceId);
        }
        chain.doFilter(request, response);
    }
}
