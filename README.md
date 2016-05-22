# SimpleFolderChooserDialog

> my first open source :)
>
> flyingfish


features:
  select file/files
  select folder/folders
        
        
How-to-use:

## File Single
SimpleFileChooser dialog = new SimpleFileChooser();
dialog.setOnSelectCompleteListener(listener);
dialog.show(getSupportFragmentManager(), "File Single");

## file Multi
SimpleFileChooser dialog = new SimpleFileChooser();
dialog.setSelectType(SimpleFileChooser.SELECT_TYPE.Multi);
dialog.setOnSelectCompleteListener(listener);
        dialog.show(getSupportFragmentManager(), "File Multi");
        
## folder Single
SimpleFileChooser dialog = new SimpleFileChooser();
dialog.setOpenType(SimpleFileChooser.OPEN_TYPE.Folder);
dialog.setOnSelectCompleteListener(listener);
dialog.show(getSupportFragmentManager(), "Folder Single");

## folder Multi
SimpleFileChooser dialog = new SimpleFileChooser();
dialog.setOpenType(SimpleFileChooser.OPEN_TYPE.Folder);
dialog.setSelectType(SimpleFileChooser.SELECT_TYPE.Multi);
dialog.setOnSelectCompleteListener(listener);
dialog.show(getSupportFragmentManager(), "Folder Multi");
