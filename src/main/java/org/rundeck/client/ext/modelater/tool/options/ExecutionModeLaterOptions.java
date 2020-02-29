package org.rundeck.client.ext.modelater.tool.options;

import com.lexicalscope.jewel.cli.Option;

public interface ExecutionModeLaterOptions  {

    @Option(shortName = "t", longName = "timeValue", description = "Set the time value where the execution will be enable/disable. For example: 3m, 1h, 5d")
    String getTimeValue();
}
