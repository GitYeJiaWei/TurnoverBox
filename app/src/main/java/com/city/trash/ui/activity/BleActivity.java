package com.city.trash.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.city.trash.R;
import com.qs.helper.printer.Device;
import com.qs.helper.printer.PrintService;
import com.qs.helper.printer.PrinterClass;
import com.qs.helper.printer.bt.BtService;

public class BleActivity extends ListActivity {

    protected static final String TAG = "BleActivity";
    public static boolean checkState = true;
    private Thread tv_update;
    TextView textView_state;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    Handler mhandler = null;
    Handler handler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        setTitle("蓝牙设置");

        textView_state = (TextView) findViewById(R.id.textView_state);
        setListAdapter(new SimpleAdapter(this, getData("simple-list-item-2"),
                android.R.layout.simple_list_item_2, new String[]{"title",
                "description"}, new int[]{android.R.id.text1,
                android.R.id.text2}));

        mhandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        Log.i(TAG, "readMessage=" + readMessage);
                        Log.i(TAG, "readBuf:" + readBuf[0]);
                        if (readBuf[0] == 0x13) {
                            PrintService.isFUll = true;
                            ShowMsg(getResources().getString(R.string.str_printer_state) + ":" + getResources().getString(R.string.str_printer_bufferfull));
                        } else if (readBuf[0] == 0x11) {
                            PrintService.isFUll = false;
                            ShowMsg(getResources().getString(R.string.str_printer_state) + ":" + getResources().getString(R.string.str_printer_buffernull));
                        } else if (readBuf[0] == 0x08) {
                            ShowMsg(getResources().getString(R.string.str_printer_state) + ":" + getResources().getString(R.string.str_printer_nopaper));
                        } else if (readBuf[0] == 0x01) {
                            //ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
                        } else if (readBuf[0] == 0x04) {
                            ShowMsg(getResources().getString(R.string.str_printer_state) + ":" + getResources().getString(R.string.str_printer_hightemperature));
                        } else if (readBuf[0] == 0x02) {
                            ShowMsg(getResources().getString(R.string.str_printer_state) + ":" + getResources().getString(R.string.str_printer_lowpower));
                        } else {
                            if (readMessage.contains("800"))// 80mm paper
                            {
                                PrintService.imageWidth = 72;
                                Toast.makeText(getApplicationContext(), "80mm",
                                        Toast.LENGTH_SHORT).show();
                            } else if (readMessage.contains("580"))// 58mm paper
                            {
                                PrintService.imageWidth = 48;
                                Toast.makeText(getApplicationContext(), "58mm",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case PrinterClass.STATE_CONNECTED:
                                break;
                            case PrinterClass.STATE_CONNECTING:
                                Toast.makeText(getApplicationContext(),
                                        "正在连接...", Toast.LENGTH_SHORT).show();
                                break;
                            case PrinterClass.STATE_LISTEN:
                            case PrinterClass.STATE_NONE:
                                break;
                            case PrinterClass.SUCCESS_CONNECT:
                                ////PrintService.pl.write(new byte[] { 0x1b, 0x2b });
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                PrintService.pl.write(new byte[]{0x1d, 0x67, 0x33});
                                Toast.makeText(getApplicationContext(),
                                        "连接成功", Toast.LENGTH_SHORT).show();
                                break;
                            case PrinterClass.FAILED_CONNECT:
                                Toast.makeText(getApplicationContext(),
                                        "连接失败", Toast.LENGTH_SHORT).show();

                                break;
                            case PrinterClass.LOSE_CONNECT:
                                Toast.makeText(getApplicationContext(), "失去连接",
                                        Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MESSAGE_WRITE:

                        break;
                }
                super.handleMessage(msg);
            }
        };

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        break;
                    case 1:
                        Device d = (Device) msg.obj;
                        if (d != null) {
                            if (PrintSettingActivity.deviceList == null) {
                                PrintSettingActivity.deviceList = new ArrayList<Device>();
                            }
                            if (!checkData(PrintSettingActivity.deviceList, d)) {
                                PrintSettingActivity.deviceList.add(d);
                            }
                        }
                        break;
                    case 2:
                        break;
                }
            }
        };

        tv_update = new Thread() {
            public void run() {
                while (true) {
                    if (checkState) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        textView_state.post(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                if (PrintService.pl != null) {
                                    if (PrintService.pl.getState() == PrinterClass.STATE_CONNECTED) {
                                        textView_state
                                                .setText(BleActivity.this
                                                        .getResources()
                                                        .getString(
                                                                R.string.str_connected));
                                    } else if (PrintService.pl.getState() == PrinterClass.STATE_CONNECTING) {
                                        textView_state
                                                .setText(BleActivity.this
                                                        .getResources()
                                                        .getString(
                                                                R.string.str_connecting));
                                    } else if (PrintService.pl.getState() == PrinterClass.LOSE_CONNECT
                                            || PrintService.pl.getState() == PrinterClass.FAILED_CONNECT) {
                                        checkState = false;
                                        textView_state
                                                .setText(BleActivity.this
                                                        .getResources()
                                                        .getString(
                                                                R.string.str_disconnected));
                                        Intent intent = new Intent();
                                        intent.setClass(BleActivity.this,
                                                PrintSettingActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);
                                    } else {
                                        textView_state
                                                .setText(BleActivity.this
                                                        .getResources()
                                                        .getString(
                                                                R.string.str_disconnected));
                                    }
                                }
                            }
                        });
                    }
                }
            }
        };
        tv_update.start();


        PrintService.pl = new BtService(this, mhandler, handler);

        if (PrintService.pl != null && PrintService.pl.getState() != PrinterClass.STATE_CONNECTED) {
            Intent intent = new Intent();
            intent = new Intent();
            intent.setClass(BleActivity.this, PrintSettingActivity.class);
            startActivityForResult(intent, 0);
        }

        /*Intent intent = new Intent();
        intent.putExtra("position", 0);
        intent.setClass(BleActivity.this, HelpActivity.class);
        startActivity(intent);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (PrintService.pl.getState() == PrinterClass.STATE_CONNECTED) {
                    BleActivity.this.finish();
                }
                break;
            default:
                break;
        }
    }


    public static StringBuffer bytesToString(byte[] bytes) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String s = Integer.toHexString(bytes[i] & 0xff);
            if (s.length() < 2)
                sBuffer.append('0');
            sBuffer.append(s + " ");
        }
        return sBuffer;
    }


    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onListItemClick(ListView listView, View v, int position,
                                   long id) {
        BleActivity.checkState = true;
        if (PrintService.pl != null && PrintService.pl.getState() != PrinterClass.STATE_CONNECTED) {
            Intent intent = new Intent();
            intent = new Intent();
            intent.setClass(BleActivity.this, PrintSettingActivity.class);
            startActivityForResult(intent, 0);
        }
        /*Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.setClass(BleActivity.this, HelpActivity.class);

        switch (position) {
            case 0:
                startActivity(intent);
                break;
            case 1:
                startActivity(intent);
                break;
            case 2:
                break;
        }*/
    }

    private List<Map<String, String>> getData(String title) {
        List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

        Map<String, String> map = new HashMap<String, String>();
        map.put("title", getResources().getString(R.string.mode_bt));
        map.put("description", "");
        listData.add(map);

        return listData;
    }

    private boolean checkData(List<Device> list, Device d) {
        for (Device device : list) {
            if (device.deviceAddress.equals(d.deviceAddress)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        checkState = true;
        super.onRestart();
    }

    private void ShowMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg,
                Toast.LENGTH_SHORT).show();
    }


}
