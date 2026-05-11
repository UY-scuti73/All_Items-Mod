@echo off
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
for %%R in ("%SCRIPT_DIR%..") do set "ROOT_DIR=%%~fR\"
set "VERSIONS_FILE=%SCRIPT_DIR%target_versions.txt"
set "VERSION_TXT=%SCRIPT_DIR%version.txt"
set "OUTPUT_DIR=%ROOT_DIR%Output Builds"

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

REM === clear Output Builds before copying anything ===
echo [INFO] Clearing existing files in "%OUTPUT_DIR%"
del /q "%OUTPUT_DIR%\*" >nul 2>&1

if not exist "%VERSIONS_FILE%" (
    echo [ERROR] target_versions.txt not found.
    pause
    exit /b 1
)
if not exist "%VERSION_TXT%" (
    echo [ERROR] version.txt not found next to the script.
    pause
    exit /b 1
)

set /a SUCCESS=0
set /a FAILED=0
echo ============================================
echo  Starting Gradle Builds
echo ============================================
echo.

for /f "usebackq delims=" %%A in ("%VERSIONS_FILE%") do (
    call :ProcessLine "%%A"
)

echo ============================================
echo  Build Summary
echo ============================================
echo  Succeeded : %SUCCESS%
echo  Failed    : %FAILED%
echo ============================================
echo.
pause
exit /b 0

:: -----------------------------------------------
:ProcessLine
set "REL_PATH=%~1"
:: Skip empty lines and comments
if "%REL_PATH%"=="" exit /b 0
if "%REL_PATH:~0,1%"=="#" exit /b 0

:: Normalize slashes
set "REL_PATH=%REL_PATH:/=\%"

for %%D in ("%ROOT_DIR%Versions\%REL_PATH%") do set "PROJECT_DIR=%%~fD"

echo [BUILD] %REL_PATH%

if not exist "%PROJECT_DIR%" (
    echo [ERROR] Directory not found: %PROJECT_DIR%
    set /a FAILED+=1
    echo.
    exit /b 0
)

:: Find gradle wrapper location
set "GRADLE_DIR="
if exist "%PROJECT_DIR%\gradlew.bat" (
    set "GRADLE_DIR=%PROJECT_DIR%"
) else if exist "%PROJECT_DIR%\..\gradlew.bat" (
    for %%D in ("%PROJECT_DIR%\..") do set "GRADLE_DIR=%%~fD"
) else (
    echo [ERROR] Could not find gradlew.bat for %REL_PATH%
    set /a FAILED+=1
    echo.
    exit /b 0
)

pushd "%GRADLE_DIR%"
call gradlew.bat build -x test --project-dir "%PROJECT_DIR%"
set "BUILD_RESULT=%errorlevel%"
popd

if %BUILD_RESULT% neq 0 (
    echo [ERROR] Build failed for %REL_PATH% ^(exit code %BUILD_RESULT%^)
    set /a FAILED+=1
    echo.
    exit /b 0
)

:: -----------------------------------------------
:: Read version.txt (script folder)
set "MOD_NAME="
set "MOD_VERSION="
set /a LINE_NUM=0
for /f "usebackq delims=" %%L in ("%VERSION_TXT%") do (
    if !LINE_NUM! EQU 0 set "MOD_NAME=%%L"
    if !LINE_NUM! EQU 1 set "MOD_VERSION=%%L"
    set /a LINE_NUM+=1
)

:: Split loader + MC version from path
for /f "tokens=1,2 delims=\" %%X in ("%REL_PATH%") do (
    set "LOADER=%%X"
    set "MC_VERSION=%%Y"
)

set "RENAME_PREFIX=!MOD_NAME!-!LOADER!-!MC_VERSION!-!MOD_VERSION!"
set "LIBS_DIR=%PROJECT_DIR%\build\libs"
set /a COPIED=0

for /f "delims=" %%F in ('dir /b /a-d "%LIBS_DIR%\*.jar" 2^>nul') do (
    call :CopyJar "%LIBS_DIR%\%%F" "%%~nF"
)

if !COPIED! EQU 0 (
    echo [WARN] No matching JARs found in %LIBS_DIR%
    set /a FAILED+=1
) else (
    set /a SUCCESS+=1
)

echo.
exit /b 0

:: -----------------------------------------------
:CopyJar
set "JAR_PATH=%~1"
set "JAR_NAME=%~2"
set "LOWER_NAME=!JAR_NAME!"

:: quick substring checks (no findstr needed)
if not "!LOWER_NAME:-sources=!"=="!LOWER_NAME!" exit /b 0
if not "!LOWER_NAME:-dev=!"=="!LOWER_NAME!" exit /b 0

set "NEW_NAME=!RENAME_PREFIX!.jar"
copy /y "%JAR_PATH%" "%OUTPUT_DIR%\!NEW_NAME!" >nul
set /a COPIED+=1
exit /b 0