#define MyAppName "%NAME%"
#define MyAppVersion "%VERSION%"
#define VersionCode "%VERSION_UND%"
#define InstallerName "tdsm-" + VersionCode
#define MyAppPublisher "Jordan Bunke"
#define MyAppURL "https://flinkerflitzer.itch.io/tdsm"
#define MyAppExeName "tdsm.exe"
#define ProjectRoot "%PROJ_ROOT%"

[Setup]
AppId={{FC5B3BFB-6CD1-4E28-9309-73A77923E796}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\{#MyAppName}
UninstallDisplayIcon={app}\{#MyAppExeName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
LicenseFile=%PROJ_ROOT%\LICENSE
PrivilegesRequiredOverridesAllowed=dialog
SetupIconFile=%PROJ_ROOT%\out\artifacts\_to bundle with releases\icons\win-icon.ico
SolidCompression=yes
WizardStyle=modern
OutputDir={#ProjectRoot}\out\artifacts\installer
OutputBaseFilename={#InstallerName}

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "%PROJ_ROOT%\out\artifacts\dist\win\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "%PROJ_ROOT%\out\artifacts\dist\win\runtime\*"; DestDir: "{app}\runtime"; Flags: recursesubdirs createallsubdirs

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent
