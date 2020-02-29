package org.rundeck.client.ext.modelater;

import org.rundeck.client.ext.modelater.api.model.ExecutionModeLaterResponse;
import org.rundeck.client.ext.modelater.api.model.ProjectExecutionModeLater;
import org.rundeck.client.ext.modelater.api.model.executions.EnableLater;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {

    @Headers("Accept: application/json")
    @POST("system/executions/enable/later")
    Call<ExecutionModeLaterResponse> executionModeEnableLater(@Body EnableLater value);

    @Headers("Accept: application/json")
    @POST("system/executions/disable/later")
    Call<ExecutionModeLaterResponse> executionModeDisableLater(@Body EnableLater value);

    @Headers("Accept: application/json")
    @POST("project/{project}/enable/later")
    Call<ExecutionModeLaterResponse> projectExecutionModeEnableLater(
            @Path("project") String project, @Body ProjectExecutionModeLater value
    );

    @Headers("Accept: application/json")
    @POST("project/{project}/disable/later")
    Call<ExecutionModeLaterResponse> projectExecutionModeDisableLater(
            @Path("project") String project, @Body ProjectExecutionModeLater value
    );
}
