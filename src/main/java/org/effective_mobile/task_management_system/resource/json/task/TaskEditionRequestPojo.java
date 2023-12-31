package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.effective_mobile.task_management_system.resource.json.RequestPojo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskEditionRequestPojo implements RequestPojo {

    @JsonProperty
    private String content;

    @JsonProperty
    private String priority;
}
