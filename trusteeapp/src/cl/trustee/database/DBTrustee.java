package cl.trustee.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cl.trustee.enums.EStatusConection;
import cl.trustee.view.Register;

public class DBTrustee extends SQLiteOpenHelper {
	public static String LOG_TABLE = "LOG";
	public static String LOG_FIELD_NAME = "name";
	public static String LOG_FIELD_NUMBER = "number";
	public static String LOG_FIELD_STAUTS = "status";
	public static String LOG_FIELD_DATE = "date";
	private static DBTrustee instance = null;

	private SQLiteDatabase db;
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "trustee.db";

	final String sqlCreateLOG = String.format(
			"CREATE TABLE %s (%s TEXT, %s TEXT, %s INTEGER, %s INTEGER)",
			LOG_TABLE, LOG_FIELD_NUMBER, LOG_FIELD_NAME, LOG_FIELD_STAUTS,
			LOG_FIELD_DATE);

	final String sqlSelectLOG = String.format(
			"select * from %s order by %s desc LIMIT 100", LOG_TABLE, LOG_FIELD_DATE);

	private DBTrustee(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		instance = this;
	}
	
	public static DBTrustee getInstance(Context context)
	{
	    if(instance == null)
	    {
	        instance = new DBTrustee(context);
	    }
	    return instance;
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sqlCreateLOG);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + LOG_TABLE);
		db.execSQL(sqlCreateLOG);
	}

	/**
	 * Obtiene la lista de clientes.
	 * 
	 * @return Lista de clientes.
	 */
	public List<Register> getListLog() {
		List<Register> list = new ArrayList<Register>();
		db = getReadableDatabase();
		Cursor cursor = db.rawQuery(sqlSelectLOG, null);
		if (cursor.moveToFirst()) {
			do {
				Register register = new Register();
				register.setNumber(cursor.getString(0));
				register.setDescription(cursor.getString(1));
				register.setStatus(EStatusConection.getById(cursor.getInt(2)));
				register.setDate(cursor.getLong(3));
				list.add(register);
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	/**
	 * Agrega el log una nueva consulta.
	 */
	public void addToLog(Register register) {
		ContentValues registro = new ContentValues();
		registro.put(LOG_FIELD_NUMBER, register.getNumber());
		registro.put(LOG_FIELD_NAME, register.getDescription());
		registro.put(LOG_FIELD_STAUTS, register.getStatus().getId());
		registro.put(LOG_FIELD_DATE, register.getDate());
		db = getWritableDatabase();
		db.insert(LOG_TABLE, null, registro);
		
		db.close();
	}

	public void clearLog() {
		db = getWritableDatabase();
		db.delete(LOG_TABLE, null, null);
		db.close();
	}

}
