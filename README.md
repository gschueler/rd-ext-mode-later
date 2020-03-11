# RD Extension: Mode Later

An extension for [rd](https://github.com/rundeck/rundeck-cli)
for grails-plugin [Disable/Enable execution after X hours](https://github.com/rundeck-plugins/execution-mode-later-plugin).

The plugin adds the option to enable/disable executions/schedule after X hours.

## Install

To install, see [RD Extensions](https://rundeck.github.io/rundeck-cli/extensions/).

## Example

**System Level**

	rd system mode activelater -t 30m   
	rd system mode disablelater -t 2h                          
                       
**Project Level**

	rd projects mode enablelater -p test -t schedule -v 30m                             
	rd projects mode disablelater -p test -t  schedule -v 1h                          