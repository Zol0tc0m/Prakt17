package com.example.prakt17;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import io.paperdb.Paper;

public class DescClothesActivity extends AppCompatActivity {
    private EditText nameEditText, descriptionEditText, priceEditText;
    private ImageView productImageView;
    private Button updateButton, deleteButton;

    private String itemId;
    private Clothes clothItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc_clothes);

        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        productImageView = findViewById(R.id.productImageView);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);

        itemId = getIntent().getStringExtra("itemId");
        if (itemId != null) {
            loadClothItem(itemId);
        } else {
            Toast.makeText(this, "Ошибка: товар не найден", Toast.LENGTH_SHORT).show();
            finish();
        }

        updateButton.setOnClickListener(v -> updateClothItem());
        deleteButton.setOnClickListener(v -> deleteClothItem());
    }

    private void loadClothItem(String itemId) {
        clothItem = Paper.book().read(itemId);
        if (clothItem != null) {
            nameEditText.setText(clothItem.getName());
            descriptionEditText.setText(clothItem.getDescription());
            priceEditText.setText(clothItem.getPrice());
            productImageView.setImageBitmap(base64ToBitmap(clothItem.getImageBase64()));
        } else {
            Toast.makeText(this, "Ошибка: товар не найден", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateClothItem() {
        String name = nameEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String price = priceEditText.getText().toString();

        if (name.isEmpty() || description.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        clothItem.setName(name);
        clothItem.setDescription(description);
        clothItem.setPrice(price);
        Paper.book().write(itemId, clothItem);

        Toast.makeText(this, "Товар обновлен", Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void deleteClothItem() {
        Paper.book().delete(itemId);
        Toast.makeText(this, "Товар удален", Toast.LENGTH_SHORT).show();

        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private Bitmap base64ToBitmap(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}