package com.ivanmorgillo.corsoandroid.teama.crashlytics

import timber.log.Timber

@Suppress("RedundantNullableReturnType")
class LineNumberDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "(${element.fileName}:${element.lineNumber})#${element.methodName}"
    }
}
