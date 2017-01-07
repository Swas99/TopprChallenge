package com.archer.thought_works_got.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.archer.thought_works_got.data_model.network_models.BattleDO;
import com.archer.thought_works_got.data_model.sql_db_models.KingsDO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Swastik Sahu
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "got.db";
    private static final int DATABASE_VERSION = 1;

    //region BattleData table - keys
    public static final String TABLE_BATTLE_DATA = "battle_response";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_BATTLE_NUMBER = "battle_number"; // INT
    public static final String COLUMN_ATTACKER_KING = "attacker_king";
    public static final String COLUMN_DEFENDER_KING = "defender_king";
    public static final String COLUMN_ATTACKER_1 = "attacker_1";
    public static final String COLUMN_ATTACKER_2 = "attacker_2";
    public static final String COLUMN_ATTACKER_3 = "attacker_3";
    public static final String COLUMN_ATTACKER_4 = "attacker_4";
    public static final String COLUMN_DEFENDER_1 = "defender_1";
    public static final String COLUMN_DEFENDER_2 = "defender_2";
    public static final String COLUMN_DEFENDER_3 = "defender_3";
    public static final String COLUMN_DEFENDER_4 = "defender_4";
    public static final String COLUMN_ATTACKER_OUTCOME = "attacker_outcome";
    public static final String COLUMN_BATTLE_TYPE = "battle_type";
    public static final String COLUMN_MAJOR_DEATH = "major_death"; //INT
    public static final String COLUMN_MAJOR_CAPTURE = "major_capture"; //INT
    public static final String COLUMN_ATTACKER_SIZE = "attacker_size";
    public static final String COLUMN_DEFENDER_SIZE = "defender_size";
    public static final String COLUMN_ATTACKER_COMMANDER = "attacker_commander";
    public static final String COLUMN_DEFENDER_COMMANDER = "defender_commander";
    public static final String COLUMN_SUMMER = "summer";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_REGION = "region";
    public static final String COLUMN_NOTE = "note";
    //endregion

    //region KING_DATA table - keys
    public static final String TABLE_KING_DATA = "king_data";
    //    public static final String COLUMN_ID = "id";
//    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CURRENT_RANK = "c_rank";
    public static final String COLUMN_TOP_RANK = "t_rank";
    public static final String COLUMN_WORST_RANK = "w_rank";
    public static final String COLUMN_CURRENT_RATING = "c_rating";
    public static final String COLUMN_HIGHEST_RATING = "t_rating";
    public static final String COLUMN_LOWEST_RATING = "w_rating";
    public static final String COLUMN_TOTAL_BATTLES = "total_battles";
    public static final String COLUMN_BATTLES_WON = "w_battles";
    public static final String COLUMN_BATTLES_LOST = "l_battles";

    //endregion


    // Database creation sql statement
    //region create BattleData table
    private static final String CREATE_BATTLE_DATA_TABLE =
            "create table " + TABLE_BATTLE_DATA + "("
                    + COLUMN_ID + " integer primary key , "
                    + COLUMN_NAME + " text, "
                    + COLUMN_YEAR + " INTEGER , "
                    + COLUMN_BATTLE_NUMBER + " INTEGER unique, "
                    + COLUMN_ATTACKER_KING + " text , "
                    + COLUMN_DEFENDER_KING + " text , "
                    + COLUMN_ATTACKER_1 + " text , "
                    + COLUMN_ATTACKER_2 + " text , "
                    + COLUMN_ATTACKER_3 + " text , "
                    + COLUMN_ATTACKER_4 + " text , "
                    + COLUMN_DEFENDER_1 + " text , "
                    + COLUMN_DEFENDER_2 + " text , "
                    + COLUMN_DEFENDER_3 + " text , "
                    + COLUMN_DEFENDER_4 + " text , "
                    + COLUMN_ATTACKER_OUTCOME + " text , "
                    + COLUMN_BATTLE_TYPE + " text , "
                    + COLUMN_MAJOR_DEATH + " INTEGER , "
                    + COLUMN_MAJOR_CAPTURE + " INTEGER , "
                    + COLUMN_ATTACKER_SIZE + " text , "
                    + COLUMN_DEFENDER_SIZE + " text , "
                    + COLUMN_ATTACKER_COMMANDER + " text , "
                    + COLUMN_DEFENDER_COMMANDER + " text , "
                    + COLUMN_SUMMER + " text , "
                    + COLUMN_LOCATION + " text , "
                    + COLUMN_REGION + " text , "
                    + COLUMN_NOTE + " text"
                    + " );";
    //endregion

    //region create KingData table
    private static final String CREATE_KING_DATA_TABLE =
            "create table " + TABLE_KING_DATA + "("
                    + COLUMN_ID + " integer primary key , "
                    + COLUMN_NAME + " text unique, "
                    + COLUMN_CURRENT_RANK + " INTEGER , "
                    + COLUMN_TOP_RANK + " INTEGER , "
                    + COLUMN_WORST_RANK + " INTEGER , "
                    + COLUMN_CURRENT_RATING + " REAL , "
                    + COLUMN_HIGHEST_RATING + " REAL , "
                    + COLUMN_LOWEST_RATING + " REAL , "
                    + COLUMN_TOTAL_BATTLES + " INTEGER , "
                    + COLUMN_BATTLES_WON + " INTEGER , "
                    + COLUMN_BATTLES_LOST + " INTEGER "
                    + " );";
    //endregion



    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_KING_DATA_TABLE);
        db.execSQL(CREATE_BATTLE_DATA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KING_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATTLE_DATA);
        onCreate(db);
    }

    //region BattleData table operations

    public static void insertRowsToBattleDataTable(Context context, List<BattleDO> battles)
    {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.beginTransaction();
        database.execSQL("delete from "+ TABLE_BATTLE_DATA);
        try {
            ContentValues values = new ContentValues();
            for (BattleDO item : battles) {
                values.put(MySQLiteHelper.COLUMN_NAME, item.getName().trim());
                values.put(MySQLiteHelper.COLUMN_YEAR, item.getYear());
                values.put(MySQLiteHelper.COLUMN_BATTLE_NUMBER, item.getBattle_number());
                values.put(MySQLiteHelper.COLUMN_ATTACKER_KING, item.getAttacker_king().trim());
                values.put(MySQLiteHelper.COLUMN_DEFENDER_KING, item.getDefender_king().trim());
                values.put(MySQLiteHelper.COLUMN_ATTACKER_1, item.getAttacker_1());
                values.put(MySQLiteHelper.COLUMN_ATTACKER_2, item.getAttacker_2());
                values.put(MySQLiteHelper.COLUMN_ATTACKER_3, item.getAttacker_3());
                values.put(MySQLiteHelper.COLUMN_ATTACKER_4, item.getAttacker_4());
                values.put(MySQLiteHelper.COLUMN_DEFENDER_1, item.getDefender_1());
                values.put(MySQLiteHelper.COLUMN_DEFENDER_2, item.getDefender_2());
                values.put(MySQLiteHelper.COLUMN_DEFENDER_3, item.getDefender_3());
                values.put(MySQLiteHelper.COLUMN_DEFENDER_4, item.getDefender_4());
                values.put(MySQLiteHelper.COLUMN_ATTACKER_OUTCOME, item.getAttacker_outcome());
                values.put(MySQLiteHelper.COLUMN_BATTLE_TYPE, item.getBattle_type());
                values.put(MySQLiteHelper.COLUMN_MAJOR_DEATH, item.getMajor_death());
                values.put(MySQLiteHelper.COLUMN_MAJOR_CAPTURE, item.getMajor_capture());
                values.put(MySQLiteHelper.COLUMN_ATTACKER_SIZE, item.getAttacker_size());
                values.put(MySQLiteHelper.COLUMN_DEFENDER_SIZE, item.getDefender_size());
                values.put(MySQLiteHelper.COLUMN_ATTACKER_COMMANDER, item.getAttacker_commander());
                values.put(MySQLiteHelper.COLUMN_DEFENDER_COMMANDER, item.getDefender_commander());
                values.put(MySQLiteHelper.COLUMN_SUMMER, item.getSummer());
                values.put(MySQLiteHelper.COLUMN_LOCATION, item.getLocation());
                values.put(MySQLiteHelper.COLUMN_REGION, item.getRegion());
                values.put(MySQLiteHelper.COLUMN_NOTE, item.getNote());
                database.insertWithOnConflict(MySQLiteHelper.TABLE_BATTLE_DATA, null, values,SQLiteDatabase.CONFLICT_REPLACE);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    public static int getCountOfBattlesWonAttacking(Context context,String king)
    {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        try {
            return (int) DatabaseUtils.queryNumEntries(database, TABLE_BATTLE_DATA,
                    COLUMN_ATTACKER_KING + "=? and " + COLUMN_ATTACKER_OUTCOME + "=?",
                    new String[]{king,NetworkUtil.OUTCOME_WIN});

        }catch (Exception e){}
        finally {
            database.close();
        }
        return 0;
    }

    public static int getCountOfBattlesWonDefending(Context context,String king)
    {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        try {

            return (int) DatabaseUtils.queryNumEntries(database, TABLE_BATTLE_DATA,
                    COLUMN_DEFENDER_KING + "=? and " + COLUMN_ATTACKER_OUTCOME + "=?",
                    new String[]{king,NetworkUtil.OUTCOME_LOSS});

        }catch (Exception e){}
        finally {
            database.close();
        }
        return 0;
    }


    public static int getWinCount(Context context, String king, String battle_type)
    {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        try {
            return
                    (int)DatabaseUtils.queryNumEntries(database, TABLE_BATTLE_DATA,
                            COLUMN_DEFENDER_KING + "=? and " +
                                    COLUMN_ATTACKER_OUTCOME + "=? and " +
                                    COLUMN_BATTLE_TYPE + "=?",
                            new String[]{king,NetworkUtil.OUTCOME_LOSS, battle_type})
                            +
                            (int)DatabaseUtils.queryNumEntries(database, TABLE_BATTLE_DATA,
                                    COLUMN_ATTACKER_KING + "=? and " +
                                            COLUMN_ATTACKER_OUTCOME + "=? and " +
                                            COLUMN_BATTLE_TYPE + "=?",
                                    new String[]{king,NetworkUtil.OUTCOME_WIN, battle_type});
        }catch (Exception e){}
        finally {
            database.close();
        }
        return -1;
    }
    public static int getTotalBattlesForGivenType(Context context, String king, String battle_type)
    {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        try {
            return
                    (int)DatabaseUtils.queryNumEntries(database, TABLE_BATTLE_DATA,
                            "(" + COLUMN_DEFENDER_KING + "=? or " +
                                    COLUMN_ATTACKER_KING + "=?) and " +
                                    COLUMN_BATTLE_TYPE + "=?",
                            new String[]{king,king, battle_type});
        }catch (Exception e){}
        finally {
            database.close();
        }
        return -1;
    }


    public static List<String> getBattleTypes(Context context)
    {
        List<String> result = new ArrayList<>();
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        try {
            String rawQuery = "select distinct " + COLUMN_BATTLE_TYPE+
                    "  from " + TABLE_BATTLE_DATA;
            Cursor c = database.rawQuery(rawQuery,null);
            while (c.moveToNext())
                result.add(c.getString(c.getColumnIndex(COLUMN_BATTLE_TYPE)));
            c.close();
        }catch (Exception e){}
        finally {
            database.close();
        }
        return result;
    }

    //endregion


    //region KingData table operations

    public static void initKingDataTable(Context context, List<String> kings)
    {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (String item : kings) {
                values.put(MySQLiteHelper.COLUMN_NAME, item.trim());
                values.put(MySQLiteHelper.COLUMN_CURRENT_RANK, 0);
                values.put(MySQLiteHelper.COLUMN_TOP_RANK, 0);
                values.put(MySQLiteHelper.COLUMN_WORST_RANK, 0);
                values.put(MySQLiteHelper.COLUMN_CURRENT_RATING, 400f);
                values.put(MySQLiteHelper.COLUMN_HIGHEST_RATING, 400f);
                values.put(MySQLiteHelper.COLUMN_LOWEST_RATING, 400f);
                values.put(MySQLiteHelper.COLUMN_TOTAL_BATTLES, 0);
                values.put(MySQLiteHelper.COLUMN_BATTLES_WON, 0);
                values.put(MySQLiteHelper.COLUMN_BATTLES_LOST, 0);
                database.insertWithOnConflict(MySQLiteHelper.TABLE_KING_DATA, null, values,SQLiteDatabase.CONFLICT_REPLACE);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            database.close();
        }
    }

    public static void initKingDataTable(Context context,
                                         Map<String, Double> rating,
                                         Map<String, Double> lowest_rating,
                                         Map<String, Double> highest_rating,
                                         Map<String, Integer> best_rank,
                                         Map<String, Integer> worst_rank,
                                         Map<String, Integer> battle_won_count,
                                         Map<String, Integer> battle_loss_count,
                                         Map<String, Integer> battle_total_count)
    {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.execSQL("delete from "+ TABLE_KING_DATA);
//        database.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Map.Entry<String, Double> entry : rating.entrySet())
            {
                String king = entry.getKey().trim();
                values.put(MySQLiteHelper.COLUMN_NAME, king);
                values.put(MySQLiteHelper.COLUMN_CURRENT_RANK, GOT_Util.getCurrentRank(rating.get(king),king,rating));
                values.put(MySQLiteHelper.COLUMN_TOP_RANK, best_rank.get(king));
                values.put(MySQLiteHelper.COLUMN_WORST_RANK, worst_rank.get(king));
                values.put(MySQLiteHelper.COLUMN_CURRENT_RATING, rating.get(king));
                values.put(MySQLiteHelper.COLUMN_HIGHEST_RATING, highest_rating.get(king));
                values.put(MySQLiteHelper.COLUMN_LOWEST_RATING, lowest_rating.get(king));
                values.put(MySQLiteHelper.COLUMN_TOTAL_BATTLES, battle_total_count.get(king));
                values.put(MySQLiteHelper.COLUMN_BATTLES_WON, battle_won_count.get(king));
                values.put(MySQLiteHelper.COLUMN_BATTLES_LOST, battle_loss_count.get(king));
                database.insertWithOnConflict(MySQLiteHelper.TABLE_KING_DATA, null, values,SQLiteDatabase.CONFLICT_REPLACE);
            }
//            database.setTransactionSuccessful();
        } finally {
//            database.endTransaction();
            database.close();
        }
    }

    public static List<KingsDO> getKingData(Context context)
    {
        List<KingsDO> result = new ArrayList<>();
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        try {
            String rawQuery = "select * " + "  from " + TABLE_KING_DATA +
                    " ORDER BY " + COLUMN_CURRENT_RANK +" ASC";
            Cursor c = database.rawQuery(rawQuery,null);
            KingsDO objKingsDO;
            while (c.moveToNext())
            {
                objKingsDO = new KingsDO();
                objKingsDO.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
                objKingsDO.setTotalBattles(c.getString(c.getColumnIndex(COLUMN_TOTAL_BATTLES)));
                objKingsDO.setBattlesLost(c.getString(c.getColumnIndex(COLUMN_BATTLES_LOST)));
                objKingsDO.setBattlesWon(c.getString(c.getColumnIndex(COLUMN_BATTLES_WON)));
                objKingsDO.setCurrentRank(c.getString(c.getColumnIndex(COLUMN_CURRENT_RANK)));
                objKingsDO.setTopRank(c.getString(c.getColumnIndex(COLUMN_TOP_RANK)));
                objKingsDO.setWorstRank(c.getString(c.getColumnIndex(COLUMN_WORST_RANK)));
                objKingsDO.setCurrentRating(c.getString(c.getColumnIndex(COLUMN_CURRENT_RATING)));
                objKingsDO.setHighestRating(c.getString(c.getColumnIndex(COLUMN_HIGHEST_RATING)));
                objKingsDO.setLowestRating(c.getString(c.getColumnIndex(COLUMN_LOWEST_RATING)));
                result.add(objKingsDO);
            }
            c.close();
        }catch (Exception e){}
        finally {
            database.close();
        }
        return result;
    }
    public static KingsDO getKingData(Context context, String name)
    {
        KingsDO objKingsDO = null;
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        try {
            Cursor c = database.query(TABLE_KING_DATA, new String[]{},
                    COLUMN_NAME+" = ?",
                    new String[]{name},
                    null,
                    null,
                    null
            );

            while (c.moveToNext())
            {
                objKingsDO = new KingsDO();
                objKingsDO.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
                objKingsDO.setTotalBattles(c.getString(c.getColumnIndex(COLUMN_TOTAL_BATTLES)));
                objKingsDO.setBattlesLost(c.getString(c.getColumnIndex(COLUMN_BATTLES_LOST)));
                objKingsDO.setBattlesWon(c.getString(c.getColumnIndex(COLUMN_BATTLES_WON)));
                objKingsDO.setCurrentRank(c.getString(c.getColumnIndex(COLUMN_CURRENT_RANK)));
                objKingsDO.setTopRank(c.getString(c.getColumnIndex(COLUMN_TOP_RANK)));
                objKingsDO.setWorstRank(c.getString(c.getColumnIndex(COLUMN_WORST_RANK)));
                objKingsDO.setCurrentRating(c.getString(c.getColumnIndex(COLUMN_CURRENT_RATING)));
                objKingsDO.setHighestRating(c.getString(c.getColumnIndex(COLUMN_HIGHEST_RATING)));
                objKingsDO.setLowestRating(c.getString(c.getColumnIndex(COLUMN_LOWEST_RATING)));
            }
            c.close();
        }catch (Exception e){}
        finally {
            database.close();
        }
        return objKingsDO;
    }

    //endregion


}