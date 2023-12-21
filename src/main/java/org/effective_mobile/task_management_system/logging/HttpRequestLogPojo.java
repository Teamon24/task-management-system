package org.effective_mobile.task_management_system.logging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.effective_mobile.task_management_system.resource.json.JsonPojo;

/**
 * Информация о http-запросе.
 */
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpRequestLogPojo implements JsonPojo {

    /**
     * http-метод (GET, POST, ...). */
    @JsonProperty private String httpMethod;

    /**
     * Запрашиваемый uri. */
    @JsonProperty private String path;

    /**
     * ip клиента. */
    @JsonProperty private String clientIp;

    /**
     * Тело запроса (чаще всего - принимаемый json). */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Object requestBody;

    /**
     * Строка запроса (если есть). */
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String queryString;

    /**
     * Информация об аутентифицированном пользователе. */
    @JsonProperty private HttpRequestAuthInfo httpRequestAuthInfo;
}