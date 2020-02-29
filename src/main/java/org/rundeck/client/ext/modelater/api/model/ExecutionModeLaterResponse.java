package org.rundeck.client.ext.modelater.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.rundeck.client.api.model.ErrorResponse;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionModeLaterResponse extends ErrorResponse {

    private boolean saved;
    private String msg;

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
