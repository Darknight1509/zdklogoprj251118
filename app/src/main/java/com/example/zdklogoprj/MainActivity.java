package com.example.zdklogoprj;

import static android.os.Environment.DIRECTORY_DCIM;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Gainmap;
import android.graphics.Paint;
import android.graphics.Typeface;

import androidx.exifinterface.media.ExifInterface;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zdklogoprj.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1; // 请求码

    private int selectedImageResId = -1; // 默认值为-1，表示没有选中任何图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonSelectImage = findViewById(R.id.button_select_image);
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动图片选择器
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1)); // 每行1个元素

        List<Integer> imageResIds = Arrays.asList(
                R.drawable.applelogo, R.drawable.canonlogo, R.drawable.cpslogo,
                R.drawable.haseelbellogo, R.drawable.longlogo, R.drawable.nikonlogo,
                R.drawable.whitelogohdr1hdr, R.drawable.puregray, R.drawable.zdkoldlogoblack);
        ImageOptionAdapter adapter = new ImageOptionAdapter(this, imageResIds, new ImageOptionAdapter.OnImageSelectedListener() {
            @Override
            public void onImageSelected(int imageResId) {
                selectedImageResId = imageResId; // 更新Activity或Fragment中的变量
            }
        });


        selectedImageResId = adapter.getSelectedImageResId();

        recyclerView.setAdapter(adapter);

    }


    public static String replaceMarkII(String input) {
        // 匹配格式 R5m2、R6m2 等，"R" 后跟数字，然后 "m2"
        Pattern pattern = Pattern.compile("R(\\d)m2");
        Matcher matcher = pattern.matcher(input);

        // 进行替换
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            // 获取数字部分
            String number = matcher.group(1);
            // 替换为 "R<number> MARK II"
            matcher.appendReplacement(result, "R" + number + " Mark II");
        }
        matcher.appendTail(result);

        if ("GFX100S".equals(input)) {
             result = new StringBuffer("FUJI  GFX100S");
        }

        return result.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();


            // 获取到图片的InputStream，然后将其转换为Bitmap
            InputStream imageStream = null;
            try {

                imageStream = getContentResolver().openInputStream(selectedImageUri);

                ExifInterface exif = new ExifInterface(imageStream);

                imageStream = getContentResolver().openInputStream(selectedImageUri);

                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                // 找到布局中的ImageView并设置Bitmap
                ImageView imageView = findViewById(R.id.imageView);


                Bitmap src = selectedImage;

                Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

                InputStream is = null;

                Paint paint = new Paint();

                paint.setTextSize(62); // 水印的字体大小
                paint.setAntiAlias(true);
                paint.setTypeface(Typeface.DEFAULT_BOLD); // 设置文字样式
                paint.setColor(Color.BLACK); // 水印的颜色

                try {

                    switch (selectedImageResId){

                        case 0:
                            is = getAssets().open("applelogo.png");
                            paint.setColor(Color.BLACK); // 水印的颜色
                            break;
                        case 1:
                            is = getAssets().open("canonlogo.png");
                            paint.setColor(Color.BLACK); // 水印的颜色
                            break;

                        case 2:
                            is = getAssets().open("cpslogo.png");
                            paint.setColor(Color.BLACK); // 水印的颜色
                            break;

                        case 3:
                            is = getAssets().open("haseelbellogo.png");
                            paint.setColor(Color.WHITE); // 水印的颜色
                            break;

                        case 4:
                            is = getAssets().open("longlogo.jpg");
                            paint.setColor(Color.YELLOW); // 水印的颜色
                            break;

                        case 5:
                            is = getAssets().open("nikonlogo.png");
                            paint.setColor(Color.BLACK); // 水印的颜色
                            break;

                        case 6:
                            is = getAssets().open("whitelogohdr1hdr.jpg");
                            paint.setColor(Color.BLACK); // 水印的颜色
                            break;
                        case 7:
                            is = getAssets().open("puregray.png");
                            paint.setColor(Color.WHITE); // 水印的颜色
                            break;
                        case 8:
                            is = getAssets().open("zdkoldlogoblack.jpg");
                            paint.setColor(Color.WHITE); // 水印的颜色
                            break;
                        default:
                            is = getAssets().open("longlogo.jpg");
                            paint.setColor(Color.YELLOW); // 水印的颜色
                            break;
                    }


                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);


                    String model = exif.getAttribute(ExifInterface.TAG_MODEL);

                    model = replaceMarkII(model);



                    EditText etLensModel = findViewById(R.id.et_lens_model);
                    String customLensModel = etLensModel.getText().toString().trim();

// 2. 原有的 EXIF 获取逻辑
                    String lensModel = exif.getAttribute(ExifInterface.TAG_LENS_MODEL);

// 3. 优先级判断：如果输入框有内容，优先使用输入框的文字
                    if (!customLensModel.isEmpty()) {
                        lensModel = customLensModel;
                    }

                    String make =  exif.getAttribute(ExifInterface.TAG_MAKE);

                    if(lensModel == null){
                        lensModel = make;
                    }

                    String dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL);

                    String[] timeparts = dateTime.split(" ", 2);
                    String datePart = timeparts[0].replace(':', '.');
                    dateTime = datePart + " " + timeparts[1];

                    String focalLength = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                    String focalLengthFuji = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
                    String focalLength35mm = exif.getAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM);

                    // 将字符串分割为分子和分母
                    String[] parts = focalLength.split("/");

                    // 转换分子和分母为double类型进行计算
                    double numerator = Double.parseDouble(parts[0]);
                    double denominator = Double.parseDouble(parts[1]);
                    double focalLengthResult = (numerator / denominator);

                    String fNumber = exif.getAttribute(ExifInterface.TAG_F_NUMBER);

                    String exposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);

                    String isoSpeedRatings = exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS);

                    double time = 1 / (Double.parseDouble(exposureTime));

                    String realExposure ="";

                    if (time < 1 ) {
                        realExposure = exposureTime;
                    }else {
                        realExposure = "1/" + String.valueOf(time);
                    }

                    realExposure = realExposure.split("\\.")[0];

                    String sfocalLengthResult = String.valueOf(focalLengthResult).split("\\.")[0];

                    if(!String.valueOf(focalLengthResult).split("\\.")[1].equals("0")){
                        sfocalLengthResult = String.valueOf(focalLengthResult);
                    }

                    if(focalLength35mm!=null&&(!("FUJI  GFX100S".equals(model)))){
                        sfocalLengthResult = focalLength35mm;
                    }


                    String exposure = sfocalLengthResult + "mm f/" + fNumber + " " + realExposure + "s ISO"+ isoSpeedRatings;



                    Canvas logoCanvas = new Canvas(mutableBitmap);
                    if(model != null) {
                        logoCanvas.drawText(model, 200, 155, paint);
                    }
                    if(lensModel != null ){
                        logoCanvas.drawText(lensModel, 200, 250, paint);
                    }

                    if(exposure != null) {
                        logoCanvas.drawText(exposure, 3085, 155, paint);
                    }
                    if(dateTime != null) {
                        logoCanvas.drawText(dateTime, 3085, 250, paint);
                    }

                    Bitmap topBitmap = result;
                    Bitmap bottomBitmap = mutableBitmap;



                    int topBitmapLongSide = topBitmap.getWidth();

                    // 确定缩放比例
                    float scale = topBitmapLongSide / (float) Math.max(bottomBitmap.getWidth(), bottomBitmap.getHeight());

                    // 缩放下方Bitmap
                    Bitmap scaledBottomBitmap = Bitmap.createScaledBitmap(bottomBitmap,
                            (int) (bottomBitmap.getWidth() * scale),
                            (int) (bottomBitmap.getHeight() * scale),
                            true);

                    // 创建一个新的Bitmap，其宽度为两个Bitmap宽度的最大值，高度为两者之和
                    Bitmap newresult = Bitmap.createBitmap(Math.max(topBitmap.getWidth(), scaledBottomBitmap.getWidth()),
                            topBitmap.getHeight() + scaledBottomBitmap.getHeight(),
                            topBitmap.getConfig());

// 修改前
// Bitmap newresult = Bitmap.createBitmap(..., topBitmap.getConfig());

// 修改后
//                    Bitmap.Config config = src.getConfig();
//                    ColorSpace colorSpace = src.getColorSpace();
//
//                    Bitmap newresult;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && colorSpace != null) {
//                        // 强制使用原图的色彩空间，防止色域降级导致的偏色
//                        newresult = Bitmap.createBitmap(
//                                Math.max(topBitmap.getWidth(), scaledBottomBitmap.getWidth()),
//                                topBitmap.getHeight() + scaledBottomBitmap.getHeight(),
//                                config,
//                                true, // hasAlpha
//                                colorSpace
//                        );
//                    } else {
//                        newresult = Bitmap.createBitmap(
//                                Math.max(topBitmap.getWidth(), scaledBottomBitmap.getWidth()),
//                                topBitmap.getHeight() + scaledBottomBitmap.getHeight(),
//                                config
//                        );
//                    }


                    // 在新的Bitmap上绘制两个Bitmap
                    Canvas canvas = new Canvas(newresult);
                    canvas.drawBitmap(src, 0, 0, null);
                    canvas.drawBitmap(scaledBottomBitmap, 0, topBitmap.getHeight(), null);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        Gainmap srcGainMap = src.getGainmap();

// 创建合并画布
                        Bitmap srcGainMapBitmap = srcGainMap.getGainmapContents();
                        float srcGainHeight = srcGainMapBitmap.getHeight();
                        float gsRatio = srcGainHeight/src.getHeight();

                        Bitmap bottomGainMapBitmap =  Bitmap.createBitmap((int) (scaledBottomBitmap.getWidth()*gsRatio),
                                (int) (scaledBottomBitmap.getHeight()*gsRatio),
                                scaledBottomBitmap.getConfig());
//                         bottomGainMapBitmap =bitmap.getGainmap().getGainmapContents();
                        int totalHeight = (int) (srcGainMapBitmap.getHeight() + scaledBottomBitmap.getHeight()*gsRatio);
                        Bitmap mergedBitmap = Bitmap.createBitmap(
                                srcGainMapBitmap.getWidth(),
                                totalHeight,
                                Bitmap.Config.ARGB_8888,  // 保持HDR兼容
                                true,  // 启用硬件加速
                                srcGainMapBitmap.getColorSpace()  // 同步色彩空间
                        );

// 绘制拼接
                        Canvas canvas1 = new Canvas(mergedBitmap);
                        canvas1.drawBitmap(srcGainMapBitmap, 0, 0, null); // 上部
                        canvas1.drawBitmap(bottomGainMapBitmap,
                                0,
                                srcGainMapBitmap.getHeight(),  // Y偏移位置
                                null); // 下部

// 新建合并后的Gainmap
                        Gainmap mergedGainmap = src.getGainmap(); // 继承基础参数

                        Bitmap scaledMergedBitmap = Bitmap.createScaledBitmap(mergedBitmap,
                                newresult.getWidth(),
                                newresult.getHeight(),
                                true);
                        mergedGainmap.setGainmapContents(mergedBitmap);

//                        mergedGainmap.setGainmapContents(mergedBitmap);

                        newresult.setGainmap(mergedGainmap);
                    }

//            //todo 展示形式修改
//            imageView.setImageBitmap(newresult);

                    OutputStream os = null;
                    try {
                        // 保存图片到Pictures目录
                        File directory = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);
                        String originFileName =getFileName(selectedImageUri);
                        String filename = originFileName.substring(0,originFileName.length()-4)+"_logo.jpg";
                        File file = new File(directory, filename);

                        os = new FileOutputStream(file);
                        newresult.compress(Bitmap.CompressFormat.JPEG, 100, os); // 以JPEG格式保存
                        os.flush();
                        os.close();
                        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, null);
                        Toast.makeText(this, "加水印图片已保存到" + directory, Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(columnIndex);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}