package org.rundeck.client.ext.modelater.tool.commands.system

import okhttp3.ResponseBody
import org.rundeck.client.api.RequestFailed
import org.rundeck.client.ext.modelater.Api
import org.rundeck.client.ext.modelater.api.model.ExecutionModeLaterResponse
import org.rundeck.client.ext.modelater.tool.commands.MockRdTool
import org.rundeck.client.tool.RdApp
import org.rundeck.client.util.Client
import org.rundeck.toolbelt.CommandOutput
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.mock.Calls
import spock.lang.Specification

class ModeSpec extends Specification {

    def "test active later"(){
        given:

        def api = Mock(Api)

        def retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl('http://example.com/fake/').build()
        def out = Mock(CommandOutput)
        def client = new Client(api, retrofit, null, null, 34, true, null)

        def rdapp = Mock(RdApp) {
                getClient(Api) >> client
            }
            def rdtool = new MockRdTool(client: client, rdApp: rdapp)

        def opts = Mock(Mode.ModeActiveLater) {
            getTimeValue() >> '30m'
        }

        Mode mode = new Mode()
        mode.rdTool=rdtool

        when:
        def result = mode.activeLater(opts, out)

        then:
        1 * api.executionModeEnableLater(_) >>
                Calls.response(
                        new ExecutionModeLaterResponse(saved: saved, msg: 'Execution Saved')
                )

        infoCalls * out.info("Next Execution Mode will be active")
        warmCalls * out.warning("Next Execution Mode wasn't saved")
        result == saved

        where:
        saved   | infoCalls | warmCalls
        true    | 1         | 0
        false   | 0         | 1
    }

    def "test active failed"(){
        given:

        def api = Mock(Api)

        def retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl('http://example.com/fake/').build()
        def out = Mock(CommandOutput)
        def client = new Client(api, retrofit, null, null, 34, true, null)

        def rdapp = Mock(RdApp) {
                getClient(Api) >> client
            }
            def rdtool = new MockRdTool(client: client, rdApp: rdapp)

        def opts = Mock(Mode.ModeActiveLater) {
            getTimeValue() >> '30m'
        }

        Mode mode = new Mode()
        mode.rdTool=rdtool

        when:
        def result = mode.activeLater(opts, out)

        then:
        RequestFailed e = thrown()
        1 * api.executionModeEnableLater(_) >>
                Calls.response(
                        Response.error(400, ResponseBody.create(
                                Client.MEDIA_TYPE_JSON,
                                '{"saved":false,"msg":"Error saving execution mode"}'
                        )
                        )
                )

        0 * out.info("Next Execution Mode will be active")
        1 * out.error("Error saving execution mode")

    }

    def "test disable later"(){
        given:

        def api = Mock(Api)

        def retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl('http://example.com/fake/').build()
        def out = Mock(CommandOutput)
        def client = new Client(api, retrofit, null, null, 34, true, null)

        def rdapp = Mock(RdApp) {
                getClient(Api) >> client
            }
            def rdtool = new MockRdTool(client: client, rdApp: rdapp)

        def opts = Mock(Mode.ModeActiveLater) {
            getTimeValue() >> '30m'
        }

        Mode mode = new Mode()
        mode.rdTool=rdtool

        when:
        def  result = mode.disableLater(opts, out)

        then:
        1 * api.executionModeDisableLater(_) >>
                Calls.response(
                        new ExecutionModeLaterResponse(saved: saved, msg: 'Execution Saved')
                )

        infoCalls * out.info("Next Execution Mode will be disable")
        warmCalls * out.warning("Next Execution Mode wasn't saved")

        result == saved

        where:
        saved   | infoCalls | warmCalls
        true    | 1         | 0
        false   | 0         | 1
    }

    def "test diable failed"(){
        given:

        def api = Mock(Api)

        def retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl('http://example.com/fake/').build()
        def out = Mock(CommandOutput)
        def client = new Client(api, retrofit, null, null, 34, true, null)

        def rdapp = Mock(RdApp) {
                getClient(Api) >> client
            }
            def rdtool = new MockRdTool(client: client, rdApp: rdapp)

        def opts = Mock(Mode.ModeActiveLater) {
            getTimeValue() >> '30m'
        }

        Mode mode = new Mode()
        mode.rdTool=rdtool

        when:
        def result = mode.disableLater(opts, out)

        then:
        RequestFailed e = thrown()
        1 * api.executionModeDisableLater(_) >>
                Calls.response(
                        Response.error(400, ResponseBody.create(
                                Client.MEDIA_TYPE_JSON,
                                '{"saved":false,"msg":"Error saving execution mode"}'
                        )
                        )
                )

        0 * out.info("Next Execution Mode will be active")
        1 * out.error("Error saving execution mode")

    }

    def "test active failed, plugin not enabled"(){
        given:

        def api = Mock(Api)

        def retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .baseUrl('http://example.com/fake/').build()
        def out = Mock(CommandOutput)
        def client = new Client(api, retrofit, null, null, 34, true, null)

        def rdapp = Mock(RdApp) {
                getClient(Api) >> client
            }
            def rdtool = new MockRdTool(client: client, rdApp: rdapp)

        def opts = Mock(Mode.ModeActiveLater) {
            getTimeValue() >> '30m'
        }

        Mode mode = new Mode()
        mode.rdTool=rdtool

        when:
        def result = mode.activeLater(opts, out)

        then:
        1 * api.executionModeEnableLater(_) >>
                Calls.response(
                        Response.error(404, ResponseBody.create(
                                Client.MEDIA_TYPE_JSON,
                                '{"Error": "Invalid API Request: /api/29/project/Demo/enable/later"}'
                        )
                        )
                )

        0 * out.info("executions will be enable after 30m")
        1 * out.error("Server returned a 404. Either you don\'t have access to the API or the execution later plugin feature is not enabled.")
        1 * out.warning('Error processing API')

    }
}
