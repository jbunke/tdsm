#define MyAppName "Top Down Sprite Maker"
#define MyAppVersion "1.1.0"
#define VersionCode "1_1_0"
#define InstallerName "tdsm-" + VersionCode
#define MyAppPublisher "Jordan Bunke"
#define MyAppURL "https://flinkerflitzer.itch.io/tdsm"
#define MyAppExeName "tdsm.exe"
#define ProjectRoot "C:\Users\Jordan\Documents\code\java\tdsm"

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
LicenseFile=C:\Users\Jordan\Documents\code\java\tdsm\LICENSE
PrivilegesRequiredOverridesAllowed=dialog
SetupIconFile=C:\Users\Jordan\Documents\code\java\tdsm\out\artifacts\bundle\icons\win-icon.ico
SolidCompression=yes
WizardStyle=modern
OutputDir={#ProjectRoot}\out\artifacts\installer
OutputBaseFilename={#InstallerName}

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "C:\Users\Jordan\Documents\code\java\tdsm\out\artifacts\dist\win\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\Users\Jordan\Documents\code\java\tdsm\out\artifacts\dist\win\runtime\*"; DestDir: "{app}\runtime"; Flags: recursesubdirs createallsubdirs

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{autodesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: nowait postinstall skipifsilent