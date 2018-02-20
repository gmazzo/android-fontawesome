package com.github.gmazzo.gradle.plugins

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.github.gmazzo.gradle.plugins.tasks.DownloadFileTask
import com.github.gmazzo.gradle.plugins.tasks.GenerateFontDrawablesTask
import com.github.gmazzo.gradle.plugins.tasks.GenerateFontStringResourcesTask
import org.gradle.api.Plugin
import org.gradle.api.Project

public class FontAwesomePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def extension = project.extensions.create('fontawesome', FontAwesomePluginExtension)

        extension.includeStyles.toUnique().each { styleEnum ->
            def style = styleEnum.name().toLowerCase()
            def taskName = style.capitalize()

            project.with {
                def resourcesDir = "$buildDir/generated/res/fontawesome_$style"
                def drawablesDir = file("$resourcesDir/drawable")
                def stringResFile = file("$resourcesDir/values/fontawesome_${style}.xml")

                def baseUrl = new URL("https://raw.githubusercontent.com/FortAwesome/Font-Awesome/master/web-fonts-with-css/webfonts/${styleEnum.fontFile}")
                def svgFontUrl = new URL("${baseUrl}.svg")
                def svgFontFile = file("$buildDir/intermediates/fontawesome/webfont-${style}.svg")
                def ttfFontUrl = new URL("${baseUrl}.ttf")
                def ttfFontFile = file("$resourcesDir/font/fontawesome_${style}.ttf")

                def downloadTask = tasks.create("downloadFontAwesome${taskName}Svg", DownloadFileTask) {
                    url = svgFontUrl
                    outFile = svgFontFile
                }

                def generateFontTask = tasks.create("generateFontAwesome${taskName}ResFont", DownloadFileTask) {
                    onlyIf { extension.generateFontResource }

                    url = ttfFontUrl
                    outFile = ttfFontFile
                }

                def generateValuesTask = tasks.create("generateFontAwesome${taskName}ResValues", GenerateFontStringResourcesTask) {
                    onlyIf { extension.generateFontResource }
                    dependsOn downloadTask

                    it.extension = extension
                    it.fontStyle = style
                    it.svgFont = svgFontFile
                    it.outputFile = stringResFile
                }

                def generateDrawablesTask = tasks.create("generateFontAwesome${taskName}ResDrawables", GenerateFontDrawablesTask) {
                    onlyIf { extension.generateDrawableGlyphsResources }
                    dependsOn downloadTask

                    it.extension = extension
                    it.fontStyle = style
                    it.svgFont = svgFontFile
                    it.outputDir = drawablesDir
                }

                afterEvaluate {
                    if (!plugins.any { p -> p instanceof AppPlugin || p instanceof LibraryPlugin }) {
                        throw new IllegalStateException('The Android plugin is required.')
                    }

                    android {
                        sourceSets.main.res.srcDirs resourcesDir

                        (it.hasProperty('applicationVariants') ? applicationVariants : libraryVariants).all {
                            tasks["generate${it.name.capitalize()}ResValues"].dependsOn generateFontTask, generateValuesTask, generateDrawablesTask
                        }
                    }
                }
            }
        }
    }

}
