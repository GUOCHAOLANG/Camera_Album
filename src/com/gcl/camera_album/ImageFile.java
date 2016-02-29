package com.gcl.camera_album;

import com.example.camera_album.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

/**
 * �������Ҫ������������ʾ����ͼƬ���ļ���
 */
public class ImageFile extends Activity {

	private FolderAdapter folderAdapter;
	private Button bt_cancel;
	private Context mContext;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.g_plugin_camera_image_file);
		PublicWay.activityList.add(this);
		mContext = this;
		bt_cancel = (Button) findViewById(R.id.cancel);
		bt_cancel.setOnClickListener(new CancelListener());
		GridView gridView = (GridView) findViewById(R.id.fileGridView);
		folderAdapter = new FolderAdapter(this);
		gridView.setAdapter(folderAdapter);
	}
	// ȡ����ť�ļ���
	private class CancelListener implements OnClickListener {
		public void onClick(View v) {
			//���ѡ���ͼƬ
			Bimp.tempSelectBitmap.clear();
			Intent intent = new Intent();
			intent.setClass(mContext, AlbumActivity.class);
			startActivity(intent);
			finish();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			/*Intent intent = new Intent();
			intent.setClass(mContext, MainActivity.class);
			startActivity(intent);*/
			finish();
		}
		
		return true;
	}

}
