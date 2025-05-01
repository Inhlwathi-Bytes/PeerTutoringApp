package com.inhlwathibytes.peertutoringapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "InhlwathiTutors.db";
    public static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_TUTORS = "tutors";
    public static final String TABLE_STUDENTS = "students";
    public static final String TABLE_TUTOR_REQUESTS = "tutor_requests";

    // Tutor Table Columns
    public static final String TUTOR_ID = "id";
    public static final String TUTOR_NAME = "name";
    public static final String TUTOR_SURNAME = "surname";
    public static final String TUTOR_SUBJECT = "subject";
    public static final String TUTOR_STUDENT_NUMBER = "student_number";
    public static final String TUTOR_CATEGORY = "category";
    public static final String TUTOR_DESCRIPTION = "description";
    public static final String TUTOR_EMAIL = "email";
    public static final String TUTOR_PHONE = "phone";
    public static final String TUTOR_PASSWORD = "password";

    // Student Table Columns
    public static final String STUDENT_ID = "id";
    public static final String STUDENT_NAME = "name";
    public static final String STUDENT_SURNAME = "surname";
    public static final String STUDENT_NUMBER = "student_number";
    public static final String STUDENT_INSTITUTION = "institution";
    public static final String STUDENT_EMAIL = "email";
    public static final String STUDENT_PHONE = "phone";
    public static final String STUDENT_PASSWORD = "password";


    // Columns
    public static final String REQUEST_ID = "id";
    public static final String REQUEST_STUDENT_EMAIL = "student_email";
    public static final String REQUEST_TUTOR_EMAIL = "tutor_email";
    public static final String REQUEST_STATUS = "status";  // Pending, Accepted, Declined

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTutorsTable = "CREATE TABLE " + TABLE_TUTORS + " (" +
                TUTOR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TUTOR_NAME + " TEXT, " +
                TUTOR_SURNAME + " TEXT, " +
                TUTOR_SUBJECT + " TEXT, " +
                TUTOR_STUDENT_NUMBER + " TEXT, " +
                TUTOR_CATEGORY + " TEXT, " +
                TUTOR_DESCRIPTION + " TEXT, " +
                TUTOR_EMAIL + " TEXT, " +
                TUTOR_PHONE + " TEXT, " +
                TUTOR_PASSWORD + " TEXT)";
        db.execSQL(createTutorsTable);

        String createStudentsTable = "CREATE TABLE " + TABLE_STUDENTS + " (" +
                STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                STUDENT_NAME + " TEXT, " +
                STUDENT_SURNAME + " TEXT, " +
                STUDENT_NUMBER + " TEXT, " +
                STUDENT_INSTITUTION + " TEXT, " +
                STUDENT_EMAIL + " TEXT, " +
                STUDENT_PHONE + " TEXT, " +
                STUDENT_PASSWORD + " TEXT)";
        db.execSQL(createStudentsTable);

        String createTutorRequestsTable = "CREATE TABLE " + TABLE_TUTOR_REQUESTS + " (" +
                REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                REQUEST_STUDENT_EMAIL + " TEXT, " +
                REQUEST_TUTOR_EMAIL + " TEXT, " +
                REQUEST_STATUS + " TEXT)";
        db.execSQL(createTutorRequestsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TUTORS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // Insert a new tutor
    public boolean insertTutor(String name, String surname, String subject, String studentNumber,
                               String category, String description, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TUTOR_NAME, name);
        values.put(TUTOR_SURNAME, surname);
        values.put(TUTOR_SUBJECT, subject);
        values.put(TUTOR_STUDENT_NUMBER, studentNumber);
        values.put(TUTOR_CATEGORY, category);
        values.put(TUTOR_DESCRIPTION, description);
        values.put(TUTOR_EMAIL, email);
        values.put(TUTOR_PHONE, phone);
        values.put(TUTOR_PASSWORD, password);

        long result = db.insert(TABLE_TUTORS, null, values);
        return result != -1;
    }

    // Insert a new student
    public boolean insertStudent(String name, String surname, String studentNumber, String institution,
                                 String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STUDENT_NAME, name);
        values.put(STUDENT_SURNAME, surname);
        values.put(STUDENT_NUMBER, studentNumber);
        values.put(STUDENT_INSTITUTION, institution);
        values.put(STUDENT_EMAIL, email);
        values.put(STUDENT_PHONE, phone);
        values.put(STUDENT_PASSWORD, password);

        long result = db.insert(TABLE_STUDENTS, null, values);
        return result != -1;
    }
    public boolean insertTutorRequest(String studentEmail, String tutorEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REQUEST_STUDENT_EMAIL, studentEmail);
        values.put(REQUEST_TUTOR_EMAIL, tutorEmail);
        values.put(REQUEST_STATUS, "Pending");

        long result = db.insert(TABLE_TUTOR_REQUESTS, null, values);
        return result != -1;
    }
    public Cursor getRequestsForTutor(String tutorEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TUTOR_REQUESTS + " WHERE " + REQUEST_TUTOR_EMAIL + " = ?", new String[]{tutorEmail});
    }
    public Cursor getRequestsForStudent(String studentEmail) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TUTOR_REQUESTS + " WHERE " + REQUEST_STUDENT_EMAIL + " = ?", new String[]{studentEmail});
    }
    public boolean updateRequestStatus(int requestId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REQUEST_STATUS, newStatus);
        int rows = db.update(TABLE_TUTOR_REQUESTS, values, REQUEST_ID + "=?", new String[]{String.valueOf(requestId)});
        return rows > 0;
    }

}
