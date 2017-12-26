package com.github.gmazzo.gradle.plugins

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import groovy.xml.MarkupBuilder
import org.gradle.api.Plugin
import org.gradle.api.Project

public class FontAwesomePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (!project.plugins.any { p -> p instanceof AppPlugin || p instanceof LibraryPlugin }) {
            throw new IllegalStateException('The \'com.android.application\' plugin is required.')
        }

        def extension = project.extensions.create('fontawesome', FontAwesomePluginExtension)

        project.with {
            def resourcesDir = "$buildDir/generated/res/fontawesome"
            def drawablesDir = file("$resourcesDir/drawable")
            def stringResFile = file("$resourcesDir/values/fontawesome.xml")

            def baseUrl = 'https://raw.githubusercontent.com/FortAwesome/Font-Awesome/master/fonts'
            def svgFontUrl = "$baseUrl/fontawesome-webfont.svg"
            def svgFontFile = file("$buildDir/fontawesome-webfont.svg")
            def ttfFontUrl = "$baseUrl/fontawesome-webfont.ttf"
            def ttfFontFile = file("$resourcesDir/font/fontawesome.ttf")

            tasks.create('fontawesomeDownloadFontSvg') {
                inputs.property 'url', svgFontUrl
                outputs.file svgFontFile

                doFirst {
                    svgFontFile.parentFile.mkdirs()
                    svgFontFile.newOutputStream() << new URL(svgFontUrl).openStream()
                }
            }

            tasks.create('fontawesomeGenerateGlyphsResValues') {
                inputs.file svgFontFile
                outputs.dir drawablesDir
                outputs.file stringResFile
                dependsOn fontawesomeDownloadFontSvg

                doFirst {
                    drawablesDir.mkdirs()
                    stringResFile.parentFile.mkdirs()

                    def resValues = [:]

                    def parser = new XmlSlurper()
                    parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
                    parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

                    def svg = parser.parseText(svgFontFile.text)

                    def bbox = svg.defs.font.'font-face'.'@bbox'.text().tokenize(' ')*.toDouble()
                    def width = bbox[2] - bbox[0]
                    def height = bbox[3] - bbox[1]
                    def defOffsetX = (svg.defs.font.'@horiz-adv-x'.text() ?: 0) as double

                    svg.defs.font.glyph.each { glyph ->
                        def glyphName = glyph.'@glyph-name'.text()
                        def d = glyph.'@d'.text()

                        if (d && extension.shouldIncludeGlyph(glyphName)) {
                            def resName = extension.glyphToResourceName(glyphName)
                            def file = file("$drawablesDir/ic_glyph_${resName}.xml")

                            def offsetX = (glyph.'@horiz-adv-x'.text() ?: defOffsetX) as double

                            def xml = new MarkupBuilder(file.newWriter())
                            xml.mkp.xmlDeclaration version: '1.0', encoding: 'UTF-8'
                            xml.vector(
                                    'xmlns:android': 'http://schemas.android.com/apk/res/android',
                                    'android:width': '24dp',
                                    'android:height': '24dp',
                                    'android:viewportWidth': width,
                                    'android:viewportHeight': height) {
                                group(
                                        'android:scaleX': 1,
                                        'android:scaleY': -height / width,
                                        'android:pivotX': width / 2,
                                        'android:pivotY': height / 2) {
                                    group(
                                            'android:translateX': -bbox[0] + (offsetX ? (width - offsetX) / 2 : 0),
                                            'android:translateY': -bbox[1]) {
                                        path('android:fillColor': '#000', 'android:pathData': d)
                                    }
                                }
                            }

                            resValues.put(resName, glyph.'@unicode')
                        }
                    }

                    def xml = new MarkupBuilder(stringResFile.newWriter())
                    xml.mkp.xmlDeclaration version: '1.0', encoding: 'UTF-8'
                    xml.resources() {
                        resValues.each { k, v ->
                            string(name: "glyph_$k", translatable: false, v)
                        }
                    }
                }
            }

            tasks.create('fontawesomeGenerateFontResValues') {
                inputs.property 'url', ttfFontUrl
                outputs.file ttfFontFile

                doFirst {
                    ttfFontFile.parentFile.mkdirs()
                    ttfFontFile.newOutputStream() << new URL(ttfFontUrl).openStream()
                }
            }

            android {
                sourceSets.main.res.srcDirs resourcesDir

                (it.hasProperty('applicationVariants') ? applicationVariants : libraryVariants).all {
                    tasks["generate${it.name.capitalize()}ResValues"].dependsOn fontawesomeGenerateFontResValues, fontawesomeGenerateGlyphsResValues
                }
            }
        }
    }

}