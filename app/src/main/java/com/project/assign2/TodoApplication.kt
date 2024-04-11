package com.project.assign2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Annotates the application class to enable Hilt for dependency injection
@HiltAndroidApp
class TodoApplication : Application() {
    // No custom logic is needed in this class
}
