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
 * 这个是显示一个文件夹里面的所有图片时的界面
 */
public class ShowAllPhoto extends Activity {
	private GridView gridView;
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	// 完成按钮
	private Button okButton;
	// 预览按钮
	private Button preview;
	// 返回按钮
	private Button photo_files;
	// 取消按钮
	private Button cancel;
	// 标题
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

	// 返回相册按钮监听
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
	// 取消按钮的监听
	private class CancelListener implements OnClickListener {
		public void onClick(View v) {
			//清空选择的图片
			Log.i("--guo--", "取消按钮的监听");
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
							Toast.makeText(ShowAllPhoto.this, "超出可选图片张数", 200).show();
							return;
						}

						if (isChecked) {
							button.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size()
									+ "/"+PublicWay.num+")");
						} else {
							button.setVisibility(View.GONE);
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
						}
						isShowOkBt();
					}
				});
		// 完成按钮的监听
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
					//Toast.makeText(getApplicationContext(), "选择了"+itt, Toast.LENGTH_LONG).show();
				}
				/*My_data_application aa = (My_data_application) getApplicationContext();
				if(aa.from_Act == 1){
					//上传景点图片
					UpPics(aa.ad_id_temp,pic_list);
					//上传完，跳回打卡界面
					Intent intent = new Intent();
					intent.putExtra("uppic", "1");
					intent.putExtra("spots_id", aa.ad_id_temp);
					setResult(1, intent);
				}else{
					Intent intent = new Intent(ShowAllPhoto.this,AddNewSpotsSubmitActivity.class);
					//地点名
					intent.putExtra("spots_name", aa.ad_name_temp);
					//坐标
					intent.putExtra("lat", aa.ad_lat_temp);
					intent.putExtra("lon", aa.ad_lon_temp);
					//传递ArrayList照片数组
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
			okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			//preview.setPressed(true);
			okButton.setPressed(true);
			//preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
			//preview.setTextColor(Color.WHITE);
		} else {
			okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
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

	// 上传景点图片函数
	/*public void UpPics(String spots_id, ArrayList<String> up_pics_url) {
		// TODO Auto-generated method stub
		// 上传用的地址 api4
		String spotsPhotoAdd_url = null;
		My_data_application da = (My_data_application) getApplicationContext();
		spotsPhotoAdd_url = da.getBasic_url("spotsPhotoAdd");
		spotsPhotoAdd_url = spotsPhotoAdd_url + "&id=" + spots_id;
		// 判断有多张还是一张图片上传
		if (up_pics_url.size() >= 1) {
			Iterator<String> ita = up_pics_url.iterator();
			int num = 0;
			while (ita.hasNext()) { // 一张一张上传
				String picPath = ita.next();
				// spotsPhotoAdd_url = spotsPhotoAdd_url+"&imgs="+picPath;
				spotsPhotoAdd_url = spotsPhotoAdd_url + "&imgs=" + picPath;
				UploadFileTask uploadFileTask = new UploadFileTask(this,
						spotsPhotoAdd_url);
				uploadFileTask.execute(picPath);
			}

			// 输出结果上传
			
			 * if (da.isUp) { Toast.makeText(getApplicationContext(),
			 * "上传成功!",Toast.LENGTH_LONG).show();
			 * 
			 * } else { Toast.makeText(getApplicationContext(),
			 * "上传失败!",Toast.LENGTH_LONG).show(); }
			 
		} else {
			Log.e("--guo--", "没图片");
		}
	}*/
}
