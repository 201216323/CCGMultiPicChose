package com.example.ccgmultipicchose;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccgmultipicchose.iosdialog.ActionSheetDialog;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {


    private EditText edit_main_input;
    private TextView text_main_current;
    private TextView text_main_total;
    private NoScrollGridView noScrollGridView_main_pic;
    private TextView text_main_fabu;
    private GridAdapter adapter;
    private MainActivity self;
    public static final int NUM = 6;//控制显示的图片数量,只用修改此数量即可

    public static ArrayList<String> tempSelectBitmap = new ArrayList<>();//图片所在的list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        self = MainActivity.this;
        edit_main_input = (EditText) findViewById(R.id.edit_main_input);
        text_main_current = (TextView) findViewById(R.id.text_main_current);
        text_main_total = (TextView) findViewById(R.id.text_main_total);
        noScrollGridView_main_pic = (NoScrollGridView) findViewById(R.id.noScrollGridView_main_pic);
        text_main_fabu = (TextView) findViewById(R.id.text_main_fabu);
        MainActivity.tempSelectBitmap.add("camera_default");


        adapter = new GridAdapter(MainActivity.this);
        noScrollGridView_main_pic.setAdapter(adapter);

        ininListener();
    }

    private void ininListener() {
        noScrollGridView_main_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String path = MainActivity.tempSelectBitmap.get(i);
                if (path.contains("camera_default") && i == MainActivity.tempSelectBitmap.size() - 1) {
                    showSelectImageDialog();
                }

            }
        });

        text_main_fabu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "aaaaa", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showSelectImageDialog() {
        new ActionSheetDialog(self)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        String state = Environment.getExternalStorageState();
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(openCameraIntent, 111);
                    }
                })
                .addSheetItem("打开相册", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {

                        Intent intent = new Intent(MainActivity.this, ImageSelectActivity.class);
                        startActivityForResult(intent, 123);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 111:
                if (resultCode == RESULT_OK) {
                    if (MainActivity.tempSelectBitmap.size() < NUM + 1) {

                        Bitmap bm = (Bitmap) data.getExtras().get("data");
                        File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);// Environment.DIRECTORY_PICTURES
                        File paiZhaoFile = ImageTools.savePhotoToSDCard(bm, outDir, "" + System.currentTimeMillis());
                        for (int i = 0; i < MainActivity.tempSelectBitmap.size(); i++) {
                            String path = MainActivity.tempSelectBitmap.get(i);
                            if (path.contains("camera_default")) {
                                MainActivity.tempSelectBitmap.remove(MainActivity.tempSelectBitmap.size() - 1);
                            }

                        }
                        MainActivity.tempSelectBitmap.add(paiZhaoFile.getAbsolutePath());
                        if (MainActivity.tempSelectBitmap.size() < NUM + 1) {
                            MainActivity.tempSelectBitmap.add("camera_default");
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "只能选择六张图片哦", Toast.LENGTH_SHORT).show();
                }
                break;

            case 123:
                if (resultCode == RESULT_OK) {
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

}
