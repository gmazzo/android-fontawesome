package com.github.gmazzo.gradle.plugins.tasks

import groovy.util.slurpersupport.GPathResult
import groovy.xml.MarkupBuilder
import org.gradle.api.tasks.OutputDirectory

public class GenerateFontDrawablesTask extends ProcessSVGFontTask {

    @OutputDirectory
    File outputDir

    double unitsPerEm, descent, defWidth

    @Override
    void onGlyphFound(GPathResult svg, GPathResult glyph, String glyphName, String glyphPath, String resName, String resValue) {
        if (!unitsPerEm) {
            def face = svg.defs.font.'font-face'
            unitsPerEm = (face.'@units-per-em'.text() ?: 0) as double
            descent = (face.'@descent'.text() ?: 0) as double
            defWidth = (svg.defs.font.'@horiz-adv-x'.text() ?: unitsPerEm) as double
        }

        def drawableName = extension.drawableResourceName(resName)
        def file = new File(outputDir, "${drawableName}.xml")

        def height = unitsPerEm
        def width = (glyph.'@horiz-adv-x'.text() ?: defWidth) as double

        def xml = new MarkupBuilder(file.newWriter())
        xml.mkp.xmlDeclaration version: '1.0', encoding: 'UTF-8'
        xml.vector(
                'xmlns:android': 'http://schemas.android.com/apk/res/android',
                'android:width': "${width * 24 / height}dp",
                'android:height': '24dp',
                'android:viewportWidth': width,
                'android:viewportHeight': height) {
            group(
                    'android:scaleX': 1,
                    'android:scaleY': -1,
                    'android:pivotX': width / 2,
                    'android:pivotY': height / 2) {
                group(
                        'android:translateX': 0,
                        'android:translateY': descent) {
                    path('android:fillColor': '#000', 'android:pathData': glyphPath)
                }
            }
        }
    }

}
