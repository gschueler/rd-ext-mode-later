package org.rundeck.client.ext.modelater.tool.commands.projects;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import lombok.Setter;
import org.rundeck.client.api.RequestFailed;
import org.rundeck.client.api.RundeckApi;
import org.rundeck.client.ext.modelater.Api;
import org.rundeck.client.ext.modelater.api.model.ExecutionModeLaterResponse;
import org.rundeck.client.ext.modelater.api.model.ProjectExecutionModeLater;
import org.rundeck.client.ext.modelater.tool.options.ProjectExecutionModeLaterOptions;
import org.rundeck.client.ext.modelater.tool.options.QuietOption;

import org.rundeck.client.ext.modelater.tool.commands.ExecutionLaterResponseHandler;
import org.rundeck.client.tool.InputError;
import org.rundeck.client.tool.extension.RdCommandExtension;
import org.rundeck.client.tool.extension.RdTool;
import org.rundeck.client.util.Client;
import org.rundeck.client.util.ServiceClient;
import org.rundeck.toolbelt.Command;
import org.rundeck.toolbelt.CommandOutput;
import org.rundeck.toolbelt.SubCommand;
import retrofit2.Response;

import java.io.IOException;

/**
 * Subcommands for project enable/disable execution/schedule after X time
 *
 * @author roberto
 * @since 12/30/19
 */
@SubCommand(path = {"projects", "mode"},
            descriptions = {"", "Manage Project Execution Mode"})
public class Mode
        implements RdCommandExtension
{
    @Setter RdTool rdTool;


    @CommandLineInterface(application = "enableLater")
    interface ModeActiveLater
            extends
            ProjectExecutionModeLaterOptions,
            QuietOption
    {

    }

    private ServiceClient<Api> getClient() throws InputError {
        return rdTool.getRdApp().getClient(Api.class);
    }

    @Command(description = "enable executions/schedule mode later")
    public boolean enableLater(ModeActiveLater opts, CommandOutput output)
            throws IOException, InputError, Client.UnsupportedVersionDowngrade
    {

        ProjectExecutionModeLater enableLater = new ProjectExecutionModeLater();
        enableLater.setValue(opts.getTimeValue());
        enableLater.setType(opts.getType());

        String project = opts.getProject();

        if (!opts.isQuiet()) {
            output.info(String.format("Setting %s mode to  %s", opts.getType(), opts.getTimeValue()));
        }
        ServiceClient<Api> client = getClient();

        ServiceClient.WithErrorResponse<ExecutionModeLaterResponse> execute = client.apiWithErrorResponseDowngradable(
                api -> api.projectExecutionModeEnableLater(project, enableLater)
        );

        checkValidationError(output, client, execute);

        ExecutionModeLaterResponse response = ExecutionLaterResponseHandler.handle(execute, output);

        if (!opts.isQuiet()) {
            if (response.isSaved()) {
                output.info(String.format("%s will be enable after %s", opts.getType(), opts.getTimeValue()));
                output.output(response.getMsg());
            } else {
                output.warning(String.format("%s mode wasn't save", opts.getType()));
                output.warning(response.getMsg());
            }
        }

        return response.isSaved();
    }

    @Command(description = "disable executions/schedule mode later")
    public boolean disableLater(ModeActiveLater opts, CommandOutput output)
            throws IOException, InputError, Client.UnsupportedVersionDowngrade
    {

        ProjectExecutionModeLater disableLater = new ProjectExecutionModeLater();
        disableLater.setValue(opts.getTimeValue());
        disableLater.setType(opts.getType());

        String project = opts.getProject();

        if (!opts.isQuiet()) {
            output.info(String.format("Setting %s mode to  %s", opts.getType(), opts.getTimeValue()));
        }
        ServiceClient<Api> client = getClient();

        ServiceClient.WithErrorResponse<ExecutionModeLaterResponse> execute = client.apiWithErrorResponseDowngradable(

                api -> api.projectExecutionModeDisableLater(project, disableLater)
        );

        checkValidationError(output, client, execute);

        ExecutionModeLaterResponse response = ExecutionLaterResponseHandler.handle(execute, output);

        if (!opts.isQuiet()) {
            if (response.isSaved()) {
                output.info(String.format("%s will be disable after %s", opts.getType(), opts.getTimeValue()));
                output.output(response.getMsg());
            } else {
                output.warning(String.format("%s mode wasn't save", opts.getType()));
                output.warning(response.getMsg());
            }
        }

        return response.isSaved();
    }

    private static void checkValidationError(
            CommandOutput output,
            final ServiceClient<Api> client,
            final ServiceClient.WithErrorResponse<ExecutionModeLaterResponse> errorResponse
    )
            throws IOException
    {
        Response<ExecutionModeLaterResponse> response = errorResponse.getResponse();
        if (errorResponse.isError400()) {
            ExecutionModeLaterResponse error = client.readError(
                    errorResponse.getErrorBody(),
                    ExecutionModeLaterResponse.class,
                    Client.MEDIA_TYPE_JSON
            );

            if (null != error) {
                if (error.getErrorMessage() != null) {
                    output.error(error.getErrorMessage());
                }
                if (error.getMsg() != null) {
                    output.error(error.getMsg());
                }
            }

            throw new RequestFailed(String.format(
                    "Validation failed: (error: %d %s)",
                    response.code(),
                    response.message()
            ), response.code(), response.message());
        }
    }
}
