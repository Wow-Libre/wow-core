; Script Inno Setup para Wow Libre Core (proyecto wow-core)
; Requisito: compilar con mvn clean package y ejecutar installer\build-installer.bat
; para copiar el JAR a installer\app\

#define MyAppName "Wow Libre Core"
#define MyAppVersion "0.0.1"
#define MyAppPublisher "Wow Libre"
#define MyAppURL "https://github.com/ManuChitiva/wow-core"
#define JarName "wowlibre-0.0.1-SNAPSHOT.jar"
#ifexist "icon.ico"
#define HasIcon
#endif
#ifexist "IniciarCore.exe"
#define UseExeLauncher
#endif

[Setup]
AppId={{C3D4E5F6-A7B8-9012-CDEF-345678901234}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={autopf}\WowLibreCore
DefaultGroupName=Wow Libre
DisableProgramGroupPage=yes
LicenseFile=
OutputDir=output
OutputBaseFilename=WowLibreCore-Setup-{#MyAppVersion}
#ifdef HasIcon
SetupIconFile=icon.ico
UninstallDisplayIcon={app}\icon.ico
#endif
Compression=lzma
SolidCompression=yes
WizardStyle=modern
PrivilegesRequired=admin
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible

[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "app\{#JarName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "IniciarCore.bat"; DestDir: "{app}"; Flags: ignoreversion
; Opcional: IniciarCore.exe (launcher con icono). Si existe, se usará en los accesos directos.
Source: "IniciarCore.exe"; DestDir: "{app}"; Flags: ignoreversion skipifsourcedoesntexist
Source: "icon.ico"; DestDir: "{app}"; Flags: ignoreversion skipifsourcedoesntexist

[Icons]
#ifdef UseExeLauncher
  #ifdef HasIcon
Name: "{group}\Iniciar Wow Libre Core"; Filename: "{app}\IniciarCore.exe"; WorkingDir: "{app}"; IconFilename: "{app}\icon.ico"
Name: "{autodesktop}\Iniciar Wow Libre Core"; Filename: "{app}\IniciarCore.exe"; WorkingDir: "{app}"; IconFilename: "{app}\icon.ico"; Tasks: desktopicon
  #else
Name: "{group}\Iniciar Wow Libre Core"; Filename: "{app}\IniciarCore.exe"; WorkingDir: "{app}"
Name: "{autodesktop}\Iniciar Wow Libre Core"; Filename: "{app}\IniciarCore.exe"; WorkingDir: "{app}"; Tasks: desktopicon
  #endif
#else
  #ifdef HasIcon
Name: "{group}\Iniciar Wow Libre Core"; Filename: "{app}\IniciarCore.bat"; WorkingDir: "{app}"; IconFilename: "{app}\icon.ico"
Name: "{autodesktop}\Iniciar Wow Libre Core"; Filename: "{app}\IniciarCore.bat"; WorkingDir: "{app}"; IconFilename: "{app}\icon.ico"; Tasks: desktopicon
  #else
Name: "{group}\Iniciar Wow Libre Core"; Filename: "{app}\IniciarCore.bat"; WorkingDir: "{app}"
Name: "{autodesktop}\Iniciar Wow Libre Core"; Filename: "{app}\IniciarCore.bat"; WorkingDir: "{app}"; Tasks: desktopicon
  #endif
#endif
Name: "{group}\Configurar variables (.env)"; Filename: "notepad.exe"; Parameters: "{app}\.env"; WorkingDir: "{app}"
Name: "{group}\{cm:UninstallProgram,{#MyAppName}}"; Filename: "{uninstallexe}"

[Run]
#ifdef UseExeLauncher
Filename: "{app}\IniciarCore.exe"; Description: "Iniciar Wow Libre Core ahora"; Flags: postinstall nowait skipifsilent
#else
Filename: "{app}\IniciarCore.bat"; Description: "Iniciar Wow Libre Core ahora"; Flags: postinstall nowait skipifsilent
#endif

[Code]
var
  PageDB, PageServer, PageExtra: TInputQueryWizardPage;

procedure InitializeWizard;
begin
  { Página 1: Base de datos (API central) }
  PageDB := CreateInputQueryPage(wpSelectDir,
    'Base de datos', 'MySQL del Core (plataforma central)',
    'URL JDBC, usuario y contraseña de la base de datos del Core (application.yml: DB_CORE_*).');
  PageDB.Add('URL JDBC (ej. jdbc:mysql://localhost:3306/platform):', False);
  PageDB.Values[0] := 'jdbc:mysql://localhost:3306/platform?useUnicode=true&characterEncoding=UTF-8';
  PageDB.Add('Usuario MySQL:', False);
  PageDB.Values[1] := 'root';
  PageDB.Add('Contraseña MySQL:', True);
  PageDB.Values[2] := '';

  { Página 2: Servidor }
  PageServer := CreateInputQueryPage(PageDB.ID,
    'Servidor', 'Puerto y configuración del Core',
    'Puerto de la API central (CORE_SERVER_PORT), secret JWT (CORE_JWT_SECRET_KEY) y dominio (HOST_DOMAIN).');
  PageServer.Add('Puerto del Core (ej. 8091):', False);
  PageServer.Values[0] := '8091';
  PageServer.Add('Secret JWT (CORE_JWT_SECRET_KEY):', True);
  PageServer.Values[1] := '';
  PageServer.Add('Dominio público / frontend (HOST_DOMAIN, ej. http://localhost:3000):', False);
  PageServer.Values[2] := 'http://localhost:3000';

  { Página 3: CORS y opcionales }
  PageExtra := CreateInputQueryPage(PageServer.ID,
    'CORS y opcionales', 'Orígenes permitidos para CORS. El resto se puede editar después en .env',
    'APP_CORS_ALLOWED_ORIGINS: orígenes separados por coma. Deja en blanco para usar el valor por defecto.');
  PageExtra.Add('CORS allowed origins (ej. http://localhost:3000,http://127.0.0.1:3000):', False);
  PageExtra.Values[0] := 'http://localhost:3000,http://127.0.0.1:3000';
end;

function EnvLine(const Key, Value: String): String;
begin
  Result := Key + '=' + Value + #13#10;
end;

procedure CurStepChanged(CurStep: TSetupStep);
var
  EnvPath, BatPath, S, JwtSecret: String;
  EnvContent: AnsiString;
begin
  if CurStep = ssPostInstall then
  begin
    EnvPath := ExpandConstant('{app}\.env');
    BatPath := ExpandConstant('{app}\IniciarCore.bat');

    if Trim(PageServer.Values[1]) = '' then
      JwtSecret := 'CambiarClaveJWTSecretCoreEnProduccion456'
    else
      JwtSecret := PageServer.Values[1];

    { Variables según application.yml del wow-core (perfil prod) }
    S := '';
    S := S + EnvLine('DB_CORE_URL', PageDB.Values[0]);
    S := S + EnvLine('DB_CORE_USERNAME', PageDB.Values[1]);
    S := S + EnvLine('DB_CORE_PASSWORD', PageDB.Values[2]);
    S := S + EnvLine('CORE_SERVER_PORT', PageServer.Values[0]);
    S := S + EnvLine('CORE_JWT_SECRET_KEY', JwtSecret);
    S := S + EnvLine('HOST_DOMAIN', PageServer.Values[2]);
    if Trim(PageExtra.Values[0]) <> '' then
      S := S + EnvLine('APP_CORS_ALLOWED_ORIGINS', PageExtra.Values[0]);

    EnvContent := AnsiString(S);
    SaveStringToFile(EnvPath, EnvContent, False);

    { Regenerar IniciarCore.bat con el nombre correcto del JAR }
    S := '@echo off' + #13#10;
    S := S + 'cd /d "%~dp0"' + #13#10;
    S := S + 'title Wow Libre Core' + #13#10;
    S := S + 'echo.' + #13#10;
    S := S + 'echo ========================================' + #13#10;
    S := S + 'echo   Wow Libre Core' + #13#10;
    S := S + 'echo ========================================' + #13#10;
    S := S + 'echo.' + #13#10;
    S := S + 'if not exist "{#JarName}" (' + #13#10;
    S := S + '  echo ERROR: No se encuentra {#JarName}' + #13#10;
    S := S + '  goto :fin)' + #13#10;
    S := S + 'if exist .env for /f "usebackq eol=# tokens=* delims=" %%a in (".env") do set "%%a"' + #13#10;
    S := S + 'echo Iniciando aplicacion...' + #13#10;
    S := S + 'echo.' + #13#10;
    S := S + 'java -jar "{#JarName}" --spring.profiles.active=prod' + #13#10;
    S := S + ':fin' + #13#10;
    S := S + 'echo.' + #13#10;
    S := S + 'pause' + #13#10;
    SaveStringToFile(BatPath, AnsiString(S), False);
  end;
end;
