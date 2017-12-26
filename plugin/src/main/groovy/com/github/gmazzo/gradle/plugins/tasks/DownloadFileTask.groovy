package com.github.gmazzo.gradle.plugins.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

public class DownloadFileTask extends DefaultTask {

    @Input
    URL url

    @OutputFile
    File outFile

    @TaskAction
    void perform() {
        outFile.parentFile.mkdirs()
        outFile.newOutputStream() << url.openStream()
    }

}
