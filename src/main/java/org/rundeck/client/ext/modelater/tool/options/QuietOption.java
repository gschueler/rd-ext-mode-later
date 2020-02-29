package org.rundeck.client.ext.modelater.tool.options;

import com.lexicalscope.jewel.cli.Option;

public interface QuietOption {
    @Option(shortName = "q", longName = "quiet", description = "Reduce output.")
    boolean isQuiet();
}
