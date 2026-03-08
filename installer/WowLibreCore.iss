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
  PageDB, PageServer, PageMail, PageExtra, PageOptional: TInputQueryWizardPage;

procedure InitializeWizard;
begin
  { Página 1: Base de datos (API central) }
  PageDB := CreateInputQueryPage(wpSelectDir,
    'Base de datos',
    'Dónde se guarda la información de la plataforma',
    'El Core necesita una base de datos MySQL para guardar usuarios, suscripciones, reinos y toda la información de la plataforma. Indique la dirección del servidor MySQL, el nombre de la base (ej. platform) y las credenciales para conectarse.');
  PageDB.Add('URL de la base de datos (dirección del servidor MySQL + nombre de la base, ej. jdbc:mysql://localhost:3306/platform):', False);
  PageDB.Values[0] := 'jdbc:mysql://localhost:3306/platform?useUnicode=true&characterEncoding=UTF-8';
  PageDB.Add('Usuario de MySQL (el usuario con el que el Core se conecta a la base):', False);
  PageDB.Values[1] := 'root';
  PageDB.Add('Contraseña de MySQL (contraseña del usuario anterior):', True);
  PageDB.Values[2] := '';

  { Página 2: Servidor }
  PageServer := CreateInputQueryPage(PageDB.ID,
    'Servidor y seguridad',
    'Puerto, tokens y URL del frontend',
    'Aquí se configura en qué puerto escuchará la API del Core, la clave secreta con la que se firman los tokens de sesión (JWT) y la URL pública de su sitio o frontend, para que el Core sepa desde qué dominio se hacen las peticiones.');
  PageServer.Add('Puerto del Core (número de puerto donde la API estará disponible, ej. 8091; los clientes/reinos se conectarán a este puerto):', False);
  PageServer.Values[0] := '8091';
  PageServer.Add('Clave secreta JWT (se usa para firmar los tokens de sesión; debe ser larga y secreta; si la deja en blanco se usará una por defecto):', True);
  PageServer.Values[1] := '';
  PageServer.Add('Dominio o URL del frontend (URL pública de su web o app, ej. http://localhost:3000 o https://www.wowlibre.com; el Core la usa para enlaces y validaciones):', False);
  PageServer.Values[2] := 'http://localhost:3000';

  { Página 3: Correo (SMTP) }
  PageMail := CreateInputQueryPage(PageServer.ID,
    'Correo electrónico (SMTP)',
    'Configuración para enviar emails a los usuarios',
    'El Core puede enviar correos (registro, recuperar contraseña, notificaciones). Indique los datos del servidor de correo. Con Gmail use smtp.gmail.com, puerto 587 y una "contraseña de aplicación" (no la contraseña normal). Con otro proveedor use su host y puerto.');
  PageMail.Add('Servidor de correo / Host SMTP (ej. smtp.gmail.com para Gmail, o el que le indique su proveedor):', False);
  PageMail.Values[0] := 'smtp.gmail.com';
  PageMail.Add('Puerto SMTP (587 suele ser para TLS; 465 para SSL; 25 sin cifrado):', False);
  PageMail.Values[1] := '587';
  PageMail.Add('Usuario / correo (la dirección de email que envía los mensajes):', False);
  PageMail.Values[2] := '';
  PageMail.Add('Contraseña (en Gmail use "contraseña de aplicación" desde la cuenta de Google):', True);
  PageMail.Values[3] := '';

  { Página 4: CORS }
  PageExtra := CreateInputQueryPage(PageMail.ID,
    'CORS (orígenes permitidos)',
    'Qué sitios pueden llamar a la API del Core',
    'Por seguridad, el navegador solo permite que ciertos orígenes (dominios) llamen a la API. Indique las URLs de su frontend o de los sitios que consumirán la API, separadas por coma. Así se evitan bloqueos cuando la web y la API están en dominios distintos.');
  PageExtra.Add('Orígenes permitidos (URLs separadas por coma, ej. http://localhost:3000,https://www.wowlibre.com):', False);
  PageExtra.Values[0] := 'http://localhost:3000,http://127.0.0.1:3000';

  { Página 5: Opcionales (firma, PayU, reCAPTCHA, Telegram) }
  PageOptional := CreateInputQueryPage(PageExtra.ID,
    'Opcionales (pagos, reCAPTCHA, Telegram)',
    'Clave de firma, PayU, reCAPTCHA y bot de Telegram',
    'Estas opciones puede dejarlas en blanco y editarlas después en el archivo .env de la carpeta de instalación. Clave de firma: para validar peticiones internas. PayU: URL de la pasarela de pagos. reCAPTCHA: clave de Google para evitar bots. Telegram: si habilita un bot, indique token y nombre de usuario.');
  PageOptional.Add('Clave de firma de la aplicación (se usa para validar peticiones firmadas entre servicios; puede dejar el valor por defecto):', False);
  PageOptional.Values[0] := 'wowLibreSecret';
  PageOptional.Add('URL de la API PayU (pasarela de pagos; use la URL de sandbox para pruebas o la de producción cuando vaya a cobrar):', False);
  PageOptional.Values[1] := 'https://sandbox.api.payulatam.com/reports-api/4.0/service.cgi';
  PageOptional.Add('Clave secreta de Google reCAPTCHA (para proteger formularios de bots; obténgala en Google reCAPTCHA Admin):', False);
  PageOptional.Values[2] := '';
  PageOptional.Add('¿Bot de Telegram habilitado? (escriba true para activar notificaciones/consultas por Telegram, o false para desactivarlo):', False);
  PageOptional.Values[3] := 'false';
  PageOptional.Add('Token del bot de Telegram (lo obtiene al crear un bot con @BotFather en Telegram; déjelo en blanco si no usa bot):', False);
  PageOptional.Values[4] := '';
  PageOptional.Add('Nombre de usuario del bot de Telegram (ej. @WowLibre_bot; debe coincidir con el bot cuyo token puso arriba):', False);
  PageOptional.Values[5] := '';
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
    S := S + EnvLine('CORE_GOOGLE_HOST', PageMail.Values[0]);
    S := S + EnvLine('CORE_GOOGLE_PORT', PageMail.Values[1]);
    S := S + EnvLine('CORE_GOOGLE_USERNAME', PageMail.Values[2]);
    S := S + EnvLine('CORE_GOOGLE_PASSWORD', PageMail.Values[3]);
    if Trim(PageExtra.Values[0]) <> '' then
      S := S + EnvLine('APP_CORS_ALLOWED_ORIGINS', PageExtra.Values[0]);
    if Trim(PageOptional.Values[0]) <> '' then
      S := S + EnvLine('APP_SIGNATURE_SECRET_KEY', PageOptional.Values[0]);
    if Trim(PageOptional.Values[1]) <> '' then
      S := S + EnvLine('PAYU_API_URL', PageOptional.Values[1]);
    if Trim(PageOptional.Values[2]) <> '' then
      S := S + EnvLine('GOOGLE_API_SECRET', PageOptional.Values[2]);
    S := S + EnvLine('TELEGRAM_BOT_ENABLED', PageOptional.Values[3]);
    if Trim(PageOptional.Values[4]) <> '' then
      S := S + EnvLine('TELEGRAM_BOT_TOKEN', PageOptional.Values[4]);
    if Trim(PageOptional.Values[5]) <> '' then
      S := S + EnvLine('TELEGRAM_BOT_USERNAME', PageOptional.Values[5]);

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
