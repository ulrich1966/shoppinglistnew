package com.example.shoppinglisthq;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ShoppingMemoDataSource {

    private static final String TAG = ShoppingMemoDataSource.class.getSimpleName();

    private SQLiteDatabase database;
    private ShoppingMemoDbHelper dbHelper;

    private String[] columns = {
            ShoppingMemoDbHelper.COLUMN_ID,
            ShoppingMemoDbHelper.COLUMN_PRODUCT,
            ShoppingMemoDbHelper.COLUMN_QUANTITY,
            ShoppingMemoDbHelper.COLUMN_CHECKED
    };

    public ShoppingMemoDataSource(Context context){
        Log.d(TAG, "DataSource erzeugt jetzt den dbHelper");
        dbHelper = new ShoppingMemoDbHelper(context);
    }

    public ShoppingMemo createShoppingMemo(String product, int quantity){
        ContentValues values= new ContentValues();
        values.put(ShoppingMemoDbHelper.COLUMN_PRODUCT,product);
        values.put(ShoppingMemoDbHelper.COLUMN_QUANTITY,quantity);

        long insertId = database.insert(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,null,values);

        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,columns,
                ShoppingMemoDbHelper.COLUMN_ID + "=" + insertId,null,null,null,null);

        cursor.moveToFirst();
        ShoppingMemo shoppingMemo = cursorToShoppingMemo(cursor);
        cursor.close();
        return shoppingMemo;
    }

    public ShoppingMemo updateShoppingMemo(long id, String newProduct, int newQuantity, boolean newChecked){
        int intValueChecked = newChecked? 1 : 0;

        ContentValues values = new ContentValues();
        values.put(ShoppingMemoDbHelper.COLUMN_PRODUCT,newProduct);
        values.put(ShoppingMemoDbHelper.COLUMN_QUANTITY,newQuantity);
        values.put(ShoppingMemoDbHelper.COLUMN_CHECKED, intValueChecked);

        database.update(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,values,ShoppingMemoDbHelper.COLUMN_ID + "=" + id,
                null);

        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,columns,ShoppingMemoDbHelper.COLUMN_ID +"="+id,
                null,null,null,null);
        cursor.moveToFirst();
        ShoppingMemo memo = cursorToShoppingMemo(cursor);
        cursor.close();
        return memo;
    }

    public void deleteShoppingMemo(ShoppingMemo shoppingMemo){
        long id = shoppingMemo.getId();
        database.delete(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,
                ShoppingMemoDbHelper.COLUMN_ID + "=" + id,null);
        Log.d(TAG, "Eintrag gelöscht! ID: " + id + " Inhalt: " + shoppingMemo.toString());
    }

    private ShoppingMemo cursorToShoppingMemo(Cursor cursor) {
        int idIndex = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_ID);
        int idProduct = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_PRODUCT);
        int idQuantity = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_QUANTITY);
        int idChecked = cursor.getColumnIndex(ShoppingMemoDbHelper.COLUMN_CHECKED);

        String product = cursor.getString(idProduct);
        int quantity = cursor.getInt(idQuantity);
        long id = cursor.getLong(idIndex);
        int intValueChecked = cursor.getInt(idChecked);

        boolean isCkecked = intValueChecked != 0 ;

        ShoppingMemo shoppingMemo = new ShoppingMemo(product,quantity,id,isCkecked);
        return shoppingMemo;
    }

    public List<ShoppingMemo> getAllShoppingMemos(){
        List<ShoppingMemo> shoppingMemoList = new ArrayList<>();
        Cursor cursor = database.query(ShoppingMemoDbHelper.TABLE_SHOPPING_LIST,columns,
                null,null,null,null,null);
        cursor.moveToFirst();
        ShoppingMemo shoppingMemo;

        while (!cursor.isAfterLast()){
            shoppingMemo = cursorToShoppingMemo(cursor);
            shoppingMemoList.add(shoppingMemo);
            Log.d(TAG, "ID: " + shoppingMemo.getId() + ", Inhalt: " + shoppingMemo.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return shoppingMemoList;
    }


    public void open(){
        Log.d(TAG, "Eine Referenz auf die Datenbank wird angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(TAG, "open: Referenz erhalten. Pfad zur DB: " + database.getPath());
    }

    public void close(){
        dbHelper.close();
        Log.d(TAG, "Datenbank mit DbHelper geschlossen");
    }
}
