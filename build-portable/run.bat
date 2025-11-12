@echo off
setlocal EnableExtensions EnableDelayedExpansion

rem Defaults
set "CONFIG_FILE=run.cfg"

rem Parse args
:parse_args
if "%~1"=="" goto args_done
if /i "%~1"=="--config" (set "CONFIG_FILE=%~2" & shift & shift & goto parse_args)
if /i "%~1"=="--help" (call :show_help & exit /b 0)
echo ERROR: Unknown option: %~1. Use --help for usage information.
exit /b 1

:args_done

rem Setup paths
set "APP_DIR=%~dp0"
set "APP_BIN=%APP_DIR%Karnak"
set "KARNAK_BIN=%APP_BIN%\Karnak.exe"

rem Load configuration file
if exist "%CONFIG_FILE%" (
    echo [run.bat] Loading configuration from '%CONFIG_FILE%'
    for /f "usebackq tokens=* delims=" %%a in ("%CONFIG_FILE%") do (
        set "line=%%a"
        rem Skip empty lines and comments
        if defined line (
            echo !line! | findstr /r "^[A-Z_][A-Z0-9_]*=" >nul
            if !errorlevel! equ 0 (
                set "%%a"
            )
        )
    )
) else (
    echo [run.bat] No configuration file found at '%CONFIG_FILE%', using defaults
)

rem Validate environment
if not exist "%KARNAK_BIN%" (echo ERROR: Karnak executable not found & exit /b 1)

rem Start Karnak
echo [run.bat] Starting Karnak from '%KARNAK_BIN%'
start "Karnak" "%KARNAK_BIN%"

exit /b 0

:show_help
echo Usage: run.bat [OPTIONS]
echo   --config ^<file^>        Config file to source (default: ./run.cfg)
echo   --help                 Show this help message
exit /b 0
