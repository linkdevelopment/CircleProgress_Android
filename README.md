# CircleProgress
Circular progress is android library to create circle progress which useful in

1.	Show percentages
2.	Count downs,etc

## Setup

Add the dependency in your app build.gradle:
```
implementation  'io.card:android-sdk:5.5.1' todo will be changed
```
Add Jitpack in your root build.gradle at the end of repositories:
```
 allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
## Usage
Basic usage
```xml
<com.linkdev.circleprogress.CircularProgress
        android:id="@+id/progressCircular"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:progress="50"
       />
```
Usage for all attributes
```xml
    <com.linkdev.circleprogress.CircularProgress
        android:id="@+id/progressCircular"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_gravity="center_horizontal"
        app:innerCircleBackground="#ff00ff"
        app:max="100"
        app:outerStrokeColor="#797474"
        app:outerStrokeThickness="1dp"
        app:progress="75"
        app:text="hello world"
        app:progressDirection="Clockwise"
        app:progressRoundedEnd="false"
        app:progressStrokeColor="#ff0000"
        app:progressStrokeThickness="5dp"
        app:showDecimalZero="false"
        app:startAngle="Top"
        app:textColor="#000000"
        app:textDisplay="ProgressPercentage"
        app:textSize="12sp" />
```
![](images/circle_progress.gif)

#### Set Max
```xml
 app:max="100"
```
```xml
 app:max="@integer/max"
```
```kotlin
  progressCircular.setMax(100)
```
#### Set inner circle background
```xml
 app:innerCircleBackground="#ff0000"
```
```kotlin
  progressCircular.setInnerCircleBackground(Color.BLUE)
  progressCircular.setInnerCircleBackground(Color.parseColor("#00ffff"))
  progressCircular.setInnerCircleBackground(ResourcesCompat.getColor(
                        resources,
                        R.color.green,
                        null
                    ))
```
#### Set Outer Stroke Color
```xml
 app:outerStrokeColor="#ff0000"
```
```kotlin
  progressCircular.setOuterStrokeColor(Color.BLUE)
```

#### Set Outer Stroke Thickness
```xml
 app:outerStrokeThickness="5dp"
```
```kotlin
  progressCircular.setOuterStrokeThickness(5)
```
#### Set Progress
```xml
 app:progress="57"
```
Progress value is set as float
```kotlin
  progressCircular.setProgress(57f)
```
#### Set Progress Direction
```xml
 app:progressDirection="Clockwise"
 ```
 ```kotlin
  progressCircular.setProgressDirection(ProgressDirection.CLOCKWISE)
```
 or
```xml
app:progressDirection="Anticlockwise"
```
 ```kotlin
  progressCircular.setProgressDirection(ProgressDirection.ANTICLOCKWISE)
```
#### Set Progress Rounded End
```xml
 app:progressRoundedEnd="true"
 ```
 ```kotlin
  progressCircular.setProgressRoundedEnd(false)
```
#### Set Progress Stroke Color
```xml
 app:progressStrokeColor="#ff0000"
```
```kotlin
  progressCircular.setProgressStrokeColor(Color.BLUE)
```

 #### Set Progress Stroke Thickness
```xml
 app:progressStrokeThickness="5dp"
```
```kotlin
  progressCircular.setProgressStrokeThickness(5)
```
#### Show Decimal Zero
if this is true it will display the int values as following 50.0 but if
it false it will be displayed as 50 and in both cases the float values
will be displayed normally
```xml
 app:showDecimalZero="false"
```
```kotlin
  progressCircular.setShowDecimalZero(true)
```
#### Start Angle
It is the start point of drawing the progress it can be drawn from top,
right, left and bottom
```xml
 app:startAngle="Top"
```
```kotlin
  progressCircular.setStartAngle(StartAngle.TOP)
```
or
```xml
 app:startAngle="Bottom"
```
```kotlin
  progressCircular.setStartAngle(StartAngle.BOTTOM)
```
or
```xml
 app:startAngle="Left"
```
```kotlin
  progressCircular.setStartAngle(StartAngle.LEFT)
```
or
```xml
 app:startAngle="Right"
```
```kotlin
  progressCircular.setStartAngle(StartAngle.RIGHT)
```
#### Text Color
```xml
 app:textColor="#000000"
```
```kotlin
  progressCircular.setTextColor(Color.Red)
```
#### Text Size
```xml
 app:textSize="12sp"
```
```kotlin
  progressCircular.setTextSize(12f)
```
#### Text Font
```kotlin
  progressCircular.setTextFont(R.font.custom_font)
```
#### Text
By default the displayed text is the percentage value of the progress
(progress / max ) *100 ,But we can display the progress value it self or
display no text or display any other text by the following
```xml
 app:textDisplay="ProgressPercentage"
```
```kotlin
progressCircular.setTextDisplay(TextDisplay.PROGRESS_PERCENTAGE)
```
or
```xml
 app:textDisplay="ProgressValue"
```
```kotlin
progressCircular.setTextDisplay(TextDisplay.PROGRESS_VALUE)
```
or
```xml
 app:textDisplay="NoText"
```
```kotlin
 progressCircular.setTextDisplay(TextDisplay.NO_TEXT)
```
or
```xml
 app:textDisplay="ProvidedText"
 app:text="hello world"
```
```kotlin
  progressCircular.setTextDisplay(TextDisplay.PROVIDED_TEXT)
  progressCircular.setText("hello world")
```
#### Attention
If you provide text to the circle progress but the textDisplay is set by
any value except provided text the text will be ignored

## Developed By
Ahmed Ezz
