@rem BatChat
@echo off
cls

rem ================================================

del apks.apks
rem gradlew :app:bundleDebug
java -jar bundletool.jar build-apks --local-testing --bundle app/build/outputs/bundle/debug/app-debug.aab --output apks.apks
java -jar bundletool.jar install-apks --apks apks.apks