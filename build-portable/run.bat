@echo off
setlocal enabledelayedexpansion

REM Script to run dcm4che storescp listener and then start Karnak
REM Usage: run.bat [OPTIONS]
REM   --ae-title <title>       AE title for storescp (default: KARNAK-LOCAL)
REM   --port <port>            Port for storescp (default: 11115)
REM   --dicom-dir <path>       DICOM storage directory (default: .\dicom)
REM   --filepath <pattern>     File path pattern (default: {00100010}/{00080060}/{0020000E}/{00080018}.dcm)
REM   --force                  Force stop existing storescp instances
REM   --help                   Show this help message

REM Default values
set "AE_TITLE=KARNAK-LOCAL"
set "PORT=11115"
set "DICOM_DIR=.\dicom"
set "FILEPATH_PATTERN={00100010}/{00080060}/{0020000E}/{00080018}.dcm"
set "FORCE_STOP=false"
set "STORESCP_PID="
set "KARNAK_PID="

REM Parse arguments
:parse_args
if "%~1"=="" goto args_done
if "%~1"=="--ae-title" (
    set "AE_TITLE=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--port" (
    set "PORT=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--dicom-dir" (
    set "DICOM_DIR=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--filepath" (
    set "FILEPATH_PATTERN=%~2"
    shift
    shift
    goto parse_args
)
if "%~1"=="--force" (
    set "FORCE_STOP=true"
    shift
    goto parse_args
)
if "%~1"=="--help" (
    echo Usage: run.bat [OPTIONS]
    echo   --ae-title ^<title^>       AE title for storescp (default: KARNAK-LOCAL)
    echo   --port ^<port^>            Port for storescp (default: 11115)
    echo   --dicom-dir ^<path^>       DICOM storage directory (default: .\dicom)
    echo   --filepath ^<pattern^>     File path pattern (default: {00100010}/{00080060}/{0020000E}/{00080018}.dcm)
    echo   --force                  Force stop existing storescp instances
    echo   --help                   Show this help message
    exit /b 0
)
echo ERROR: Unknown option: %~1. Use --help for usage information.
exit /b 1

:args_done

REM Set the application directory
set "APP_DIR=%~dp0"
set "APP_BIN=%APP_DIR%Karnak\bin"

REM Add dcm4che bin directory to PATH
set "DCM4CHE_BIN=%APP_BIN%\dcm4che\bin"
if not exist "%DCM4CHE_BIN%" (
    echo ERROR: dcm4che bin directory not found at '%DCM4CHE_BIN%'
    exit /b 1
)
set "PATH=%DCM4CHE_BIN%;%PATH%"

REM Check if storescp exists
where storescp >nul 2>&1
if errorlevel 1 (
    echo ERROR: storescp not found in PATH. Ensure dcm4che is installed.
    exit /b 1
)

REM Check if Karnak executable exists
set "KARNAK_BIN=%APP_BIN%\Karnak.exe"
if not exist "%KARNAK_BIN%" (
    echo ERROR: Karnak executable not found at '%KARNAK_BIN%'
    exit /b 1
)

REM Check for existing storescp processes
call :check_existing_storescp

REM Create dicom directory if it doesn't exist
if not exist "%DICOM_DIR%" mkdir "%DICOM_DIR%"
echo [run.bat] DICOM storage directory: '%DICOM_DIR%'

REM Start storescp listener in background
echo [run.bat] Starting dcm4che storescp listener on %AE_TITLE%:%PORT%
start "storescp" /B cmd /c "storescp -b "%AE_TITLE%:%PORT%" --directory "%DICOM_DIR%" --filepath "%FILEPATH_PATTERN%""

REM Give storescp a moment to initialize
timeout /t 2 /nobreak >nul

REM Find storescp PID
for /f "tokens=2" %%i in ('tasklist /FI "WINDOWTITLE eq storescp" /FO LIST ^| findstr "PID:"') do set "STORESCP_PID=%%i"
if not defined STORESCP_PID (
    echo ERROR: Failed to start storescp
    exit /b 1
)
echo [run.bat] storescp started with PID: %STORESCP_PID%

REM Setup cleanup handler
set "CLEANUP_SCRIPT=%TEMP%\karnak_cleanup_%RANDOM%.bat"
(
    echo @echo off
    echo taskkill /F /PID %STORESCP_PID% 2^>nul
    echo for /f "tokens=2" %%%%i in ('netstat -ano ^| findstr ":%PORT% "') do taskkill /F /PID %%%%i 2^>nul
) > "%CLEANUP_SCRIPT%"

REM Register cleanup on exit
reg add "HKCU\Software\Microsoft\Command Processor" /v AutoRun /t REG_SZ /d "if exist \"%CLEANUP_SCRIPT%\" call \"%CLEANUP_SCRIPT%\" & del \"%CLEANUP_SCRIPT%\"" /f >nul 2>&1

REM Run Karnak
echo [run.bat] Starting Karnak from '%KARNAK_BIN%'
"%KARNAK_BIN%"

REM Cleanup
call :cleanup
exit /b 0

REM Function to check existing storescp processes
:check_existing_storescp
set "EXISTING_PIDS="
for /f "tokens=2,5" %%a in ('netstat -ano ^| findstr ":%PORT% "') do (
    set "EXISTING_PIDS=!EXISTING_PIDS! %%b"
)

if defined EXISTING_PIDS (
    echo [run.bat] Found existing process(es) on port %PORT%:
    for %%p in (%EXISTING_PIDS%) do (
        tasklist /FI "PID eq %%p" /FO TABLE
    )

    if "%FORCE_STOP%"=="true" (
        echo [run.bat] Stopping existing process(es)...
        for %%p in (%EXISTING_PIDS%) do taskkill /F /PID %%p 2>nul
        timeout /t 2 /nobreak >nul
        echo [run.bat] Existing process(es) stopped
    ) else (
        set /p "REPLY=Do you want to stop the existing process(es)? (y/n): "
        if /i "!REPLY!"=="y" (
            echo [run.bat] Stopping existing process(es)...
            for %%p in (%EXISTING_PIDS%) do taskkill /F /PID %%p 2>nul
            timeout /t 2 /nobreak >nul
            echo [run.bat] Existing process(es) stopped
        ) else (
            echo ERROR: Cannot start storescp - port %PORT% is already in use. Use --force to automatically stop existing processes.
            exit /b 1
        )
    )
)
exit /b 0

REM Cleanup function
:cleanup
echo [run.bat] Shutting down services...

REM Stop storescp
if defined STORESCP_PID (
    echo [run.bat] Shutting down storescp (PID: %STORESCP_PID%)
    taskkill /PID %STORESCP_PID% 2>nul
    timeout /t 2 /nobreak >nul
    taskkill /F /PID %STORESCP_PID% 2>nul
)

REM Clean up any remaining processes on the port
for /f "tokens=5" %%p in ('netstat -ano ^| findstr ":%PORT% "') do (
    echo [run.bat] Cleaning up remaining process: %%p
    taskkill /F /PID %%p 2>nul
)

REM Delete cleanup script
if exist "%CLEANUP_SCRIPT%" del "%CLEANUP_SCRIPT%"

echo [run.bat] Cleanup complete
exit /b 0
