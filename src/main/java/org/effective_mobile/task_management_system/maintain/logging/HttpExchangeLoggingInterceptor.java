package org.effective_mobile.task_management_system.maintain.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.effective_mobile.task_management_system.security.CookieComponent;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Log4j2
@AllArgsConstructor
public class HttpExchangeLoggingInterceptor implements HandlerInterceptor {

    private final CookieComponent cookieComponent;
    private final HttpExchangeLoggingComponent httpExchangeLoggingComponent;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (log.isDebugEnabled() || log.isInfoEnabled()) {
            setStartTime(request);
            HttpRequestLogPojo httpRequestLogPojo = HttpRequestLogPojo.builder()
                .httpMethod(request.getMethod())
                .path(request.getRequestURI())
                .requestBody(httpExchangeLoggingComponent.getPayload(request))
                .clientIp(request.getRemoteHost() + ":" + request.getRemotePort())
                .queryString(request.getQueryString())
                .httpRequestAuthInfo(
                    HttpRequestAuthInfo.builder()
                        .headers(httpExchangeLoggingComponent.getHeaders(request))
                        .cookies(request.getCookies())
                        .token(
                            cookieComponent.hasToken(request) ? cookieComponent.getToken(request) : "UNAUTHENTICATED")
                        .build()
                )
                .build();

            log.debug(httpExchangeLoggingComponent.asPretty(httpRequestLogPojo));
        }
        return true;
    }

    @Override
    public void afterCompletion(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull Object handler,
        Exception ex)
    {
        val startTime = getStartTime(request);
        val endTime = System.currentTimeMillis();

        if (log.isDebugEnabled() || log.isInfoEnabled()) {
            try {
                final HttpResponseLogPojo httpResponseLogPojo = HttpResponseLogPojo.builder()
                    .path(request.getRequestURI())
                    .status(response.getStatus())
                    .responseBody(httpExchangeLoggingComponent.getPayload(response))
                    .headers(httpExchangeLoggingComponent.getHeaders(response))
                    .executionTime(endTime - startTime)
                    .build();

                log.debug(httpExchangeLoggingComponent.asPretty(httpResponseLogPojo));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setStartTime(HttpServletRequest request) {
        request.setAttribute(Headers.START_TIME_ATTRIBUTE, System.currentTimeMillis());
    }

    private Long getStartTime(HttpServletRequest request) {
        return (Long) request.getAttribute(Headers.START_TIME_ATTRIBUTE);
    }
}