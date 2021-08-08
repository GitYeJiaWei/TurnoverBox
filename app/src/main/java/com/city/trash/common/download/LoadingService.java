package com.city.trash.common.download;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import com.city.trash.R;
import com.city.trash.common.util.ACache;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;

/**
 * IntentService是继承并处理异步请求的一个类，
 * 在IntentService内有一个工作线程来处理耗时操作，
 * 启动IntentService的方式和启动传统的Service一样，
 * 同时，当任务执行完后，IntentService会自动停止，而不需要我们手动去控制或stopSelf()。
 * 另外，可以启动IntentService多次，
 * 而每一个耗时操作会以工作队列的方式在IntentService的onHandleIntent回调方法中执行，
 * 并且，每次只会执行一个工作线程，执行完第一个再执行第二个，以此类推
 */

public class LoadingService extends IntentService {
    NotificationManager nm;
    private String url, path;
    private int cannId = 100;
    private String TAG = "ttag";

    public LoadingService(String name) {
        super(name);
    }

    public LoadingService() {
        super("MyService");
    }


    public static void startUploadImg(Context context) {
        Intent intent = new Intent(context, LoadingService.class);
        context.startService(intent);
    }


    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            Log.d(TAG, "onHandleIntent: ");
            updateApk();
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }


    private void updateApk() {
        url = ACache.get(this).getAsString("url");
        path = ACache.get(this).getAsString("path");
        Log.d(TAG, "path:" + path + "url:" + url);


        //下载地址
        RequestParams requestParams = new RequestParams(url);
        // 文件下载后的保存路径及文件名
        requestParams.setSaveFilePath(path);
        // 下载完成后自动为文件命名
        requestParams.setAutoRename(false);
        //下载请求
        x.http().get(requestParams, new Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File result) {
                //倒数第二调用
                Log.d(TAG, "---------------下载完成---------------------------");
                nm.cancel(cannId);
                installApkFile();//下载成功 检查权限打开安装界面
                stopSelf();//结束服务
                sendBroadcast(new Intent().setAction("android.intent.action.loading_over"));//发送下载结束的广播
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "---------------下载失败---------------------------");
                Log.d(TAG, "onError: "+ex.toString());
                sendBroadcast(new Intent().setAction("android.intent.action.loading_over"));//发送下载结束的广播
                nm.cancel(cannId);
                stopSelf();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "---------------取消下载---------------------------");
            }

            @Override
            public void onFinished() {
                //最后调用
                Log.d(TAG, "---------------下载结束---------------------------");
            }

            @Override
            public void onWaiting() {
                // 最开始调用
                Log.d(TAG, "---------------等待下载---------------------------");
            }

            @Override
            public void onStarted() {
                //第二调用
                Log.d(TAG, "---------------开始下载---------------------------");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                // 当前的下载进度和文件总大小
                // 下载的时候不断回调的方法
                //参数：总大小，已经下载的大小，是否正在下载
                Log.d(TAG, "***" + total + "********" + current + "****************" + isDownloading + "**********");
                //百分比为整数
                Log.d(TAG, "下载进度为：" + (int) (((float) current / total) * 100) + "%");

                sendBroadcast(new Intent().setAction("android.intent.action.loading"));//发送正在下载的广播
                createNotification(total, current);

            }
        });
    }

    private void createNotification(final long total, final long current) {
        String id = "my_channel_01";
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, this.getPackageName(), NotificationManager.IMPORTANCE_LOW);
            Log.i(TAG, mChannel.toString());
            nm.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this, id)
                    .setChannelId(id)
                    .setContentTitle("下载更新")
                    .setContentText("下载中")
                    .setProgress((int) total, (int) current, false)
                    .setOngoing(true)
                    .setSmallIcon(R.mipmap.icon_round).build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id)
                    .setProgress((int) total, (int) current, false)
                    .setContentTitle("下载更新")            //标题 （现在没用）
                    .setContentText("下载中")               //内容（现在没用）
                    .setSmallIcon(R.mipmap.icon_round)            //必须要设置这个属性，否则不显示
                    .setChannelId(id)                       //无效
                    .setOngoing(true);                      //设置左右滑动不能删除
            notification = builder.build();
        }
        nm.notify(cannId, notification);//发送通知
    }

    /**
     * 安装下载的新版本
     */
    public void installApkFile() {
        File apkFile = new File(path);
        if (apkFile != null && apkFile.exists()) {
            try {
                chmod("777", path);
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.addCategory(Intent.CATEGORY_DEFAULT);
                Uri uri;
                //判读版本是否在7.0以上
                if (Build.VERSION.SDK_INT >= 24) {
                    uri = FileProvider.getUriForFile(this, this.getPackageName() + ".fileprovider", apkFile);
                    installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Log.d(TAG, "7.0以上，正在安装apk..." + "uri:" + uri);
                } else {
                    Log.d(TAG, "正在安装apk...");
                    uri = Uri.fromFile(apkFile);
                }
                installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                this.startActivity(installIntent);
            } catch (Exception e) {
                Log.d(TAG, "Exception" + e.getMessage());
            }
        }
    }

    /**
     *     * 获取权限
     *     *
     *     * @param permission
     *     *            权限
     *     * @param path
     *     *            路径
     *     
     */
    public void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
