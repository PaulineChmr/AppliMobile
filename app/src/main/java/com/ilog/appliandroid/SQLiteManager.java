package com.ilog.appliandroid;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager sqLiteManager;

    private static final String DATABASE_NAME = "RecipientDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Recipient";
    private static final String COUNTER = "Counter";

    private static final String ID_FIELD = "id";
    private static final String PRENOM_FIELD = "prénom";
    private static final String NOM_FIELD = "nom";
    private static final String NUMERO_FIELD = "numéro";
    private static final String DELETED_FIELD = "deleted";

    @SuppressLint("SimpleDateFormat")
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public SQLiteManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQLiteManager instanceOfDatabase(Context context){
        if (sqLiteManager == null){
            sqLiteManager = new SQLiteManager(context);
        }
        return sqLiteManager;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        StringBuilder sql;
        sql = new StringBuilder()
                .append("CREATE TABLE ")
                .append(TABLE_NAME)
                .append("(")
                .append(COUNTER)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ")
                .append(ID_FIELD)
                .append(" INT, ")
                .append(PRENOM_FIELD)
                .append(" TEXT, ")
                .append(NOM_FIELD)
                .append(" TEXT, ")
                .append(NUMERO_FIELD)
                .append(" TEXT, ")
                .append(DELETED_FIELD)
                .append(" TEXT)");

        sqLiteDatabase.execSQL(sql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        /*switch (oldVersion){
            case 1:
                sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + NEW_COLUMN + " TEXT");
            case 2:
                sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + NEW_COLUMN + " TEXT");

        }*/
    }

    public void addRecipientToDatabase(Recipient recipient){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, recipient.getId());
        contentValues.put(PRENOM_FIELD, recipient.getPrenom());
        contentValues.put(NOM_FIELD, recipient.getNom());
        contentValues.put(NUMERO_FIELD, recipient.getNumero());
        contentValues.put(DELETED_FIELD, getStringFromDate(recipient.getDeleted()));

        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void populateRecipientListArray(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        try (Cursor result = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null))
        {
            if (result.getCount() != 0){
                while(result.moveToNext()){
                    int id = result.getInt(1);
                    String numero = result.getString(2);
                    String prenom = result.getString(3);
                    String nom = result.getString(4);
                    String stringDeleted = result.getString(5);
                    Date deleted = getDateFromString(stringDeleted);
                    Recipient recipient = new Recipient(id, numero, prenom, nom, deleted);
                    Recipient.recipientArrayList.add(recipient);
                }
            }
        }
    }

    public void updateRecipientInDB(Recipient recipient){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID_FIELD, recipient.getId());
        contentValues.put(PRENOM_FIELD, recipient.getPrenom());
        contentValues.put(NOM_FIELD, recipient.getNom());
        contentValues.put(NUMERO_FIELD, recipient.getNumero());
        contentValues.put(DELETED_FIELD, getStringFromDate(recipient.getDeleted()));

        sqLiteDatabase.update(TABLE_NAME, contentValues, ID_FIELD + " =? ", new String[]{String.valueOf(recipient.getNumero())});

    }

    private String getStringFromDate(Date date) {
        if(date == null)
            return null;
        return dateFormat.format(date);
    }

    private Date getDateFromString(String string){
        try{
            return dateFormat.parse(string);
        }
        catch (ParseException | NullPointerException e){
            return null;
        }
    }
}
