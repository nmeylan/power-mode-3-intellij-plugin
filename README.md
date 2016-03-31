Power Mode II Intellij plugin
======================

[![Join the chat at https://gitter.im/baptistemesta/power-mode-intellij-plugin](https://badges.gitter.im/baptistemesta/power-mode-intellij-plugin.svg)](https://gitter.im/baptistemesta/power-mode-intellij-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[Plugin available in Jetbrains plugin repositories](https://plugins.jetbrains.com/plugin/8251)

![Demo](/images/powerMode.gif)

How to use
---------

Install the plugin then simply enable the sparkling in Preferences > Appearance > Power mode

## Features
* Exploding falling sparks
* Editor shaking
* Flames
* Heatup based on typing speed. The more you type the more happens.
* Everything is configurable
* keyboard shortcut to toggle power mode [shift ctrl alt O]

 
## Architecture

* `PowerMode` is the settings instance which is used by `PowerModeConfigurable` to populate the UI Settings dialog.
                   `PowerModeConfigurableUI` manages the settings dialog to change the settings in `PowerMode`.
*  `PowerMode` is stored/loaded by xml serializer and annotation magic by Intellij.  
*  `PowerMode` starts up the `SparkContainerManager`. The `SparkContainerManager` creates a `SparkContainer` for each editor.
* The `SparkContainer` creates the `ElementOfPower` (`PowerSpark, PowerFlame`) and manages their animation and lifecycle. 

### heatup

* heatup increases the lifetime and amount of Sparks and Flames over time. The most values are multiplied with `ElementOfPower.valueFactor` to simulate this heatup.
`ElementOfPower.valueFactor` is composed of `heatupFactor` (how much of all animation doesn't dempend on heatup) and `timeFactor` (how much heatup do we currently have).
* The heatup itself is calculated by `keyStrokesPerMinute` and the amount of keystrokes within `heatupTime`.
