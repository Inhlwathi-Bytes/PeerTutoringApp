package com.inhlwathibytes.peertutoringapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import java.text.NumberFormat;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "InhlwathiTutors.db";
    public static final int DATABASE_VERSION = 5; // Incremented version for payments table

    // Table Names
    public static final String TABLE_TUTORS = "tutors";
    public static final String TABLE_STUDENTS = "students";
    public static final String TABLE_REQUESTS = "requests";
    public static final String TABLE_APPOINTMENTS = "appointments";
    public static final String TABLE_RATINGS = "ratings";
    public static final String TABLE_PAYMENTS = "payments";

    // Common Columns
    public static final String KEY_ID = "id";

    // Tutor Table Columns
    public static final String TUTOR_ID = KEY_ID;
    public static final String TUTOR_NAME = "name";
    public static final String TUTOR_SURNAME = "surname";
    public static final String TUTOR_SUBJECT = "subject";
    public static final String TUTOR_STUDENT_NUMBER = "student_number";
    public static final String TUTOR_CATEGORY = "category";
    public static final String TUTOR_DESCRIPTION = "description";
    public static final String TUTOR_EMAIL = "email";
    public static final String TUTOR_PHONE = "phone";
    public static final String TUTOR_PASSWORD = "password";
    public static final String TUTOR_RATING = "rating";
    public static final String TUTOR_RATING_COUNT = "rating_count";

    // Student Table Columns
    public static final String STUDENT_ID = KEY_ID;
    public static final String STUDENT_NAME = "name";
    public static final String STUDENT_SURNAME = "surname";
    public static final String STUDENT_NUMBER = "student_number";
    public static final String STUDENT_INSTITUTION = "institution";
    public static final String STUDENT_EMAIL = "email";
    public static final String STUDENT_PHONE = "phone";
    public static final String STUDENT_PASSWORD = "password";

    // Request Table Columns
    public static final String REQUEST_ID = KEY_ID;
    public static final String REQUEST_TUTOR_ID = "tutor_id";
    public static final String REQUEST_STUDENT_ID = "student_id";
    public static final String REQUEST_STUDENT_NAME = "student_name";
    public static final String REQUEST_STUDENT_EMAIL = "student_email";
    public static final String REQUEST_STUDENT_PHONE = "student_phone";
    public static final String REQUEST_STATUS = "status";
    public static final String REQUEST_DATE = "request_date";

    // Appointment Table Columns
    public static final String APPOINTMENT_ID = KEY_ID;
    public static final String APPOINTMENT_TUTOR_ID = "tutor_id";
    public static final String APPOINTMENT_STUDENT_ID = "student_id";
    public static final String APPOINTMENT_DATE = "date";
    public static final String APPOINTMENT_TIME = "time";
    public static final String APPOINTMENT_DURATION = "duration";
    public static final String APPOINTMENT_SUBJECT = "subject";
    public static final String APPOINTMENT_STATUS = "status";
    public static final String APPOINTMENT_CREATED_AT = "created_at";

    // Rating Table Columns
    public static final String RATING_ID = KEY_ID;
    public static final String RATING_TUTOR_ID = "tutor_id";
    public static final String RATING_STUDENT_ID = "student_id";
    public static final String RATING_VALUE = "rating_value";
    public static final String RATING_DATE = "rating_date";

    // Payment Table Columns
    public static final String PAYMENT_ID = KEY_ID;
    public static final String PAYMENT_STUDENT_ID = "student_id";
    public static final String PAYMENT_AMOUNT = "amount";
    public static final String PAYMENT_DATE = "payment_date";
    public static final String PAYMENT_STATUS = "status";
    public static final String PAYMENT_REFERENCE = "reference";
    public static final String PAYMENT_APPOINTMENT_ID = "appointment_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAllTables(db);
    }

    private void createAllTables(SQLiteDatabase db) {
        // Create tutors table
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
                TUTOR_PASSWORD + " TEXT, " +
                TUTOR_RATING + " REAL DEFAULT 0, " +
                TUTOR_RATING_COUNT + " INTEGER DEFAULT 0)";
        db.execSQL(createTutorsTable);

        // Create students table
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

        // Create requests table
        String createRequestsTable = "CREATE TABLE " + TABLE_REQUESTS + " (" +
                REQUEST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                REQUEST_TUTOR_ID + " INTEGER, " +
                REQUEST_STUDENT_ID + " INTEGER, " +
                REQUEST_STUDENT_NAME + " TEXT, " +
                REQUEST_STUDENT_EMAIL + " TEXT, " +
                REQUEST_STUDENT_PHONE + " TEXT, " +
                REQUEST_STATUS + " TEXT DEFAULT 'pending', " +
                REQUEST_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createRequestsTable);

        // Create appointments table
        String createAppointmentsTable = "CREATE TABLE " + TABLE_APPOINTMENTS + " (" +
                APPOINTMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                APPOINTMENT_TUTOR_ID + " INTEGER, " +
                APPOINTMENT_STUDENT_ID + " INTEGER, " +
                APPOINTMENT_DATE + " TEXT, " +
                APPOINTMENT_TIME + " TEXT, " +
                APPOINTMENT_DURATION + " TEXT, " +
                APPOINTMENT_SUBJECT + " TEXT, " +
                APPOINTMENT_STATUS + " TEXT DEFAULT 'pending', " +
                APPOINTMENT_CREATED_AT + " TEXT DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createAppointmentsTable);

        // Create ratings table
        String createRatingsTable = "CREATE TABLE " + TABLE_RATINGS + " (" +
                RATING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RATING_TUTOR_ID + " INTEGER, " +
                RATING_STUDENT_ID + " INTEGER, " +
                RATING_VALUE + " REAL, " +
                RATING_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                "UNIQUE(" + RATING_TUTOR_ID + ", " + RATING_STUDENT_ID + "))";
        db.execSQL(createRatingsTable);

        // Create payments table
        String createPaymentsTable = "CREATE TABLE " + TABLE_PAYMENTS + " (" +
                PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PAYMENT_STUDENT_ID + " INTEGER, " +
                PAYMENT_AMOUNT + " REAL, " +
                PAYMENT_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                PAYMENT_STATUS + " TEXT DEFAULT 'pending', " +
                PAYMENT_REFERENCE + " TEXT, " +
                PAYMENT_APPOINTMENT_ID + " INTEGER)";
        db.execSQL(createPaymentsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables and recreate them for major version changes
        if (oldVersion < 4) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATINGS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPOINTMENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TUTORS);
            onCreate(db);
        } else {
            // Handle incremental upgrades
            if (oldVersion < 2) {
                // Add rating columns to tutors table
                db.execSQL("ALTER TABLE " + TABLE_TUTORS + " ADD COLUMN " + TUTOR_RATING + " REAL DEFAULT 0");
                db.execSQL("ALTER TABLE " + TABLE_TUTORS + " ADD COLUMN " + TUTOR_RATING_COUNT + " INTEGER DEFAULT 0");
            }
            if (oldVersion < 3) {
                // Create ratings table
                String createRatingsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_RATINGS + " (" +
                        RATING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RATING_TUTOR_ID + " INTEGER, " +
                        RATING_STUDENT_ID + " INTEGER, " +
                        RATING_VALUE + " REAL, " +
                        RATING_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                        "UNIQUE(" + RATING_TUTOR_ID + ", " + RATING_STUDENT_ID + "))";
                db.execSQL(createRatingsTable);
            }
            if (oldVersion < 4) {
                // Create payments table
                String createPaymentsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PAYMENTS + " (" +
                        PAYMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PAYMENT_STUDENT_ID + " INTEGER, " +
                        PAYMENT_AMOUNT + " REAL, " +
                        PAYMENT_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP, " +
                        PAYMENT_STATUS + " TEXT DEFAULT 'pending', " +
                        PAYMENT_REFERENCE + " TEXT, " +
                        PAYMENT_APPOINTMENT_ID + " INTEGER)";
                db.execSQL(createPaymentsTable);
            }
        }
    }

    // Helper method to check if table exists
    private boolean tableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{tableName});
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    // Payment methods with defensive checks
    public double getOutstandingBalance(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();

        if (!tableExists(db, TABLE_PAYMENTS) || !tableExists(db, TABLE_APPOINTMENTS)) {
            return 0.0;
        }

        String query = "SELECT SUM(" + APPOINTMENT_DURATION + " * 0.5) FROM " + TABLE_APPOINTMENTS +
                " WHERE " + APPOINTMENT_STUDENT_ID + " = ? AND " +
                APPOINTMENT_STATUS + " = 'approved' AND " +
                APPOINTMENT_ID + " NOT IN (SELECT " + PAYMENT_APPOINTMENT_ID +
                " FROM " + TABLE_PAYMENTS + " WHERE " + PAYMENT_STATUS + " = 'paid')";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        double balance = 0;
        if (cursor != null && cursor.moveToFirst()) {
            balance = cursor.getDouble(0);
            cursor.close();
        }
        return balance;
    }

    public boolean recordPayment(int studentId, double amount, String reference, int appointmentId) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (!tableExists(db, TABLE_PAYMENTS)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(PAYMENT_STUDENT_ID, studentId);
        values.put(PAYMENT_AMOUNT, amount);
        values.put(PAYMENT_REFERENCE, reference);
        values.put(PAYMENT_STATUS, "paid");
        values.put(PAYMENT_APPOINTMENT_ID, appointmentId);

        long result = db.insert(TABLE_PAYMENTS, null, values);
        return result != -1;
    }

    public Cursor getPaymentHistory(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();

        if (!tableExists(db, TABLE_PAYMENTS)) {
            return null;
        }

        return db.query(TABLE_PAYMENTS,
                null,
                PAYMENT_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)},
                null, null,
                PAYMENT_DATE + " DESC");
    }

    // ... [Keep all your existing methods unchanged below this point] ...
    // All your other methods (for tutors, students, requests, appointments, ratings) remain exactly the same
    // as in your original code, just copy them here unchanged

    // ... [Previous methods remain the same until rating-related methods] ...

    // Check if student has already rated this tutor
    public boolean hasStudentRatedTutor(int tutorId, int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RATINGS,
                new String[]{RATING_ID},
                RATING_TUTOR_ID + " = ? AND " + RATING_STUDENT_ID + " = ?",
                new String[]{String.valueOf(tutorId), String.valueOf(studentId)},
                null, null, null);

        boolean hasRated = cursor.getCount() > 0;
        cursor.close();
        return hasRated;
    }

    // Update tutor rating (with student tracking)
    public boolean updateTutorRating(int tutorId, int studentId, float newRating) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First check if student has already rated this tutor
        if (hasStudentRatedTutor(tutorId, studentId)) {
            return false; // Already rated
        }

        // Insert the new rating
        ContentValues ratingValues = new ContentValues();
        ratingValues.put(RATING_TUTOR_ID, tutorId);
        ratingValues.put(RATING_STUDENT_ID, studentId);
        ratingValues.put(RATING_VALUE, newRating);
        long ratingResult = db.insertWithOnConflict(TABLE_RATINGS, null, ratingValues, SQLiteDatabase.CONFLICT_IGNORE);

        if (ratingResult == -1) {
            return false; // Failed to insert rating
        }

        // Calculate new average rating
        Cursor cursor = db.rawQuery("SELECT AVG(" + RATING_VALUE + "), COUNT(*) FROM " + TABLE_RATINGS +
                " WHERE " + RATING_TUTOR_ID + " = ?", new String[]{String.valueOf(tutorId)});

        if (cursor != null && cursor.moveToFirst()) {
            float updatedRating = cursor.getFloat(0);
            int ratingCount = cursor.getInt(1);
            cursor.close();

            // Update the tutor's rating
            ContentValues tutorValues = new ContentValues();
            tutorValues.put(TUTOR_RATING, updatedRating);
            tutorValues.put(TUTOR_RATING_COUNT, ratingCount);

            int result = db.update(TABLE_TUTORS, tutorValues,
                    TUTOR_ID + " = ?",
                    new String[]{String.valueOf(tutorId)});
            return result > 0;
        }
        return false;
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

    // Insert a new request
    public boolean insertRequest(int tutorId, int studentId, String studentName,
                                 String studentEmail, String studentPhone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REQUEST_TUTOR_ID, tutorId);
        values.put(REQUEST_STUDENT_ID, studentId);
        values.put(REQUEST_STUDENT_NAME, studentName);
        values.put(REQUEST_STUDENT_EMAIL, studentEmail);
        values.put(REQUEST_STUDENT_PHONE, studentPhone);

        long result = db.insert(TABLE_REQUESTS, null, values);
        return result != -1;
    }

    // Get all requests for a tutor
    public Cursor getRequestsForTutor(int tutorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_REQUESTS,
                null,
                REQUEST_TUTOR_ID + " = ?",
                new String[]{String.valueOf(tutorId)},
                null, null,
                REQUEST_DATE + " DESC");
    }

    // Update request status
    public boolean updateRequestStatus(int requestId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REQUEST_STATUS, status);

        int result = db.update(TABLE_REQUESTS, values,
                REQUEST_ID + " = ?",
                new String[]{String.valueOf(requestId)});
        return result > 0;
    }

    // Get all requests for a student
    public Cursor getRequestsForStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_REQUESTS,
                null,
                REQUEST_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)},
                null, null,
                REQUEST_DATE + " DESC");
    }

    // Get tutor details by ID
    public Tutor getTutorById(int tutorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TUTORS,
                null,
                TUTOR_ID + " = ?",
                new String[]{String.valueOf(tutorId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Tutor tutor = new Tutor(
                    cursor.getInt(cursor.getColumnIndexOrThrow(TUTOR_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_SUBJECT)),
                    cursor.getFloat(cursor.getColumnIndexOrThrow(TUTOR_RATING)), // Get actual rating from DB
                    cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_SURNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TUTOR_PHONE))
            );
            cursor.close();
            return tutor;
        }
        return null;
    }

    // Update tutor rating
    public boolean updateTutorRating(int tutorId, float newRating) {
        SQLiteDatabase db = this.getWritableDatabase();

        // First get current rating and count
        Cursor cursor = db.query(TABLE_TUTORS,
                new String[]{TUTOR_RATING, TUTOR_RATING_COUNT},
                TUTOR_ID + " = ?",
                new String[]{String.valueOf(tutorId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            float currentRating = cursor.getFloat(cursor.getColumnIndexOrThrow(TUTOR_RATING));
            int ratingCount = cursor.getInt(cursor.getColumnIndexOrThrow(TUTOR_RATING_COUNT));
            cursor.close();

            // Calculate new average rating
            float totalRating = currentRating * ratingCount;
            totalRating += newRating;
            ratingCount++;
            float updatedRating = totalRating / ratingCount;

            // Update the tutor's rating
            ContentValues values = new ContentValues();
            values.put(TUTOR_RATING, updatedRating);
            values.put(TUTOR_RATING_COUNT, ratingCount);

            int result = db.update(TABLE_TUTORS, values,
                    TUTOR_ID + " = ?",
                    new String[]{String.valueOf(tutorId)});
            return result > 0;
        }
        return false;
    }

    // Get all approved students for a tutor
    public Cursor getApprovedStudentsForTutor(int tutorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_REQUESTS,
                null,
                REQUEST_TUTOR_ID + " = ? AND " + REQUEST_STATUS + " = ?",
                new String[]{String.valueOf(tutorId), "approved"},
                null, null,
                REQUEST_DATE + " DESC");
    }

    // Get student details by ID
    public Student getStudentById(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENTS,
                null,
                STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Student student = new Student(
                    cursor.getInt(cursor.getColumnIndexOrThrow(STUDENT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(STUDENT_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(STUDENT_SURNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(STUDENT_NUMBER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(STUDENT_INSTITUTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(STUDENT_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(STUDENT_PHONE))
            );
            cursor.close();
            return student;
        }
        return null;
    }

    public boolean insertAppointment(int tutorId, int studentId, String date, String time,
                                     String duration, String subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APPOINTMENT_TUTOR_ID, tutorId);
        values.put(APPOINTMENT_STUDENT_ID, studentId);
        values.put(APPOINTMENT_DATE, date);
        values.put(APPOINTMENT_TIME, time);
        values.put(APPOINTMENT_DURATION, duration);
        values.put(APPOINTMENT_SUBJECT, subject);

        long result = db.insert(TABLE_APPOINTMENTS, null, values);
        return result != -1;
    }

    public Cursor getApprovedRequestsForStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_REQUESTS,
                null,
                REQUEST_STUDENT_ID + " = ? AND " + REQUEST_STATUS + " = ?",
                new String[]{String.valueOf(studentId), "approved"},
                null, null,
                REQUEST_DATE + " DESC");
    }

    public Cursor getPendingAppointmentsForTutor(int tutorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_APPOINTMENTS,
                null,
                APPOINTMENT_TUTOR_ID + " = ? AND " + APPOINTMENT_STATUS + " = ?",
                new String[]{String.valueOf(tutorId), "pending"},
                null, null,
                APPOINTMENT_CREATED_AT + " DESC");
    }

    public boolean updateAppointmentStatus(int appointmentId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(APPOINTMENT_STATUS, status);

        int result = db.update(TABLE_APPOINTMENTS, values,
                APPOINTMENT_ID + " = ?",
                new String[]{String.valueOf(appointmentId)});
        return result > 0;
    }

    public Cursor getApprovedAppointmentsForTutor(int tutorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_APPOINTMENTS,
                null,
                APPOINTMENT_TUTOR_ID + " = ? AND " + APPOINTMENT_STATUS + " = ?",
                new String[]{String.valueOf(tutorId), "approved"},
                null, null,
                APPOINTMENT_DATE + " ASC, " + APPOINTMENT_TIME + " ASC");
    }

    public Cursor getApprovedAppointmentsForStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_APPOINTMENTS,
                null,
                APPOINTMENT_STUDENT_ID + " = ? AND " + APPOINTMENT_STATUS + " = ?",
                new String[]{String.valueOf(studentId), "approved"},
                null, null,
                APPOINTMENT_DATE + " ASC, " + APPOINTMENT_TIME + " ASC");
    }

    public Cursor getAllAppointmentsForStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_APPOINTMENTS,
                null,
                APPOINTMENT_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)},
                null, null,
                APPOINTMENT_CREATED_AT + " DESC");
    }
    public Cursor getAppointmentsForTutor(int tutorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_APPOINTMENTS,
                null,
                APPOINTMENT_TUTOR_ID + " = ?",
                new String[]{String.valueOf(tutorId)},
                null, null,
                APPOINTMENT_DATE + " DESC, " + APPOINTMENT_TIME + " DESC");
    }




}