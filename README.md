# DEPRECATED

see https://developer.android.com/training/basics/intents/result

# CoroutineActivityResult

Android library for starting activity and get the result using kotlin coroutines.

## Setup
Add the JitPack repository in your root build.gradle at the end of repositories:
```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}
```

Add the dependency in your build.gradle of the module:
```gradle
dependencies {
    implementation 'com.github.komitake:CoroutineActivityResult:v1.0.0'
}
```

## Usage

Call `activityResult` in coroutine scope of Main dispatcher in Activity so that you obitain the result as return value.

```kotlin
button.setOnClickListener {
    GlobalScope.launch(Dispatchers.Main) {
        val getContentIntent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" } // your intent
        val result = activityResult(getContentIntent)
        if (result is ActivityResult.Ok) {
            val uri: Uri = result.data.data
        }
    }
}
```
