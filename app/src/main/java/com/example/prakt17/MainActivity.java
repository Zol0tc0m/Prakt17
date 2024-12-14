package com.example.prakt17;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private EditText nameEditText, descriptionEditText, priceEditText;
    private Button addButton, selectImageButton;
    private ImageView productImageView;

    private ListView clothListView;
    private List<Clothes> clothList;

    private String selectedItemId = null;
    private String selectedImageBase64 = null;

    private static final int REQUEST_CODE_IMAGE_PICK = 1;
    private static final int REQUEST_CODE_DELETE_ITEM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        clothListView = findViewById(R.id.clothListView);

        loadClothList();

        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        addButton = findViewById(R.id.addButton);
        selectImageButton = findViewById(R.id.selectImageButton);
        productImageView = findViewById(R.id.productImageView);

        // Кнопка для выбора изображения
        selectImageButton.setOnClickListener(v -> openGallery());

        // Кнопка для добавления товара
        addButton.setOnClickListener(v -> addClothItem());

        clothListView.setOnItemClickListener((parent, view, position, id) -> {
            Clothes selectedItem = clothList.get(position);
            openClothDescActivity(selectedItem.getId());
        });

    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                selectedImageBase64 = bitmapToBase64(bitmap);
                productImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == REQUEST_CODE_DELETE_ITEM && resultCode == RESULT_OK) {
            loadClothList();
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void addClothItem() {
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String price = priceEditText.getText().toString();

        if (name.isEmpty() || description.isEmpty() || price.isEmpty() || selectedImageBase64 == null) {
            Toast.makeText(this, "Заполните все поля и выберите изображение", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = UUID.randomUUID().toString();
        Clothes cloth = new Clothes(id, name, description, price, selectedImageBase64);
        Paper.book().write(id, cloth);

        Toast.makeText(this, "Товар добавлен", Toast.LENGTH_SHORT).show();
        clearInputs();
        loadClothList();
    }


    private void clearInputs() {
        nameEditText.setText("");
        descriptionEditText.setText("");
        priceEditText.setText("");
        productImageView.setImageResource(0);
        selectedItemId = null;
        selectedImageBase64 = null;
    }

    private void loadClothList() {
        clothList = new ArrayList<>();
        List<String> allKeys = Paper.book().getAllKeys(); // Получаем все ключи из PaperDB

        for (String key : allKeys) {
            Clothes item = Paper.book().read(key); // Читаем объект по ключу
            if (item != null) {
                clothList.add(item); // Добавляем в список
            }
        }
        ClothAdapter adapter = new ClothAdapter(this, clothList);
        clothListView.setAdapter(adapter);
    }

    private void openClothDescActivity(String itemId) {
        Intent intent = new Intent(this, DescClothesActivity.class);
        intent.putExtra("itemId", itemId);
        startActivityForResult(intent, REQUEST_CODE_DELETE_ITEM);
    }

}