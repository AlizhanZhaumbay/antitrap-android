package com.example.exit_plan

import android.app.Application

class AntitrapApp : Application() {
    val container: AppContainer by lazy { AppContainer() }
}
