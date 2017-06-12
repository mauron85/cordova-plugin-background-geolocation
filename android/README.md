For development, cordova lib is required. You can copy it from any cordova project.

1. copy content of cordova lib into `CordovaLib`
2. add `include ':CordovaLib'` into `settings.gradle`
3. add dependency `compile project(':CordovaLib')` in `plugin/build.grandle`

For more info visit https://cordova.apache.org/docs/en/2.5.0/guide/getting-started/ios/
