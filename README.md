# android-fontawesome
An Android library port of [Font-Awesome](https://github.com/FortAwesome/Font-Awesome).

## How it works
This library re-compiles `Font-Awesome` to provide:
- A ready to use set of `VectorDrawable` icons, check for any `@drawable/ic_glyph_*`.
- A ready to use `font` resource, to be applied on `TextView`'s `android:fontFamily="@font/fontawesome"` attribute.

Note: if you are targeting API 25 or below, you have to setup `AppCompat` library as well to allow `android:fontFamily` attribute to work.

## Import
### As a Gradle plugin
On your `build.gradle` add:
```groovy
plugins {
    id 'com.github.gmazzo.fontawesome' version '0.1'
}
```
Check [https://plugins.gradle.org/plugin/com.github.gmazzo.fontawesome](https://plugins.gradle.org/plugin/com.github.gmazzo.fontawesome) for other instructions

Pros|Cons
----|----
Library is updated on each build|Higher build time if many resources (or all) are used
You have control of which resources are imported|

### As a Gradle dependency
On your `build.gradle` add:
```groovy
    dependencies {
        implementation 'com.github.gmazzo:fontawesome:0.1'
    }
```
[ ![Download](https://api.bintray.com/packages/gmazzo/maven/android-fontawesome/images/download.svg) ](https://bintray.com/gmazzo/maven/android-fontawesome/_latestVersion)

Pros|Cons
----|----
Ready to use library|As it's pre-built, it may be outdated against latest `Font-Awesome` release.
Improved build time|

## Usage
Referencing Glyphs as `Drawables` inside an `ImageView` (or any other drawable capable view)
```xml
<ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_glyph_hospital" />
```

Referencing Glyphs as inside an `TextView`
```xml
<TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:fontFamily="@font/fontawesome"
        android:text="@string/glyph_hospital" />
```
## Configuring the plugin (Plugin only)
The plugins add the `fontawesome` closure to your project:

Tag|Type|Usage
---|----|-----
includeGlyphs|list of `String`|A set of glyphs to include when processing the font. By default includes all resources
excludeGlyphs|list of `String`|A set of glyphs to exclude when processing the font. By default excludes none
includeGlyphsPattern|a RegExp|A glyphs name Pattern (RexExp) to include when processing the font. By default includes all resources
excludeGlyphsPattern|a RegExp|A glyphs name Pattern (RexExp) to exclude when processing the font. By default excludes none

### Example: include just 3 resources
On your `build.gradle` add:
```groovy
fontawesome {
    includeGlyphs = [ 'glass', 'music', 'search' ]
}
```

## Reducing APK size (AAR library only)
This library will add **tons** of new `drawable` resources, one per glyph on the **Awesome** font. It's about +4MB.
If you have ProGuard enabled (and you should), add a `shrinkResources true` line to your release configuration:
```groovy
buildTypes {
    release {
        shrinkResources true
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
    }
}
```
This setting will tell to the packager just to keep the resources referenced by code or by xml, and delete everithing else.

## Demo
![Screenshot](sample/screenshot.png)
