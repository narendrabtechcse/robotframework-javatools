$command = "python " & @ScriptDir & "\..\..\src\main\python\ExternalApplicationLauncher.py" & $CmdLineRaw
Run($command, "", @SW_HIDE)
