package org.effective_mobile.task_management_system.maintain.logging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class HttpResponseLogPojo implements HttpExchangeLogPojo {

    @JsonProperty(index = 0)
    private String path;

    /**
     * статус http-ответа (GET, POST, ...). */
    @JsonProperty(index = 1)
    private Integer status;

    /**
     * Тело ответа. */
    @JsonProperty(index = 4)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Object responseBody;

    /**
     * Заголовки http-запроса. */
    @JsonProperty(index = 3) private Headers headers;

    /**
     * Время исполнения запроса. */
    @JsonProperty(index = 2) private long executionTime;
}
