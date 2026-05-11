@echo off
setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
for %%R in ("%SCRIPT_DIR%..") do set "ROOT_DIR=%%~fR\"
set "VERSIONS_FILE=%SCRIPT_DIR%target_versions.txt"
set "TARGET_SUBDIR=build\libs"

if not exist "%VERSIONS_FILE%" (
    echo [ERROR] target_versions.txt not found.
    exit /b 1
)

set /a SUCCESS=0
set /a FAILED=0
echo ============================================
echo  Starting Deletion
echo ============================================
echo.

for /f "usebackq delims=" %%A in ("%VERSIONS_FILE%") do (
    call :ProcessLine "%%A"
)

echo ============================================
echo  Deletion Summary
echo ============================================
echo  Succeeded : %SUCCESS%
echo  Failed    : %FAILED%
echo ============================================
echo.

exit /b 0

:: -----------------------------------------------
:ProcessLine
set "REL_PATH=%~1"

REM Skip empty lines and comments
if "%REL_PATH%"=="" exit /b 0
if "%REL_PATH:~0,1%"=="#" exit /b 0

REM Normalize slashes
set "REL_PATH=%REL_PATH:/=\%"

for %%D in ("%ROOT_DIR%Versions\%REL_PATH%") do set "PROJECT_DIR=%%~fD"

set "LIBS_DIR=%PROJECT_DIR%\%TARGET_SUBDIR%"

echo [DELETE] %REL_PATH%

if not exist "%PROJECT_DIR%" (
    echo [ERROR] Directory not found: %PROJECT_DIR%
    set /a FAILED+=1
    echo.
    exit /b 0
)

if not exist "%LIBS_DIR%" (
    echo [WARN] build\libs not found, skipping: %LIBS_DIR%
    set /a FAILED+=1
    echo.
    exit /b 0
)

REM Delete files in libs dir
del /q "%LIBS_DIR%\*" >nul 2>&1

REM Delete any subdirectories
for /d %%D in ("%LIBS_DIR%\*") do (
    rd /s /q "%%D" >nul 2>&1
)

echo [OK] Cleared contents of %LIBS_DIR%
set /a SUCCESS+=1
echo.
exit /b 0