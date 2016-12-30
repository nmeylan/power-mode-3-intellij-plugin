Power Mode II Intellij plugin
======================

[![Join the chat at https://gitter.im/baptistemesta/power-mode-intellij-plugin](https://badges.gitter.im/baptistemesta/power-mode-intellij-plugin.svg)](https://gitter.im/baptistemesta/power-mode-intellij-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[Plugin available in Jetbrains plugin repositories](https://plugins.jetbrains.com/plugin/8251)

![Demo](/images/powerMode.gif)

How to use
---------

Install the plugin then simply enable the sparkling in Preferences > Appearance > Power mode

## Features
<ul>
    <li>Exploding falling sparks</li>
    <li>Editor shaking</li>
    <li>Flames</li>
    <li>Heatup based on typing speed. The more you type the more happens.</li>
    <li>Keyboard shortcut to toggle power mode [shift ctrl alt O]</li>
    <li>Choose particle colors and transparency within a color space</li>
    <li>Adjust particle velocity and gravitation</li>
    <li>Modify the animation frame rate</li>
    <li>Multi caret support</li>
    <li>Animation on caret movement</li>
    <li>Play music folder: volume based on heatup</li>
    <li>Play next song action: [shift ctrl alt M]</li>
    <li>Visualize bigger file editing with "BAM!"</li>
    <li>Everything is configurable</li>
</ul>
 
## "Architecture"

* `PowerMode` is the settings instance which is used by `PowerModeConfigurable` to populate the UI Settings dialog.
                   `PowerModeConfigurableUI` manages the settings dialog to change the settings in `PowerMode`.
*  `PowerMode` is stored/loaded by xml serializer and annotation magic by Intellij.  
*  `PowerMode` starts up the `SparkContainerManager`. The `SparkContainerManager` creates a `SparkContainer` for each editor.
* The `SparkContainer` creates the `ElementOfPower` (`PowerSpark, PowerFlame`) and manages their animation and lifecycle. 

### heatup

* heatup increases the lifetime and amount of Sparks and Flames over time. The most values are multiplied with `ElementOfPower.valueFactor` to simulate this heatup.
`ElementOfPower.valueFactor` is composed of `heatupFactor` (how much of all animation doesn't depend on heatup) and `timeFactor` (how much heatup do we currently have).
* The heatup itself is calculated by `keyStrokesPerMinute` and the amount of keystrokes within `heatupTime`.
