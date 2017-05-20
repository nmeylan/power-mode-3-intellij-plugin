Power Mode II Intellij plugin
======================

[![Join the chat at https://gitter.im/baptistemesta/power-mode-intellij-plugin](https://badges.gitter.im/baptistemesta/power-mode-intellij-plugin.svg)](https://gitter.im/baptistemesta/power-mode-intellij-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)



![Demo](/images/powerMode.gif)

Installation
--------------
[Watch the Video](https://www.youtube.com/watch?v=aIWs7YQ9aMs)

[Install from Jetbrains plugin repositories](https://plugins.jetbrains.com/plugin/8251)

How to use
---------

Install the plugin then simply enable the sparkling in Preferences > Appearance > Power mode II

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
    <li>See your power level in the indicator box</li>
    <li>Customizable animation images</li>
    <li>Everything is configurable</li>
</ul>
<a href="https://github.com/axaluss/power-mode-intellij-plugin/releases/latest">Latest Release Download<img src="https://raw.githubusercontent.com/axaluss/power-mode-intellij-plugin/master/images/download.png"/></a><br/>
<a href="https://www.paypal.me/AlexanderThom">Want to Donate a Beer?
<img style="width:100px; height: 100px;" src="https://raw.githubusercontent.com/axaluss/power-mode-intellij-plugin/master/images/beer.png"/></a>

## Plugin Development Setup 
### 1 [install scala 2.11](https://www.scala-lang.org/download/) and create a global scala SDK
### 2 [download and install IntelliJ Community Edition](https://www.jetbrains.com/idea/download/)
### 3 Add the Community Edition as global IntelliJ Platform Plugin SDK
 ![Add the Community Edition as IntelliJ Platform Plugin SDK](/images/sdk1.png)
### 4 Checkout power mode II Repository from github
### 5 Create a project from existing sources but dont import the default module or libraries.
 Then create a plugin module in the project root folder (the Power Mode II repo folder. don't create a subfolder).
 Add the Scala SDK in Dependencies.
 
![create a plugin project](/images/plugin1.png)           

![create a plugin project](/images/plugin2.png)

### 6 Add a plugin run configuration
![Add a plugin run configuration](/images/run1.png)
### 7 Press run button

This should start a new IntelliJ CE Instance with PowerMode II plugin installed and independent sandbox configuration. 
 
### 8 Build the installable plugin zip file
 Hit `Build -> Prepare Plugin Module for Deployment`
 
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
