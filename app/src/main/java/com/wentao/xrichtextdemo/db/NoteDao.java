package com.wentao.xrichtextdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.wentao.xrichtextdemo.MyApplication;
import com.wentao.xrichtextdemo.bean.Note;
import com.wentao.xrichtextdemo.util.CommonUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 作者：Sendtion on 2016/10/24 0024 15:53
 * 邮箱：sendtion@163.com
 * 博客：http://sendtion.cn
 * 描述：笔记处理
 */

public class NoteDao {
    private MyOpenHelper helper;

    public NoteDao(Context context) {
        helper = MyOpenHelper.getInstance(context);
    }

    /**
     * 查询所有笔记
     */
    public List<Note> queryNotesAll(int groupId) {
        SQLiteDatabase db = helper.getWritableDatabase();

        List<Note> noteList = new ArrayList<>();
        Note note ;
        String sql ;
        Cursor cursor = null;
        try {
            if (groupId > 0){
                sql = "select * from db_note where n_group_id =" + groupId +
                        "order by n_create_time desc";
            } else {
                sql = "select * from db_note " ;
            }
            cursor = db.rawQuery(sql, null);
            //cursor = db.query("note", null, null, null, null, null, "n_id desc");
            while (cursor.moveToNext()) {
                //循环获得展品信息
                note = new Note();
                note.setId(cursor.getInt(cursor.getColumnIndex("n_id")));
                note.setTitle(cursor.getString(cursor.getColumnIndex("n_title")));
                note.setContent(cursor.getString(cursor.getColumnIndex("n_content")));
                note.setGroupId(cursor.getInt(cursor.getColumnIndex("n_group_id")));
                note.setGroupName(cursor.getString(cursor.getColumnIndex("n_group_name")));
                note.setType(cursor.getInt(cursor.getColumnIndex("n_type")));
                note.setBgColor(cursor.getString(cursor.getColumnIndex("n_bg_color")));
                note.setIsEncrypt(cursor.getInt(cursor.getColumnIndex("n_encrypt")));
                note.setCreateTime(cursor.getString(cursor.getColumnIndex("n_create_time")));
                note.setUpdateTime(cursor.getString(cursor.getColumnIndex("n_update_time")));
                note.setObjectId(cursor.getString(cursor.getColumnIndex("n_object_id")));
                note.setUserId(cursor.getString(cursor.getColumnIndex("n_user_id")));
                noteList.add(note);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return noteList;
    }

    /**
     * 插入笔记
     */
    public long insertNote(Note note) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "insert into db_note(n_title,n_content,n_group_id,n_group_name," +
                "n_type,n_bg_color,n_encrypt,n_create_time,n_update_time , n_object_id , n_user_id , n_id ) " +
                "values(?,?,?,?,?,?,?,?,?,?,?,?)";

        long ret = 0;
        //sql = "insert into ex_user(eu_login_name,eu_create_time,eu_update_time) values(?,?,?)";
        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransaction();
        try {
            stat.bindString(1, note.getTitle());
            stat.bindString(2, note.getContent());
            stat.bindLong(3, note.getGroupId());
            stat.bindString(4, note.getGroupName());
            stat.bindLong(5, note.getType());
            stat.bindString(6, note.getBgColor());
            stat.bindLong(7, note.getIsEncrypt());
            stat.bindString(8, CommonUtil.date2string(new Date()));
            stat.bindString(9, CommonUtil.date2string(new Date()));
            stat.bindString(10 , "");
            stat.bindString(11 , MyApplication.phoneNumber);
            stat.bindLong(12, note.getId());
            ret = stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    /**
     * 更新笔记
     * @param note
     */
    public void updateNote(Note note) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("n_title", note.getTitle());
        values.put("n_content", note.getContent());
        values.put("n_group_id", note.getGroupId());
        values.put("n_group_name", note.getGroupName());
        values.put("n_type", note.getType());
        values.put("n_bg_color", note.getBgColor());
        values.put("n_encrypt", note.getIsEncrypt());
        values.put("n_update_time", CommonUtil.date2string(new Date()));
        values.put("n_object_id" , note.getObjectId());
        values.put("n_user_id" , note.getUserId());
        db.update("db_note", values, "n_id=?", new String[]{note.getId()+""});
        db.close();
    }

    /**
     * 删除笔记
     * 暂时还没有添加网络端功能
     */
    public int deleteNote(int noteId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int ret = 0;
        try {
            ret = db.delete("db_note", "n_id=?", new String[]{noteId + ""});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return ret;
    }

    /**
     * 批量删除笔记
     *
     * @param mNotes
     */
    public int deleteNote(List<Note> mNotes) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int ret = 0;
        try {
            if (mNotes != null && mNotes.size() > 0) {
                db.beginTransaction();//开始事务
                try {
                    for (Note note : mNotes) {
                        ret += db.delete("db_note", "n_id=?", new String[]{note.getId() + ""});
                    }
                    db.setTransactionSuccessful();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
        return ret;
    }
}
