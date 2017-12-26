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

        project.with {
            def resourcesDir = "$buildDir/generated/res/fontawesome"
            def drawablesDir = file("$resourcesDir/drawable")
            def stringResFile = file("$resourcesDir/values/fontawesome.xml")

            def baseUrl = new URL('https://raw.githubusercontent.com/FortAwesome/Font-Awesome/master/fonts')
            def svgFontUrl = new URL("$baseUrl/fontawesome-webfont.svg")
            def svgFontFile = file("$buildDir/intermediates/fontawesome/webfont.svg")
            def ttfFontUrl = new URL("$baseUrl/fontawesome-webfont.ttf")
            def ttfFontFile = file("$resourcesDir/font/fontawesome.ttf")

            tasks.create('downloadFontAwesomeSvg', DownloadFileTask) {
                url = svgFontUrl
                outFile = svgFontFile
            }

            tasks.create('generateFontAwesomeResFont', DownloadFileTask) {
                onlyIf { extension.generateFontResource }

                url = ttfFontUrl
                outFile = ttfFontFile
            }

            tasks.create('generateFontAwesomeResValues', GenerateFontStringResourcesTask) {
                onlyIf { extension.generateFontResource }
                dependsOn downloadFontAwesomeSvg

                it.extension = extension
                it.svgFont = svgFontFile
                it.outputFile = stringResFile
            }

            tasks.create('generateFontAwesomeResDrawables', GenerateFontDrawablesTask) {
                onlyIf { extension.generateDrawableGlyphsResources }
                dependsOn downloadFontAwesomeSvg

                it.extension = extension
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
                        tasks["generate${it.name.capitalize()}ResValues"].dependsOn generateFontAwesomeResFont, generateFontAwesomeResValues, generateFontAwesomeResDrawables
                    }
                }
            }
        }
    }

}
