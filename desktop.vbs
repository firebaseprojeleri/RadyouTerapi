dim fso: set fso = CreateObject("Scripting.FileSystemObject")
CurrentDirectory = fso.GetAbsolutePathName(".")
EditDesktopIni("C:\Users\SE\AndroidStudioProjects\RadyouTerapi")

Sub EditDesktopIni(foldpath)
    Dim fso, inifile, icondata, file, fold, subfold, item, subfoldpath
    Const ForReading = 1, ForWriting = 2
    Const TristateUseDefault = -2, TristateTrue = -1, TristateFalse = 0
    Set fso = CreateObject("Scripting.FileSystemObject")
    Set fold = fso.GetFolder(foldpath)
    Set subfold = fold.SubFolders
	inifile = foldpath + "\desktop.ini"
	If (fso.FileExists(inifile)) Then 'If desktop.ini exists, delete it
		fso.DeleteFile inifile, True
	End If
    For Each item In subfold
        inifile = foldpath + "\" + item.Name + "\desktop.ini"
        subfoldpath = foldpath & "\" & item.name
        If (fso.FileExists(inifile)) Then 'If desktop.ini exists, delete it
            fso.DeleteFile inifile, True
        End If
		EditDesktopIni(subfoldpath)
    Next
End Sub