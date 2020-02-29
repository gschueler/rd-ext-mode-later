package org.rundeck.client.ext.modelater.tool.commands.system;

import com.lexicalscope.jewel.cli.CommandLineInterface;
import lombok.Setter;
import org.rundeck.client.api.RequestFailed;
import org.rundeck.client.ext.modelater.Api;
import org.rundeck.client.ext.modelater.api.model.ExecutionModeLaterResponse;
import org.rundeck.client.ext.modelater.api.model.executions.EnableLater;
import org.rundeck.client.ext.modelater.tool.commands.ExecutionLaterResponseHandler;
import org.rundeck.client.ext.modelater.tool.options.ExecutionModeLaterOptions;
import org.rundeck.client.ext.modelater.tool.options.QuietOption;
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

@SubCommand(path = {"system", "mode"})
public class Mode
        implements RdCommandExtension
{
    @Setter RdTool rdTool;

    @CommandLineInterface(application = "activeLater")
    interface ModeActiveLater
            extends ExecutionModeLaterOptions,
                    QuietOption
    {

    }

    @Command(description = "Set execution mode Active Later")
    public boolean activeLater(ModeActiveLater opts, CommandOutput output)
            throws IOException, InputError, Client.UnsupportedVersionDowngrade
    {
        ServiceClient<Api> client = getClient();
        EnableLater enableLater = new EnableLater();
        enableLater.setValue(opts.getTimeValue());

        if (!opts.isQuiet()) {
            output.info("Setting execution mode to active");
        }

        ServiceClient.WithErrorResponse<ExecutionModeLaterResponse> execute =
                client.apiWithErrorResponseDowngradable(api -> api.executionModeEnableLater(enableLater)
                );

        checkValidationError(output, client, execute);

        ExecutionModeLaterResponse response = ExecutionLaterResponseHandler.handle(execute, output);


        if (!opts.isQuiet()) {
            if (response.isSaved()) {
                output.info("Next Execution Mode will be active");
                output.output(response.getMsg());
            } else {
                output.warning("Next Execution Mode wasn't saved");
                output.warning(response.getMsg());
            }
        }

        return response.isSaved();
    }

    private ServiceClient<Api> getClient() throws InputError {
        return rdTool.getRdApp().getClient(Api.class);
    }

    @Command(description = "Set execution mode Disable Later")
    public boolean disableLater(ModeActiveLater opts, CommandOutput output)
            throws IOException, InputError, Client.UnsupportedVersionDowngrade
    {
        EnableLater enableLater = new EnableLater();
        enableLater.setValue(opts.getTimeValue());

        if (!opts.isQuiet()) {
            output.info(String.format("Setting execution mode to passive"));
        }
        ServiceClient<Api> client = getClient();
        ServiceClient.WithErrorResponse<ExecutionModeLaterResponse> execute = client.apiWithErrorResponseDowngradable(
                api -> api.executionModeDisableLater(enableLater)
        );

        checkValidationError(output, client, execute);

        ExecutionModeLaterResponse response = ExecutionLaterResponseHandler.handle(execute, output);

        if (!opts.isQuiet()) {
            if (response.isSaved()) {
                output.info("Next Execution Mode will be disable");
                output.output(response.getMsg());
            } else {
                output.warning("Next Execution Mode wasn't saved");
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
