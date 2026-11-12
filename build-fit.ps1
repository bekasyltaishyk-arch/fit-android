$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
& .\gradlew.bat assembleDebug testDebugUnitTest @args
exit $LASTEXITCODE
