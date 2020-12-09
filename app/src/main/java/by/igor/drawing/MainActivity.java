package by.igor.drawing;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import top.defaults.colorpicker.ColorPickerPopup;

public class MainActivity extends AppCompatActivity {

    private DrawingView drawView;
    private Button button;
    private ImageButton colorPickerButton;
    private ImageButton pen;
    private ImageButton erase;
    private ImageButton shape;

    private int status = 0;
    Drawable drawable;

    @SuppressLint({"ResourceType"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        drawView = findViewById(R.id.drawing);
        button = findViewById(R.id.colorBtn);
        drawView.setButton(button);
        button.setBackgroundColor(Color.BLACK);
        pen = findViewById(R.id.draw_btn);
        erase = findViewById(R.id.erase_btn);
        shape = findViewById(R.id.shapes_btn);

        try {
            drawable = Drawable.createFromXml(getResources(), getResources().getXml(R.drawable.btn_border));
            pen.setForeground(drawable);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException ignored) {
        }

        colorPickerButton = findViewById(R.id.btn_main_color_picker_dialog);
        colorPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog();
            }
        });

    }

    private void showColorPickerDialog() {
        new ColorPickerPopup.Builder(this)
                .initialColor(Color.RED) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("Выбрать")
                .cancelTitle("Отмена")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        drawView.setPaintColor(""+color);
                        colorPickerButton.setBackgroundColor(color);
                    }
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onErase(View view) {
        drawView.setErase(true);
        status = 0;
        drawView.setStatus(0);

        pen.setForeground(null);
        shape.setForeground(null);
        erase.setForeground(drawable);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onDraw(View view) {
        drawView.setErase(false);
        status = 0;
        drawView.setStatus(0);

        pen.setForeground(drawable);
        shape.setForeground(null);
        erase.setForeground(null);
    }

    public void onResetting(View view) {
        drawView.recreate();
    }

    public void onColor(View view) {
        Button v = (Button) view;
        int color = v.getCurrentTextColor();
        drawView.setPaintColor(""+color);
    }

    public void onShape(View view) {
        String[] items = {"Прямоугольник", "Круг", "Треугольник"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Выберите фигуру")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("TAG", "onClick: " + i);
                        switch (i) {
                            case 0:
                                status = 1;
                                drawView.setStatus(1);
                                break;
                            case 1:
                                status = 2;
                                drawView.setStatus(2);
                                break;
                            case 2:
                                status = 3;
                                drawView.setStatus(3);
                                break;
                        }

                        pen.setForeground(null);
                        shape.setForeground(drawable);
                        erase.setForeground(null);
                        drawView.setErase(false);

                    }
                }).setPositiveButton("ОК", null);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void onNew(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 111);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                drawView.loadFile(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSize(View view) {
        String[] items = {"Малый", "Средний", "Большой"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Выберите размер")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("TAG", "onClick: " + i);
                        switch (i) {
                            case 0:
                                drawView.setSize(0);
                                break;
                            case 1:
                                drawView.setSize(1);
                                break;
                            case 2:
                                drawView.setSize(2);
                                break;
                        }
                    }
                }).setPositiveButton("ОК", null);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
