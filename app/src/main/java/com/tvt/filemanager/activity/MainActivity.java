package com.tvt.filemanager.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.tvt.filemanager.R;
import com.tvt.filemanager.adapter.StoreAdapter;
import com.tvt.filemanager.models.ItemStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, StoreAdapter.ItemClick {
    private static final int REQUEST_WRITE_STORAGE_EXTERNAL = 1;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton fabNewFile, fabNewFolder, fabS;
    private LinearLayout linear;
    private ListView lvFile;
    private StoreAdapter adapter;
    private List<ItemStore> arrItem;
    private TextView tvPath;

    private String path,currentPath;
    private File path1;
    private final static int RQS_OPEN_AUDIO_MP3 = 1;
    EditText input,input1;
    private boolean isLayout=false;
    private File fileCurrentCopy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkPermissions();

        if (getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);


        }
        toolbar.setTitle("File");
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        addListView();
        clickFabNewFile();
        clickFabNewFolder();

//        String path = getFilesDir().getAbsolutePath();
//        Log.d("tag",path);
        tvPath.setText(Environment.getExternalStorageDirectory().getPath());


    }

    private void clickFabNewFolder() {
        fabNewFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("New Folder")
                        .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                writeFolder(input.getText().toString());


                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                input = new EditText(MainActivity.this);
                input.setHint("Enter Name");

                RelativeLayout relativeLayout = new RelativeLayout(MainActivity.this);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                input.setLayoutParams(lp);
                relativeLayout
                        .addView(input);
                builder.setView(relativeLayout);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(s)) {
                            // Disable ok button
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            // Something into edit text. Enable the button.
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                });

            }
        });

        fabS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "SMB Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void listViewRefreshData(String path){
        arrItem.clear();
        arrItem = getPath(path);
        if(arrItem.size() ==0){
            isLayout = true;
            linear.removeView(lvFile);
            linear.addView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.file_items_null,null));
        }
        adapter.setData(arrItem);
        adapter.notifyDataSetChanged();
    }

    public void writeFolder(String path) {
        File parentDirectory=new File(currentPath);
        File file =new File(currentPath +File.separator+path);

        if(file.exists()){
            Toast.makeText(this, "Folder đã tồn tại .Xin vui lòng đặt tên khác", Toast.LENGTH_SHORT).show();
        }else {
            file.mkdirs();
            Toast.makeText(this, "Tạo folder thành công ", Toast.LENGTH_LONG).show();
        }
        if(isLayout){
            isLayout = false;
            linear.removeAllViews();
            linear.addView(lvFile);
        }
        listViewRefreshData(currentPath);
    }


    private void clickFabNewFile() {
        fabNewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.newFile)
                        .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String fileNewName=input1.getText().toString();


                                    writenewFile(fileNewName);

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                 input1 = new EditText(MainActivity.this);
                input1.setHint("Enter Name");

                RelativeLayout relativeLayout = new RelativeLayout(MainActivity.this);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                input1.setLayoutParams(lp);
                relativeLayout
                        .addView(input1);
                builder.setView(relativeLayout);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                input1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (TextUtils.isEmpty(s)) {
                            // Disable ok button
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            // Something into edit text. Enable the button.
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                });

            }
        });
    }

    public void writenewFile(String s) {
        File pathfolderFile =new File(currentPath) ;
        File file =new File(currentPath+File.separator +s);

        if(file.exists()&& file.isDirectory()){
            Toast.makeText(this, "File đã tồn tại .Xin vui lòng đặt tên khác", Toast.LENGTH_SHORT).show();
        }else {
            try {
                file.createNewFile();
                Toast.makeText(this, "Tạo File Thành công", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(isLayout){
                isLayout = false;
                linear.removeAllViews();
                linear.addView(lvFile);
            }
            listViewRefreshData(currentPath);
        }


    }


    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                //Permisson don't granted
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "Permission isn't granted ", Toast.LENGTH_SHORT).show();
                }
                // Permisson don't granted and dont show dialog again.
                else {
                    Toast.makeText(MainActivity.this, "Permisson don't granted and dont show dialog again ", Toast.LENGTH_SHORT).show();
                }
                //Register permission
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            }
        }

    }

    private void addListView() {

        arrItem = getPath(Environment.getExternalStorageDirectory()
                .getPath());
        adapter = new StoreAdapter(this, arrItem, this);
        lvFile.setAdapter(adapter);
    }

    private List<ItemStore> getPath(String pathRoot) {
        currentPath=pathRoot;
        List<ItemStore> temStore =
                new ArrayList<>();
        File file = new File(pathRoot);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (final File f : files) {
                    ItemStore itemStore = new ItemStore();
                    itemStore.setPath(f.getPath());
                    itemStore.setLastModifier(
                            new Date(f.lastModified())
                    );
                    itemStore.setName(f.getName());
                    if (f.isFile()) {
                        String path = itemStore.getPath();
                        int indexPoint = path.lastIndexOf(".");
                        if (indexPoint > -1) {
                            String ext = path.substring(indexPoint + 1);
                            itemStore.setExtent(ext);
                        }
                    }
                    temStore.add(itemStore);
                    Collections.sort(temStore, new Comparator<ItemStore>() {
                        @Override
                        public int compare(ItemStore o1, ItemStore o2) {
                            if (o1.getExtent() == null) {
                                return o1.getName().compareToIgnoreCase(o2.getName());
                            }

                            return 0;
                        }
                    });
                }
            }
        }
        return temStore;
    }


    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.nav);
        floatingActionMenu = findViewById(R.id.fab);
        lvFile = findViewById(R.id.list_item);
        fabNewFile = findViewById(R.id.new_file);
        fabNewFolder = findViewById(R.id.new_folder);
        fabS = findViewById(R.id.fab_s);
        tvPath = findViewById(R.id.path);
        linear=findViewById(R.id.linear);


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
                break;
            case R.id.home:
                path1 =Environment.getExternalStorageDirectory();
                arrItem.clear();
                tvPath.setText(path1.getPath());
                arrItem.addAll(getPath(path1.getAbsolutePath()));
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.paste:

                pasteFile();
                break;
            case R.id.move:
                moveFile();
                break;

        }

        return true;
    }

    private void moveFile() {
        File vitriCopy=new File(currentPath ,fileCurrentCopy.getName());
        Log.d("tien.mtp","Vitri copy :" +vitriCopy.toString());
        FileOutputStream outputStream = null;
        try {
            FileInputStream inputStream =new FileInputStream(fileCurrentCopy);
            outputStream = new FileOutputStream(vitriCopy);
            int dungluong = 0;
            byte[] bytes=new byte[1024];
            while ((dungluong=inputStream.read(bytes))>0){

                outputStream.write(bytes,0,dungluong);

            }

            fileCurrentCopy.delete();
            Toast.makeText(this, "Move File Hoặc Folder Thành công", Toast.LENGTH_SHORT).show();
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        listViewRefreshData(currentPath);
    }

    private void pasteFile() {
        File vitriCopy=new File(currentPath ,fileCurrentCopy.getName());
        Log.d("tien.mtp","Vitri copy :" +vitriCopy.toString());
        FileOutputStream outputStream = null;
        try {
            FileInputStream inputStream =new FileInputStream(fileCurrentCopy);
            outputStream = new FileOutputStream(vitriCopy);
            int dungluong = 0;
            byte[] bytes=new byte[1024];
            while ((dungluong=inputStream.read(bytes))>0){

                outputStream.write(bytes,0,dungluong);

            }
            Toast.makeText(this, "Copy File Hoặc Folder Thành Công", Toast.LENGTH_SHORT).show();
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        listViewRefreshData(currentPath);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        File path;
        switch (item.getItemId()) {
            case R.id.storage:
                path =Environment.getExternalStorageDirectory();
                arrItem.clear();
                tvPath.setText(path.getPath());
                arrItem.addAll(getPath(path.getAbsolutePath()));
                adapter.notifyDataSetChanged();

                break;

            case R.id.dcim:
                path =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                arrItem.clear();
                tvPath.setText(path.getPath());
                arrItem.addAll(getPath(path.getAbsolutePath()));
                adapter.notifyDataSetChanged();

                break;
            case R.id.download:
                 path =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                 arrItem.clear();
                 tvPath.setText(path.getPath());
                 arrItem.addAll(getPath(path.getAbsolutePath()));
                 adapter.notifyDataSetChanged();

                break;
            case R.id.movies:
                path =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                arrItem.clear();
                tvPath.setText(path.getPath());
                arrItem.addAll(getPath(path.getAbsolutePath()));
                adapter.notifyDataSetChanged();

                break;
            case R.id.music:
                path =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                arrItem.clear();
                tvPath.setText(path.getPath());
                arrItem.addAll(getPath(path.getAbsolutePath()));
                adapter.notifyDataSetChanged();

                break;
            case R.id.picture:
                path =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                arrItem.clear();
                tvPath.setText(path.getPath());
                arrItem.addAll(getPath(path.getAbsolutePath()));
                adapter.notifyDataSetChanged();

                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClickItem(int position) {

        ItemStore itemStore = arrItem.get(position);
        if (itemStore.getExtent() == null &&
                new File(itemStore.getPath()).isDirectory()) {
            path = itemStore.getPath();
            arrItem.clear();
            arrItem.addAll(
                    getPath(path));
            tvPath.setText(path);
            //reload lai cac item trong listview
            adapter.notifyDataSetChanged();
        }
        else if (itemStore.getExtent().equals("mp3")&& new File(itemStore.getPath()).isFile()) {
//            Intent intent = new Intent();
//
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.setType("audio/mp3");
//            startActivityForResult(Intent.createChooser(intent,"mp3"),RQS_OPEN_AUDIO_MP3);
//            Intent intent = new Intent();
//            intent.setAction(android.content.Intent.ACTION_VIEW);
//            File pathCurrent =new File(currentPath);
//            File file = new File(pathCurrent.getParent() ,pathCurrent.getName() );
//            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
//            intent.setDataAndType(Uri.fromFile(file), "audio/*");
//            startActivity(intent);
            Toast.makeText(this, "Em chưa open đc :v", Toast.LENGTH_SHORT).show();

        }else if (itemStore.getExtent().equalsIgnoreCase("txt")){
            Toast.makeText(this, "Em chưa open đc :v", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    @Override
    public void onClickLongItemView(final int position, View view) {

        PopupMenu popup = new PopupMenu(this,view, Gravity.RIGHT);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.item_extras, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.copy:
                        copyFile(position);
                        break;

                    case R.id.cut:
                        copyFile(position);
                        break;
                    case R.id.delete:
                        File file = new File(arrItem.get(position).getPath());
                        Log.d("fas", file.toString());


                        if (arrItem.get(position).getExtent() == null) {
                            deleteDirectory(file);
                            Toast.makeText(MainActivity.this, "Delete folder Thành Công", Toast.LENGTH_SHORT).show();
                        } else {
                            file.delete();
                            Toast.makeText(MainActivity.this, "Delete file thành công", Toast.LENGTH_SHORT).show();
                        }
                        listViewRefreshData(currentPath);

                        break;

                    case R.id.rename:
                      
                        reNameFileNameAndFolDer(arrItem.get(position));


                }
                return true;
                
            }
        });

        popup.show();
       
    }

    private void copyFile(int position) {
        String pathFileCopy= arrItem.get(position).getPath();
        File folderFileCopy =new File(pathFileCopy);
        fileCurrentCopy =new File(folderFileCopy.getParent(),folderFileCopy.getName());


    }

    private void reNameFileNameAndFolDer(final ItemStore itemStore) {
        
        final EditText edtReName = new EditText(this);
        String oldName=itemStore.getName();
        final File fileRename = new File(currentPath +File.separator+oldName);
        Log.d("111",fileRename.getAbsolutePath());
        final String path = itemStore.getName();


        edtReName.setHint("Enter Name");
        edtReName.setText(path);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("You Have Want Rename?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        File newFile = new File(currentPath+File.separator +edtReName.getText().toString());
                        Log.d("111",newFile.getAbsolutePath());
                       if(newFile.exists()){
                           Toast.makeText(MainActivity.this, "File hoặc folder đã tồn tại.Vui lòng nhập tên khác", Toast.LENGTH_SHORT).show();
                       }else {
                           fileRename.renameTo(newFile);
                           Toast.makeText(MainActivity.this, "Đổi Tên thành công", Toast.LENGTH_SHORT).show();
                       }


                        listViewRefreshData(currentPath);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        RelativeLayout relativeLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = this.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        lp.rightMargin = this.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        edtReName.setLayoutParams(lp);
        relativeLayout
                .addView(edtReName);
        builder.setView(relativeLayout);
        final AlertDialog dialog = builder.create();
        dialog.show();
//
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE_EXTERNAL) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Ok,đã có quyền", Toast.LENGTH_SHORT).show();
                addListView();
            } else {

                Toast.makeText(this, "Chưa có quyền", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onBackPressed() {
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.check_in);
        if(currentPath.equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
            finish();
        }else{
            File file =new File(currentPath);
            arrItem = getPath(file.getParent());
            if(arrItem.size() >0 ){
                isLayout = false;
                linear.removeAllViews();
                linear.addView(lvFile);
                tvPath.setAnimation(animation);
                tvPath.setText(currentPath);

            }
            adapter.setData(arrItem);
            adapter.notifyDataSetChanged();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RQS_OPEN_AUDIO_MP3 && resultCode == RESULT_OK){
            if ((data != null) && (data.getData() != null)){
                Uri audioFileUri = data.getData();
                // Now you can use that Uri to get the file path, or upload it, ...
            }
        }
    }
}
