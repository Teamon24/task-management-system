package org.effective_mobile.task_management_system.resource.json.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.RequestPojo;
import org.effective_mobile.task_management_system.utils.JsonPojos;

@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeRequestPojo implements RequestPojo {

    @Getter
    @JsonProperty(JsonPojos.Task.Field.STATUS)
    private String status;
}
