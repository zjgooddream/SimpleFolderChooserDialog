# SimpleFolderChooserDialog

> my first open source :)
>
> by zjgooddream


features:
  select file/files
  select folder/folders
        
  support Internal Storage, External Storage, USB Storage
  
  
How-to-use:

## File Single
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.setOnSelectCompleteListener(listener);
        dialog.show();

## file Multi
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.setSelectType(SimpleFileChooser.SELECT_TYPE.Multi);
        dialog.setOnSelectCompleteListener(listener);
        dialog.show();
        
## folder Single
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.setOpenType(SimpleFileChooser.OPEN_TYPE.Folder);
        dialog.setOnSelectCompleteListener(listener);
        dialog.show();

## folder Multi
        SimpleFileChooser dialog = new SimpleFileChooser();
        dialog.setOpenType(SimpleFileChooser.OPEN_TYPE.Folder);
        dialog.setSelectType(SimpleFileChooser.SELECT_TYPE.Multi);
        dialog.setOnSelectCompleteListener(listener);
        dialog.show();

## callback

    private SimpleFileChooser.OnSelectCompleteListener listener = new SimpleFileChooser.OnSelectCompleteListener() {
        @Override
        public void onSelectComplete(ArrayList<String> selctedFiles) {
            String toastTip = "You selected :\n";
            for (String str :
                 selctedFiles) {
                toastTip += str + "\n";
            }
            Toast.makeText(MainActivity.this, toastTip, Toast.LENGTH_SHORT).show();
        }
    };
