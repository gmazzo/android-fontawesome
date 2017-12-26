package com.github.gmazzo.gradle.plugins.tasks

import groovy.util.slurpersupport.GPathResult
import groovy.xml.MarkupBuilder
import org.gradle.api.tasks.OutputDirectory

public class GenerateFontDrawablesTask extends ProcessSVGFontTask {

    @OutputDirectory
    File outputDir

    double[] bbox
    double width, height, defOffsetX

    @Override
    void perform() {
        bbox = null
        super.perform()
    }

    @Override
    void onGlyphFound(GPathResult svg, GPathResult glyph, String glyphName, String glyphPath, String resName, String resValue) {
        if (!bbox) {
            bbox = svg.defs.font.'font-face'.'@bbox'.text().tokenize(' ')*.toDouble()
            width = bbox[2] - bbox[0]
            height = bbox[3] - bbox[1]
            defOffsetX = (svg.defs.font.'@horiz-adv-x'.text() ?: 0) as double
        }

        def drawableName = extension.drawableResourceName(resName)
        def file = new File(outputDir, "${drawableName}.xml")

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
                    path('android:fillColor': '#000', 'android:pathData': glyphPath)
                }
            }
        }
    }

}
