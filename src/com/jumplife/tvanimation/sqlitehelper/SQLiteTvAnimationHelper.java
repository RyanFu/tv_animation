package com.jumplife.tvanimation.sqlitehelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import com.jumplife.tvanimation.TvAnimationApplication;
import com.jumplife.tvanimation.entity.Animate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteTvAnimationHelper extends SQLiteOpenHelper {
    private static final String   TvAnimationTable	  = "dramas";
    public  static final String   DB_NAME             = "anime.sqlite";                            // 資料庫名稱
    private static final int      DATABASE_VERSION    = 1;
    private final  Context 		  mContext;
    public  static String 		  DB_PATH_DATA;                                         // 資料庫版本
    private static SQLiteTvAnimationHelper helper;

    public static synchronized SQLiteTvAnimationHelper getInstance(Context context) {
        if(helper == null) {
            helper = new SQLiteTvAnimationHelper(context.getApplicationContext());
        }

        return helper;
    }
    
    public SQLiteTvAnimationHelper(Context context) {
    	super(context.getApplicationContext(), DB_NAME, null, DATABASE_VERSION);
    	this.mContext = context.getApplicationContext();
    	DB_PATH_DATA = mContext.getFilesDir().getAbsolutePath().replace("files", "databases") + "/";
    	Log.d(null, "data path : " + DB_PATH_DATA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(null, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(null, "onUpgrade");
    }

    public void closeHelper() {
        if(helper != null)
        	helper.close();
    }

    public void createDataBase() {

    	int checkVersion = DATABASE_VERSION;
    	if(TvAnimationApplication.shIO.getInt("checkversion", 0) < checkVersion) {
	        File dbf = new File(DB_PATH_DATA + DB_NAME);
			if(dbf.exists()){
			    dbf.delete();
			    TvAnimationApplication.shIO.edit().putInt("checkversion", checkVersion).commit();
				Log.d(null, "delete dbf");
			}
    	}
		
    	boolean dbExist = checkDataBase();
        if(dbExist){
            Log.d(null, "db exist");
        }else{
            File dir = new File(DB_PATH_DATA);
			if(!dir.exists()){
			    dir.mkdirs();
			}
			Log.d(null, "copy database");
			copyDataBase();			
        }
    }
    
    private boolean checkDataBase(){
    	File dbFile = new File(DB_PATH_DATA + DB_NAME);
        return dbFile.exists();
    }
    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() {
    	try {
            InputStream is = mContext.getAssets().open(DB_NAME);
            OutputStream os = new FileOutputStream(DB_PATH_DATA + DB_NAME);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }

            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(null, "copy data base failed");
        }
    }

    public boolean insertTvAnimations(SQLiteDatabase db, ArrayList<Animate> tvAnimations) {
        for (int i = 0; i < tvAnimations.size(); i++) {
            Animate tvAnimation = tvAnimations.get(i);
            if(tvAnimation != null)
            	insertTvAnimation(db, tvAnimation);
        }
        return true;
    }

    public long insertTvAnimation(SQLiteDatabase db, Animate tvAnimation) {
        if(!tvAnimation.getName().equals("") && !tvAnimation.getIntroduction().equals("") && 
        		!tvAnimation.getPosterUrl().equals("") && tvAnimation.getTypeId() > 0) {
        	db.execSQL(
	                "INSERT OR IGNORE INTO " + TvAnimationTable + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
	                new String[] {1+"", 0+"", "f", tvAnimation.getId()+"", tvAnimation.getName(), tvAnimation.getSeason(), 
	                		tvAnimation.getTypeId()+"", tvAnimation.getIntroduction(), tvAnimation.getPosterUrl(),	
	                		"f", tvAnimation.getViews()+"", tvAnimation.getEpsNumStr()});
        }
        return 0;
    }

    public ArrayList<Integer> findTvAnimationsIdNotInDB(SQLiteDatabase db, ArrayList<Integer> tvAnimationId) {
        ArrayList<Integer> returnsID = new ArrayList<Integer>();
        ArrayList<Integer> dbsID = new ArrayList<Integer>();
       
        try {
	        Cursor cursor = db.rawQuery("SELECT id FROM " + TvAnimationTable, null);
	        if (cursor != null) {
	            while (cursor.moveToNext()) {
	                dbsID.add(cursor.getInt(0));
	            }
	        }
	        cursor.close();
        } catch (Exception e) {
   	     	return returnsID;
        }

        HashSet<Integer> hashSet = new HashSet<Integer>(dbsID);
        for (Integer id : tvAnimationId) {
            if (!hashSet.contains(id))
                returnsID.add(id);
        }
        return returnsID;
    }
    
    public void updateTvAnimationIsShow(SQLiteDatabase db, ArrayList<Integer> a) {
    	String idLst = "";
        for (int i = 0; i < a.size(); i++)
            idLst = a.get(i) + "," + idLst;
        idLst = idLst.substring(0, idLst.length() - 1);
        
        try {
	        if(a.size() > 0) {
	        	db.execSQL("UPDATE " + TvAnimationTable + " SET 'is_show' = CASE WHEN id IN (" + idLst + ")  THEN 't' ELSE 'f' END;");
	        }
        } catch(Exception e) {
        	try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	if(a.size() > 0) {
	        	db.execSQL("UPDATE " + TvAnimationTable + " SET 'is_show' = CASE WHEN id IN (" + idLst + ")  THEN 't' ELSE 'f' END;");
	        }
        }
        
    }
    
    public void updateTvAnimationViews(SQLiteDatabase db, ArrayList<Animate> tvAnimations) {
    	String updateViews = "UPDATE " + TvAnimationTable + " SET views = CASE";
        for(int i=0; i<tvAnimations.size(); i++)
        	updateViews = updateViews + " WHEN id = " + tvAnimations.get(i).getId() + " THEN " + tvAnimations.get(i).getViews() + " ";
        updateViews = updateViews + "END ;";
        	
        db.execSQL(updateViews);
    }
    
    public void updateTvAnimationEps(SQLiteDatabase db, ArrayList<Animate> tvAnimations) {
    	StringBuilder updateViews = new StringBuilder();
    	updateViews.append("UPDATE ").append(TvAnimationTable).append(" SET eps_num_str = CASE");
        for(int i=0; i<tvAnimations.size(); i++)
        	updateViews.append(" WHEN id = ").append(tvAnimations.get(i).getId()).append(" THEN '").append(tvAnimations.get(i).getEpsNumStr()).append("' ");
        updateViews.append("END ;");
        	
        db.execSQL(updateViews.toString());
    }
    
    public void updateTvAnimationEps(SQLiteDatabase db, int tvAnimationId, String eps) {
    	db.execSQL("UPDATE " + TvAnimationTable + " SET eps_num_str = ? WHERE id = ?", 
        									new String[] {eps + "", tvAnimationId + ""});
    }
    
    public int getTvAnimationChapterRecord(SQLiteDatabase db, int tvAnimationId) throws SQLException {
    	Cursor cursor = db.rawQuery("SELECT chapter_record FROM " + TvAnimationTable + " WHERE id = '" + tvAnimationId + "'", null);
        int chapter = -1;
        if (cursor != null) {
        	while(cursor.moveToNext()) {
	            chapter = cursor.getInt(0);
        	}
            cursor.close();
        }
        return chapter;
    }
    
    public boolean updateTvAnimationChapterRecord(SQLiteDatabase db, int tvAnimationId, int current_chapter) {
        
    	String[] arrayOfString = new String[1];
        
        arrayOfString[0] = String.valueOf(tvAnimationId);
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("chapter_record", current_chapter);
        db.update(TvAnimationTable, localContentValues, "id = ?", arrayOfString);
        
        return true;
    }
    
    public int getTvAnimationTimeRecord(SQLiteDatabase db, int tvAnimationId) throws SQLException {
    	Cursor cursor = db.rawQuery("SELECT time_record FROM " + TvAnimationTable + " WHERE id = '" + tvAnimationId + "'", null);
        int chapter = -1;
        if (cursor != null) {
        	while(cursor.moveToNext()) {
	            chapter = cursor.getInt(0);
        	}
            cursor.close();
        }
        return chapter;
    }
    
    public boolean updateTvAnimationTimeRecord(SQLiteDatabase db, int tvAnimationId, int current_time) {
        
    	String[] arrayOfString = new String[1];
        
        arrayOfString[0] = String.valueOf(tvAnimationId);
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("time_record", current_time);
        db.update(TvAnimationTable, localContentValues, "id = ?", arrayOfString);
        
        return true;
    }
    
    public int getTvAnimationLike(SQLiteDatabase db, int tvAnimationId) throws SQLException {
    	Cursor cursor = db.rawQuery("SELECT like FROM " + TvAnimationTable + " WHERE id = '" + tvAnimationId + "'", null);
        int like = 0;
        if (cursor != null) {
        	while(cursor.moveToNext()) {
        		like = cursor.getInt(0);
        	}
            cursor.close();
        }
        return like;
    }
    
    public boolean updateTvAnimationLike(SQLiteDatabase db, int tvAnimationId, int tvAnimationLike) {
        
    	String[] arrayOfString = new String[1];
        
        arrayOfString[0] = String.valueOf(tvAnimationId);
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("like", tvAnimationLike);
        db.update(TvAnimationTable, localContentValues, "id = ?", arrayOfString);
        
        return true;
    }

    public ArrayList<Animate> getTvAnimationLikeList(SQLiteDatabase db) throws SQLException {
    	ArrayList<Animate> tvAnimation_lst = new ArrayList<Animate>();
    	try {
	        Cursor cursor = null;
	        cursor = db.rawQuery("SELECT id, name, season, poster, views FROM " + TvAnimationTable + " WHERE like = '1';", null);
	        if (cursor != null) {
	        	while(cursor.moveToNext()) {
		            Animate tvAnimation = new Animate();
		            tvAnimation.setId(cursor.getInt(0));
		            tvAnimation.setName(cursor.getString(1));
		            tvAnimation.setSeason(cursor.getString(2));
		            tvAnimation.setPosterUrl(cursor.getString(3));
		            tvAnimation.setViews(cursor.getInt(4));
		            tvAnimation_lst.add(tvAnimation);
	        	}
	            cursor.close();
	        }
	    } catch (Exception e) {
		     return tvAnimation_lst;
		}

        return tvAnimation_lst;
    }

    
    public Animate getTvAnimation(SQLiteDatabase db, int tvAnimationId) throws SQLException {
    	Log.d(null, "id : " + tvAnimationId);
    	Cursor cursor = db.rawQuery("SELECT id, name, introduction, poster, eps_num_str, views FROM " + TvAnimationTable + " WHERE id = " + tvAnimationId, null);
        Animate tvAnimation = new Animate();
        if (cursor != null) {
	        while (cursor.moveToNext()) {
	        	tvAnimation.setId(cursor.getInt(0));
	        	tvAnimation.setName(cursor.getString(1));
	        	tvAnimation.setIntroduction(cursor.getString(2));
	        	tvAnimation.setPosterUrl(cursor.getString(3));
	        	tvAnimation.setEpsNumStr(cursor.getString(4));
	        	tvAnimation.setViews(cursor.getInt(5));
	        }
	        cursor.close();
        }
        Log.d(null, "id22222 : " + tvAnimation.getEpsNumStr());
        return tvAnimation;
    }
    
    public boolean updateTvAnimationEpsNumViews(SQLiteDatabase db, Animate tvAnimation) {
        
    	String[] arrayOfString = new String[1];
        
        arrayOfString[0] = String.valueOf(tvAnimation.getId());
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("eps_num_str", tvAnimation.getEpsNumStr());
        localContentValues.put("views", tvAnimation.getViews());
        db.update(TvAnimationTable, localContentValues, "id = ?", arrayOfString);
        
        return true;
    }

    public ArrayList<Animate> getTvAnimationAllList(SQLiteDatabase db) throws SQLException {
    	ArrayList<Animate> tvAnimation_lst = new ArrayList<Animate>();
    	try {
	        Cursor cursor = null;
	        cursor = db.rawQuery("SELECT id, name, season FROM " + TvAnimationTable + " WHERE is_show = 't';", null);
	        if (cursor != null) {
	        	while(cursor.moveToNext()) {
		            Animate tvAnimation = new Animate();
		            tvAnimation.setId(cursor.getInt(0));
		            tvAnimation.setName(cursor.getString(1));
		            tvAnimation.setSeason(cursor.getString(2));
		            tvAnimation_lst.add(tvAnimation);
	        	}
	            cursor.close();
	        }
	    } catch (Exception e) {
		     return tvAnimation_lst;
		}
        return tvAnimation_lst;
    }
    
    /*public ArrayList<Animate> getTvAnimationList(ArrayList<Integer> tvAnimationIds) throws SQLException {
    	final SQLiteDatabase db = getReadableDatabase();
        String idLst = "";
        for (int i = 0; i < tvAnimationIds.size(); i++)
            idLst = tvAnimationIds.get(i) + "," + idLst;
        idLst = idLst.substring(0, idLst.length() - 1);

        ArrayList<Animate> tvAnimation_lst = new ArrayList<Animate>();
        try {
	        Cursor cursor = null;
	        cursor = db.rawQuery("SELECT id, name, cover_poster, views FROM " + TvAnimationTable + " WHERE id in (" + idLst + ") AND is_show = 't';", null);
	        if (cursor != null) {
	        	while(cursor.moveToNext()) {
		            Animate tvAnimation = new Animate();
		            tvAnimation.setId(cursor.getInt(0));
		            tvAnimation.setName(cursor.getString(1));
		            tvAnimation.setCoverPosterUrl(cursor.getString(2));
		            tvAnimation.setViews(cursor.getInt(3));
		            tvAnimation_lst.add(tvAnimation);
	        	}
	            cursor.close();
	        }
	    } catch (Exception e) {
		     return tvAnimation_lst;
		}
        return tvAnimation_lst;
    }
    
    public ArrayList<Animate> getTvAnimationList(SQLiteDatabase db, String tvAnimationID) {
    	String[] likeTvAnimations = tvAnimationID.split(",");
        String tvAnimationIDs = "";
        for (int i = 0; i < likeTvAnimations.length; i++) {
            if (!likeTvAnimations[i].equals("")) {
            	tvAnimationIDs = tvAnimationIDs + likeTvAnimations[i];
                if (i < likeTvAnimations.length - 1)
                	tvAnimationIDs = tvAnimationIDs + ",";
            }
        }
        ArrayList<Animate> tvAnimation_lst = new ArrayList<Animate>();
        try {
	        Cursor cursor = null;
	    	cursor = db.rawQuery("SELECT id, name, cover_poster, views FROM " + TvAnimationTable + " WHERE id in (" + tvAnimationIDs + ") AND is_show = 't';", null);
	        if (cursor != null) {
	        	while(cursor.moveToNext()) {
		            Animate tvAnimation = new Animate();
		            tvAnimation.setId(cursor.getInt(0));
		            tvAnimation.setName(cursor.getString(1));
		            tvAnimation.setCoverPosterUrl(cursor.getString(2));
		            tvAnimation.setViews(cursor.getInt(3));
		            tvAnimation_lst.add(tvAnimation);
	        	}
	            cursor.close();
	        }
        } catch (Exception e) {
		     return tvAnimation_lst;
		}
        return tvAnimation_lst;
    }*/
    
    public ArrayList<Animate> getTvAnimationTypeList(SQLiteDatabase db, int type) throws SQLException {
    	ArrayList<Animate> tvAnimation_lst = new ArrayList<Animate>();
    	try {
	        Cursor cursor = null;
	        cursor = db.rawQuery("SELECT id, name, season, poster, views FROM " + TvAnimationTable + " WHERE type_id = " + type + " AND is_show = 't';", null);
	        if (cursor != null) {
	        	while(cursor.moveToNext()) {
		            Animate tvAnimation = new Animate();
		            tvAnimation.setId(cursor.getInt(0));
		            tvAnimation.setName(cursor.getString(1));
		            tvAnimation.setSeason(cursor.getString(2));
		            tvAnimation.setPosterUrl(cursor.getString(3));
		            tvAnimation.setViews(cursor.getInt(4));
		            tvAnimation_lst.add(tvAnimation);
	        	}
	            cursor.close();
	        }
	    } catch (Exception e) {
		     return tvAnimation_lst;
		}

        return tvAnimation_lst;
    }

}