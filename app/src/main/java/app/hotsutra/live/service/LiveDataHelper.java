package app.hotsutra.live.service;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import app.hotsutra.live.models.DownloadInfo;

import java.util.List;

public class LiveDataHelper {
    private final MediatorLiveData<DownloadInfo> _downloadInfo = new MediatorLiveData<>();
    private final MediatorLiveData<DownloadInfo> _isCompleted = new MediatorLiveData<>();
    private final MediatorLiveData<List<DownloadInfo>> _list = new MediatorLiveData<>();

    private LiveDataHelper() {}

    private static LiveDataHelper liveDataHelper;

    synchronized public static LiveDataHelper getInstance() {
        if (liveDataHelper == null)
            liveDataHelper = new LiveDataHelper();
        return liveDataHelper;
    }

    void updatePercentage(DownloadInfo info) {
        _downloadInfo.postValue(info);
    }

    public LiveData<DownloadInfo> observePercentage() {
        return _downloadInfo;
    }

    void completeStatus(DownloadInfo info) {
        _isCompleted.postValue(info);
    }

    public LiveData<DownloadInfo> observeIsCompleted() {
        return _isCompleted;
    }

    void downloadingList(List<DownloadInfo> list){
        _list.postValue(list);
    }

    public LiveData<List<DownloadInfo>> observeDownloadList(){
        return _list;
    }
}
