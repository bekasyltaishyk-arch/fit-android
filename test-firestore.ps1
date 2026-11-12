$ErrorActionPreference = 'Stop'
if ($env:JAVA_HOME) {
    $env:PATH = (Join-Path $env:JAVA_HOME 'bin') + [IO.Path]::PathSeparator + $env:PATH
}
Set-Location (Join-Path $PSScriptRoot 'tests\firestore')
& npm.cmd run test:emulator
exit $LASTEXITCODE
