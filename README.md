[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.21-blue.svg?style=flat-square&logo=kotlin)](https://kotlinlang.org/)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-green.svg?style=flat-square&logo=gradle)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

![](screens/Knotif.png)

<!-- GETTING STARTED -->
## Getting Started
### Adding dependencies
- Add it in your `commonMain.dependencies` :
  ```
  implementation("io.github.shadmanadman:knotif:0.55.0")
  ```
  
### Message notification
Note: In Android and iOS you need to set your app_icon and app_name inside `AndroidManifest.xml` and `Info.plist` respectively.
```
    val messageData = KNotifMessageData(
        id = "1",
        title = "This is a test",
        appName = "Knotif",
        message = "This is a test message",
        poster = imageResource(Res.drawable.default_poster),
        appIcon = imageResource(Res.drawable.default_app_icon),
    )
    
    // Show the notification
    Knotif.show(messageData)
    
    // Dismiss the notification
    Knotif.dismiss(messageData.id)
    
    // Dismiss all notifications
    Knotif.dismissAll()
    
    // Set a listener to be called when a notification is clicked
    Knotif.setOnBuildMessageKnotifListener {
        println("notification clicked ${it}")
    }
```
#### How it looks
![](screens/1.png)


### Music notification
Note: This notification style for Android and iOS will be replaced with the new system music notification style in the future.
```
 val musicData = KNotifMusicData(
        id = "1",
        title = "This is a test",
        appName = "Knotif",
        icons = MusicIcons(
            poster = poster,
            playIcon = playIcon,
            pauseIcon = pauseIcon,
            nextIcon = nextIcon,
            previousIcon = previousIcon
        ),
        artist = "Artist test",
        isPlaying = true,
        appIcon = appIcon,
    )
    
    // Show the notification
    Knotif.show(musicData)
    
    // Dismiss the notification
    Knotif.dismiss(messageData.id)
    
    // Dismiss all notifications
    Knotif.dismissAll()
    
    // Set a listener
    Knotif.setOnBuildMusicKnotifListener(
    knotifClicked = {},
    nextClicked = {},
    previousClicked = {},
    playPauseClicked = {})
```
#### How it looks
![](screens/2.png)



### Progress notification
```
val progressData = KNotifProgressData(
        id = "1",
        title = "This is a test",
        description = "This is a test description",
        appName = "Knotif",
        progress = 50,
        appIcon = appIcon
    )
    
    // Show the notification
    Knotif.show(progressData)
    
    // Dismiss the notification
    Knotif.dismiss(progressData.id)
    
    // Dismiss all notifications
    Knotif.dismissAll()
    
    // Set a listener to be called when a notification is clicked
    Knotif.setOnBuildProgressKnotifListener {
        println("notification clicked ${it}")
    }
```

#### How it looks
![](screens/3.png)
