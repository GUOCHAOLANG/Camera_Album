package com.gcl.camera_album;

import java.util.ArrayList;
import java.util.Iterator;

import com.example.camera_album.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * �������ʾһ���ļ������������ͼƬʱ�Ľ���
 */
public class ShowAllPhoto extends Activity {
	private GridView gridView;
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	// ��ɰ�ť
	private Button okButton;
	// Ԥ����ť
	private Button preview;
	// ���ذ�ť
	private Button photo_files;
	// ȡ����ť
	private Button cancel;
	// ����
	private TextView headTitle;
	private Intent intent;
	private Context mContext;
	
	public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.g_plugin_camera_showall_photo);
		PublicWay.activityList.add(this);
		mContext = this;
		
		photo_files = (Button) findViewById(R.id.showallphoto_back);
		cancel = (Button) findViewById(R.id.showallphoto_cancel);
		//preview = (Button) findViewById(R.id.showallphoto_preview);
		okButton = (Button) findViewById(R.id.showallphoto_ok_button);
		headTitle = (TextView) findViewById(R.id.showallphoto_headtitle);
		this.intent = getIntent();
		String folderName = intent.getStringExtra("folderName");
		if (folderName.length() > 8) {
			folderName = folderName.substring(0, 9) + "...";
		}
		headTitle.setText(folderName);
		cancel.setOnClickListener(new CancelListener());
		photo_files.setOnClickListener(new BackListener(intent));
		//preview.setOnClickListener(new PreviewListener());
		init();
		initListener();
		isShowOkBt();
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
		  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            // TODO Auto-generated method stub  
        	gridImageAdapter.notifyDataSetChanged();
        }  
    };  

	// ������ᰴť����
	private class BackListener implements OnClickListener {
		Intent intent;

		public BackListener(Intent intent) {
			this.intent = intent;
		}

		public void onClick(View v) {
			intent.setClass(ShowAllPhoto.this, ImageFile.class);
			startActivity(intent);
		}

	}
	// ȡ����ť�ļ���
	private class CancelListener implements OnClickListener {
		public void onClick(View v) {
			//���ѡ���ͼƬ
			Log.i("--guo--", "ȡ����ť�ļ���");
			Bimp.tempSelectBitmap.clear();
			/*intent.setClass(mContext, MainActivity.class);
			startActivity(intent);*/
			finish();
		}
	}

	private void init() {
		IntentFilter filter = new IntentFilter("data.broadcast.action");  
		registerReceiver(broadcastReceiver, filter);  
		//progressBar = (ProgressBar) findViewById(R.id.showallphoto_progressbar);
		//progressBar.setVisibility(View.GONE);
		gridView = (GridView) findViewById(R.id.showallphoto_myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this,dataList,
				Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		okButton = (Button) findViewById(R.id.showallphoto_ok_button);
	}

	private void initListener() {

		gridImageAdapter
				.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
					public void onItemClick(final ToggleButton toggleButton,
							int position, boolean isChecked,
							Button button) {
						if (Bimp.tempSelectBitmap.size() >= PublicWay.num&&isChecked) {
							button.setVisibility(View.GONE);
							toggleButton.setChecked(false);
							Toast.makeText(ShowAllPhoto.this, "������ѡͼƬ����", 200).show();
							return;
						}

						if (isChecked) {
							button.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							okButton.setText("���"+"(" + Bimp.tempSelectBitmap.size()
									+ "/"+PublicWay.num+")");
						} else {
							button.setVisibility(View.GONE);
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							okButton.setText("���"+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
						}
						isShowOkBt();
					}
				});
		// ��ɰ�ť�ļ���
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				okButton.setClickable(false);
//				if (PublicWay.photoService != null) {
//					PublicWay.selectedDataList.addAll(Bimp.tempSelectBitmap);
//					Bimp.tempSelectBitmap.clear();
//					PublicWay.photoService.onActivityResult(0, -2,
//							intent);
//				}
				/*intent.setClass(mContext, MainActivity.class);
				startActivity(intent);*/
				// Intent intent = new Intent();
				// Bundle bundle = new Bundle();
				// bundle.putStringArrayList("selectedDataList",
				// selectedDataList);
				// intent.putExtras(bundle);
				// intent.setClass(ShowAllPhoto.this, UploadPhoto.class);
				// startActivity(intent);
				ArrayList<ImageItem> tt = Bimp.tempSelectBitmap;
				ArrayList<String> pic_list = new ArrayList<String>();
				for(ImageItem item:tt){
					String itt = item.imagePath;
					Log.i("--guo--", "show all photo pic url="+itt);
					pic_list.add(itt);
					//Toast.makeText(getApplicationContext(), "ѡ����"+itt, Toast.LENGTH_LONG).show();
				}
				/*My_data_application aa = (My_data_application) getApplicationContext();
				if(aa.from_Act == 1){
					//�ϴ�����ͼƬ
					UpPics(aa.ad_id_temp,pic_list);
					//�ϴ��꣬���ش򿨽���
					Intent intent = new Intent();
					intent.putExtra("uppic", "1");
					intent.putExtra("spots_id", aa.ad_id_temp);
					setResult(1, intent);
				}else{
					Intent intent = new Intent(ShowAllPhoto.this,AddNewSpotsSubmitActivity.class);
					//�ص���
					intent.putExtra("spots_name", aa.ad_name_temp);
					//����
					intent.putExtra("lat", aa.ad_lat_temp);
					intent.putExtra("lon", aa.ad_lon_temp);
					//����ArrayList��Ƭ����
					intent.putStringArrayListExtra("check", (ArrayList<String>) pic_list);
					startActivity(intent);
				}*/
				unregisterReceiver(broadcastReceiver);
				finish();

			}
		});

	}

	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText("���"+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			//preview.setPressed(true);
			okButton.setPressed(true);
			//preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
			//preview.setTextColor(Color.WHITE);
		} else {
			okButton.setText("���"+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			//preview.setPressed(false);
			//preview.setClickable(false);
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
			//preview.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			intent.setClass(ShowAllPhoto.this, ImageFile.class);
			startActivity(intent);
		}
		return false;
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		isShowOkBt();
		super.onRestart();
	}

	// �ϴ�����ͼƬ����
	/*public void UpPics(String spots_id, ArrayList<String> up_pics_url) {
		// TODO Auto-generated method stub
		// �ϴ��õĵ�ַ api4
		String spotsPhotoAdd_url = null;
		My_data_application da = (My_data_application) getApplicationContext();
		spotsPhotoAdd_url = da.getBasic_url("spotsPhotoAdd");
		spotsPhotoAdd_url = spotsPhotoAdd_url + "&id=" + spots_id;
		// �ж��ж��Ż���һ��ͼƬ�ϴ�
		if (up_pics_url.size() >= 1) {
			Iterator<String> ita = up_pics_url.iterator();
			int num = 0;
			while (ita.hasNext()) { // һ��һ���ϴ�
				String picPath = ita.next();
				// spotsPhotoAdd_url = spotsPhotoAdd_url+"&imgs="+picPath;
				spotsPhotoAdd_url = spotsPhotoAdd_url + "&imgs=" + picPath;
				UploadFileTask uploadFileTask = new UploadFileTask(this,
						spotsPhotoAdd_url);
				uploadFileTask.execute(picPath);
			}

			// �������ϴ�
			
			 * if (da.isUp) { Toast.makeText(getApplicationContext(),
			 * "�ϴ��ɹ�!",Toast.LENGTH_LONG).show();
			 * 
			 * } else { Toast.makeText(getApplicationContext(),
			 * "�ϴ�ʧ��!",Toast.LENGTH_LONG).show(); }
			 
		} else {
			Log.e("--guo--", "ûͼƬ");
		}
	}*/
}
