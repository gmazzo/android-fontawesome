package com.github.gmazzo.gradle.plugins

class FontAwesomePluginExtension {

    /**
     * A set of font-styles to include when processing the font. By default includes all styles.
     */
    private FontStyle[] includeStyles = FontStyle.values()

    /**
     * A set of glyphs to include when processing the font. By default includes all resources.
     */
    private String[] includeGlyphs

    /**
     * A glyphs name Pattern (RexExp) to include when processing the font. By default includes all resources.
     */
    private String includeGlyphsPattern

    /**
     * A set of glyphs to exclude when processing the font. By default excludes none.
     */
    private String[] excludeGlyphs

    /**
     * A glyphs name Pattern (RexExp) to exclude when processing the font. By default excludes none.
     */
    private String excludeGlyphsPattern

    /**
     * Computes if it should be included as a resource or not.
     * By default executes the logic of <code>includeGlyphs</code>, <code>includeGlyphsPattern</code>, <code>excludeGlyphs</code> and <code>excludeGlyphsPattern</code>.
     */
    private Closure<Boolean> shouldIncludeGlyph = { String glyphName ->
        return (!includeGlyphs || includeGlyphs.contains(glyphName)) &&
                (!includeGlyphsPattern || glyphName.matches(includeGlyphsPattern)) &&
                (!excludeGlyphs || !excludeGlyphs.contains(glyphName)) &&
                (!excludeGlyphsPattern || !glyphName.matches(excludeGlyphsPattern))
    }

    /**
     * Computes the Android's resource name for a given font style and glyph.
     */
    private Closure<String> glyphToResourceName = { String fontStyle, String glyphName -> "glyph_${fontStyle}_${glyphName.toLowerCase().replaceAll('[^a-z0-9_]+', '_')}" }

    /**
     * Computes the Drawable's resource name for a given glyph Android's resource name. By default it prefixes an "ic_".
     */
    private Closure<String> drawableResourceName = { String name -> "ic_$name" }

    /**
     * A flag to indicate if the <code>@font/fontawesome</code> should be generated. Defaults to <code>true</code>
     */
    private boolean generateFontResource = true

    /**
     * A flag to indicate if the <code>@drawable/ic_glyph_XXX</code> should be generated. Defaults to <code>true</code>
     */
    private boolean generateDrawableGlyphsResources = true

    FontStyle[] getIncludeStyles() {
        return includeStyles
    }

    void setIncludeStyles(FontStyle[] includeStyles) {
        this.includeStyles = includeStyles
    }

    String[] getIncludeGlyphs() {
        return includeGlyphs
    }

    void setIncludeGlyphs(String[] includeGlyphs) {
        this.includeGlyphs = includeGlyphs
    }

    String getIncludeGlyphsPattern() {
        return includeGlyphsPattern
    }

    void setIncludeGlyphsPattern(String includeGlyphsPattern) {
        this.includeGlyphsPattern = includeGlyphsPattern
    }

    String[] getExcludeGlyphs() {
        return excludeGlyphs
    }

    void setExcludeGlyphs(String[] excludeGlyphs) {
        this.excludeGlyphs = excludeGlyphs
    }

    String getExcludeGlyphsPattern() {
        return excludeGlyphsPattern
    }

    void setExcludeGlyphsPattern(String excludeGlyphsPattern) {
        this.excludeGlyphsPattern = excludeGlyphsPattern
    }

    Closure<Boolean> getShouldIncludeGlyph() {
        return shouldIncludeGlyph
    }

    void setShouldIncludeGlyph(Closure<Boolean> shouldIncludeGlyph) {
        this.shouldIncludeGlyph = shouldIncludeGlyph
    }

    Closure<String> getGlyphToResourceName() {
        return glyphToResourceName
    }

    void setGlyphToResourceName(Closure<String> glyphToResourceName) {
        this.glyphToResourceName = glyphToResourceName
    }

    Closure<String> getDrawableResourceName() {
        return drawableResourceName
    }

    void setDrawableResourceName(Closure<String> drawableResourceName) {
        this.drawableResourceName = drawableResourceName
    }

    boolean getGenerateFontResource() {
        return generateFontResource
    }

    void setGenerateFontResource(boolean generateFontResource) {
        this.generateFontResource = generateFontResource
    }

    boolean getGenerateDrawableGlyphsResources() {
        return generateDrawableGlyphsResources
    }

    void setGenerateDrawableGlyphsResources(boolean generateDrawableGlyphsResources) {
        this.generateDrawableGlyphsResources = generateDrawableGlyphsResources
    }

}
