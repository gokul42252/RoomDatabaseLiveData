package com.cretlabs.roomdatabase.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.cretlabs.roomdatabase.database.RoomDatabase;
import com.cretlabs.roomdatabase.entities.School;

import java.util.List;
import java.util.Locale;

/**
 * Created by Gokul on 2/24/2018.
 */

/**
 * View model class
 */
public class SchoolViewModel extends AndroidViewModel {
    //live school data
    public final LiveData<List<School>> schoolLiveData;
    private RoomDatabase mDatabase;

    public SchoolViewModel(@NonNull Application application) {
        super(application);
        mDatabase = RoomDatabase.getDatabase(this.getApplication());
        this.schoolLiveData = mDatabase.schoolDao().getAllSchoolsLive();
    }

    public LiveData<List<School>> getSchoolLiveData() {
        return schoolLiveData;
    }

    public void AddSchoolData(int count) {

        School school = new School(count, String.format(Locale.getDefault(),"School %d", count), String.format(Locale.getDefault(),"School Address %d", count), "2222222222");
        mDatabase.schoolDao().insertSchool(school);
    }
}
