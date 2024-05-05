# Test-Automation-SeeingAI-
Test Automation using Appium and Selenium frameworks

## Overview
This project aims to automate testing of the SeeingAI application using a partial text matching approach. The automation script interacts with the application to perform various tasks and verifies the accuracy of the output against expected results.

## Features
- Automates testing of the SeeingAI application.
- Uses partial text matching to verify the accuracy of the application's output.
- Reads expected results from a file for comparison.
- Calculates and displays the pass rate of the tests.

## Requirements
- Java Development Kit (JDK)
- Appium
- Android SDK
- Appium Server
- SeeingAI application APK file
- Test device/emulator

## Picture-demo
<img src="https://github.com/umangptl/Test-Automation-SeeingAI/blob/main/Picture-demo/Pineapple.png" alt="Pinapple-demo" width="200" height="350">

<img src="https://github.com/umangptl/Test-Automation-SeeingAI/blob/main/Picture-demo/Scripting.png" alt="Scripting-demo" width="500" height="320">



## Example Expected Results File
```
A group of green peppers
A shelf with vegetables
A group of purple plums
...
```

## Example Expected Output
```
At Home
In Photos App
Sharing Image to SeeingAI
Expected Result: A Group of green peppers
Actual Result: A group of green peppers in a market
Pass
swiped to next Image--
Sharing Image to SeeingAI
Expected Result: A bunch of asparagus
Actual Result: A grocery store with produce
Fall
swiped to next Image--
Test Pass: 
Pass %: 
```

### Educational Purpose
The project is implemented for educational purposes during a course work for a class. It is intended to provide practical experience with software testing methodologies and automation techniques.
