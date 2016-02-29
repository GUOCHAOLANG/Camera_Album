package com.gcl.camera_album;

import java.util.ArrayList;
import java.util.List;

import com.example.camera_album.R;
import com.gcl.camera_album.BitmapCache.ImageCallback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * 这个是进入相册显示所有图片的界面
 */
public class AlbumActivity extends Activity {
	// 显示手机里的�?有图片的列表控件
	private GridView gridView;
	// 当手机里没有图片时，提示用户没有图片的控�?
	private TextView tv;
	// gridView的adapter
	private AlbumGridViewAdapter010 gridImageAdapter;
	// 提交完成按钮
	private Button okButton;
	// 相册按钮
	private Button photo_files;
	// 返回按钮
	private Button cancel;
	private Intent intent;
	private Context mContext;
	private ArrayList<ImageItem> dataList;
	private AlbumHelper helper;
	public static List<ImageBucket> contentList;
	public static Bitmap bitmap;
	
	//上个Activity
	//int from_activity = 2;	//2是添加景点，上传图片�?1是打卡成功，上传图片
	//传�?�数�?
	String ad_name;
	String ad_id;
	double ad_lat;
	double ad_lon;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.g_plugin_camera_album);
		
		/*// 接收传�?�数�?
		Bundle bundle = this.getIntent().getExtras();
		My_data_application zaa = (My_data_application) getApplicationContext();
		if(zaa.from_Act == 1){
			if (!bundle.isEmpty()) {
				ad_id = bundle.getString("spots_id");
				My_data_application da = (My_data_application) getApplicationContext();
				da.ad_id_temp = ad_id;
				//da.ad_lat_temp = ad_lat;
				//da.ad_lon_temp = ad_lon;
				//da.ad_name_temp = ad_name;
			} else {
				Toast.makeText(getApplicationContext(), "数据传�?�为�?", Toast.LENGTH_LONG).show();
			}
		}else{
			if (!bundle.isEmpty()) {
				ad_lat = bundle.getDouble("lat");
				ad_lon = bundle.getDouble("lon");
				ad_name = bundle.getString("spots_name");
				My_data_application da = (My_data_application) getApplicationContext();
				da.ad_lat_temp = ad_lat;
				da.ad_lon_temp = ad_lon;
				da.ad_name_temp = ad_name;
			} else {
				Toast.makeText(getApplicationContext(), "数据传�?�为�?", Toast.LENGTH_LONG).show();
			}
		}*/
		
		PublicWay.activityList.add(this);
		mContext = this;
		//注册�?个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消�?�中的图片仍处于选中状�??
		IntentFilter filter = new IntentFilter("data.broadcast.action");  
		registerReceiver(broadcastReceiver, filter);  
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.plugin_camera_no_pictures);
        init();
		initListener();
		//这个函数主要用来控制预览和完成按钮的状�??
		isShowOkBt();
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
		  
        @Override  
        public void onReceive(Context context, Intent intent) {  
        	//mContext.unregisterReceiver(this);
            // TODO Auto-generated method stub  
        	gridImageAdapter.notifyDataSetChanged();
        }  
    };  

    // 完成按钮的监�?
	private class AlbumSendListener implements OnClickListener {
		public void onClick(View v) {
			//Log.i("--guo--", "选择的结果了"+Bimp.tempSelectBitmap.size());
			//Toast.makeText(getApplicationContext(), "选择的结果了"+Bimp.tempSelectBitmap.size(), Toast.LENGTH_LONG).show();
			ArrayList<ImageItem> tt = Bimp.tempSelectBitmap;
			ArrayList<String> pic_list = new ArrayList<String>();
			for(ImageItem item:tt){
				String itt = item.imagePath;
				Log.i("--guo--", "AlbumActivity pic _url="+itt);
				pic_list.add(itt);
				//Toast.makeText(getApplicationContext(), "选择�?"+itt, Toast.LENGTH_LONG).show();
			}
			/*My_data_application da = (My_data_application) getApplicationContext();
			if(da.from_Act == 1){
				//上传景点图片
				UpPics(da.ad_id_temp,pic_list);
				//Log.i("--guo--", "打卡上传完成");
				//上传完，跳回打卡界面
				Intent intent = new Intent();
				intent.putExtra("uppic", "1");
				intent.putExtra("spots_id", da.ad_id_temp);
				//Log.i("--guo--", "sports_id="+da.ad_id_temp);
				setResult(1, intent);
			}else{
				Intent intent = new Intent(AlbumActivity.this,AddNewSpotsSubmitActivity.class);
				//地点�?
				intent.putExtra("spots_name", ad_name);
				//坐标
				intent.putExtra("lat", ad_lat);
				intent.putExtra("lon", ad_lon);
				//传�?�ArrayList照片数组
				intent.putStringArrayListExtra("check", (ArrayList<String>) pic_list);
				startActivity(intent);
			}*/
			
			
			//unregisterReceiver(broadcastReceiver);
			//finish();
		}

	}

	// 相册按钮监听
	private class BackListener implements OnClickListener {
		public void onClick(View v) {
			intent.setClass(AlbumActivity.this, ImageFile.class);
			startActivity(intent);
		}
	}

	// 返回按钮的监�?
	private class CancelListener implements OnClickListener {
		public void onClick(View v) {
			Bimp.tempSelectBitmap.clear();
			unregisterReceiver(broadcastReceiver);
			finish();
		}
	}

	// 初始化，给一些对象赋�?
	private void init() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		
		contentList = helper.getImagesBucketList(false);
		dataList = new ArrayList<ImageItem>();
		for(int i = 0; i<contentList.size(); i++){
			dataList.addAll( contentList.get(i).imageList );
		}
		//返回按钮
		cancel = (Button) findViewById(R.id.photo_back);
		cancel.setOnClickListener(new CancelListener());
		//�?有相�?
		photo_files = (Button) findViewById(R.id.photo_files);
		photo_files.setOnClickListener(new BackListener());
		//预览�?
		/*preview = (Button) findViewById(R.id.preview);
		preview.setOnClickListener(new PreviewListener());*/
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		gridView = (GridView) findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter010(this,dataList,
				Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		tv = (TextView) findViewById(R.id.myText);
		gridView.setEmptyView(tv);
		okButton = (Button) findViewById(R.id.ok_button);
		//okButton.setText(R.string.finish+"(" + Bimp.tempSelectBitmap.size()+ "/"+PublicWay.num+")");
		okButton.setText("预览 完成"+"(" + Bimp.tempSelectBitmap.size()+ "/"+")");
	}

	private void initListener() {

		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter010.OnItemClickListener() {

					public void onItemClick(final ToggleButton toggleButton,
							int position, boolean isChecked,Button chooseBt) {
						if(position == 0){
							//这个是照相机图片
						}else{
							if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
								toggleButton.setChecked(false);
								chooseBt.setVisibility(View.GONE);
								if (!removeOneData(dataList.get(position))) {
									//Toast.makeText(AlbumActivity.this, R.string.only_choose_num ,200).show();
									Toast.makeText(AlbumActivity.this, "超出可�?�图片张�?" ,200).show();
								}
								return;
							}
							if (isChecked) {
								chooseBt.setVisibility(View.VISIBLE);
								Bimp.tempSelectBitmap.add(dataList.get(position));
								okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size()
										+ "/"+PublicWay.num+")");
							} else {
								Bimp.tempSelectBitmap.remove(dataList.get(position));
								chooseBt.setVisibility(View.GONE);
								okButton.setText("完成"+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
							}
							isShowOkBt();
						}
					}
				});

		okButton.setOnClickListener(new AlbumSendListener());

	}

	private boolean removeOneData(ImageItem imageItem) {
			if (Bimp.tempSelectBitmap.contains(imageItem)) {
				Bimp.tempSelectBitmap.remove(imageItem);
				okButton.setText("完成"+"(" +Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
				return true;
			}
		return false;
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
			intent.setClass(AlbumActivity.this, ImageFile.class);
			startActivity(intent);
		}
		return false;
	}

	@Override
	protected void onRestart() {
		isShowOkBt();
		unregisterReceiver(broadcastReceiver);
		super.onRestart();
	}
	
	/*// 上传景点图片函数
	public void UpPics(String spots_id, ArrayList<String> up_pics_url) {
		// TODO Auto-generated method stub
		// 上传用的地址 api4
		String spotsPhotoAdd_url = null;
		My_data_application da = (My_data_application) getApplicationContext();
		spotsPhotoAdd_url = da.getBasic_url("spotsPhotoAdd");
		spotsPhotoAdd_url = spotsPhotoAdd_url + "&id=" + spots_id;
		Log.i("--guo--", "打卡-上传-上传图片=="+spotsPhotoAdd_url);
		// 判断有多张还是一张图片上�?
		if (up_pics_url.size() >= 1) {
			Iterator<String> ita = up_pics_url.iterator();
			int num = 0;
			while (ita.hasNext()) { // �?张一张上�?
				String picPath = ita.next();
				// spotsPhotoAdd_url = spotsPhotoAdd_url+"&imgs="+picPath;
				spotsPhotoAdd_url = spotsPhotoAdd_url + "&imgs=" + picPath;
				UploadFileTask uploadFileTask = new UploadFileTask(this,
						spotsPhotoAdd_url);
				uploadFileTask.execute(picPath);
			}

			// 输出结果上传
			if (da.isUp) {
				Toast.makeText(getApplicationContext(), "上传成功!",Toast.LENGTH_LONG).show();

			} else {
				Toast.makeText(getApplicationContext(), "上传失败!",Toast.LENGTH_LONG).show();
			}
		} else {
			Log.e("--guo--", "没图�?");
		}
	}*/
}
class AlbumGridViewAdapter010 extends BaseAdapter{
	final String TAG = getClass().getSimpleName();
	private Context mContext;
	private ArrayList<ImageItem> dataList;
	private ArrayList<ImageItem> selectedDataList;
	private DisplayMetrics dm;
	BitmapCache cache;
	public AlbumGridViewAdapter010(Context c, ArrayList<ImageItem> dataList,
			ArrayList<ImageItem> selectedDataList) {
		mContext = c;
		cache = new BitmapCache();
		this.dataList = dataList;
		this.selectedDataList = selectedDataList;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
	}

	public int getCount() {
		return dataList.size();
	}

	public Object getItem(int position) {
		return dataList.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				} else {
					Log.e(TAG, "callback, bmp not match");
				}
			} else {
				Log.e(TAG, "callback, bmp null");
			}
		}
	};
	
	/**
	 * 存放列表项控件句�?
	 */
	private class ViewHolder {
		public ImageView imageView;
		public ToggleButton toggleButton;
		public Button choosetoggle;
		public TextView textView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			//图片单项布局
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.g_plugin_camera_select_imageview, parent, false);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.image_view);
			viewHolder.toggleButton = (ToggleButton) convertView
					.findViewById(R.id.toggle_button);
			viewHolder.choosetoggle = (Button) convertView
					.findViewById(R.id.choosedbt);
//			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,dipToPx(65)); 
//			lp.setMargins(50, 0, 50,0); 
//			viewHolder.imageView.setLayoutParams(lp);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
			viewHolder.imageView.setImageResource(R.drawable.sphoto_c);
		} else {
			String path;
			if (dataList != null && dataList.size() >= position)
				path = dataList.get(position).imagePath;
			else
				path = "camera_default";
			if (path.contains("camera_default")) {
				viewHolder.imageView.setImageResource(R.drawable.ic_launcher);
			} else {
				// ImageManager2.from(mContext).displayImage(viewHolder.imageView,
				// path, Res.getDrawableID("plugin_camera_camera_default"), 100,
				// 100);
				final ImageItem item = dataList.get(position);
				viewHolder.imageView.setTag(item.imagePath);
				cache.displayBmp(viewHolder.imageView, item.thumbnailPath,
						item.imagePath, callback);
			}
		}
		/*String path;
		if (dataList != null && dataList.size() > position)
			path = dataList.get(position).imagePath;
		else
			path = "camera_default";
		if (path.contains("camera_default")) {
			viewHolder.imageView.setImageResource(R.drawable.plugin_camera_no_pictures);
		} else {
//			ImageManager2.from(mContext).displayImage(viewHolder.imageView,
//					path, Res.getDrawableID("plugin_camera_camera_default"), 100, 100);
			final ImageItem item = dataList.get(position);
			viewHolder.imageView.setTag(item.imagePath);
			cache.displayBmp(viewHolder.imageView, item.thumbnailPath, item.imagePath,
					callback);
		}*/
		viewHolder.toggleButton.setTag(position);
		viewHolder.choosetoggle.setTag(position);
		viewHolder.toggleButton.setOnClickListener(new ToggleClickListener(viewHolder.choosetoggle));
		if (selectedDataList.contains(dataList.get(position))) {
			viewHolder.toggleButton.setChecked(true);
			viewHolder.choosetoggle.setVisibility(View.VISIBLE);
		} else {
			viewHolder.toggleButton.setChecked(false);
			viewHolder.choosetoggle.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	public int dipToPx(int dip) {
		return (int) (dip * dm.density + 0.5f);
	}
	private class ToggleClickListener implements OnClickListener{
		Button chooseBt;
		public ToggleClickListener(Button choosebt){
			this.chooseBt = choosebt;
		}
		
		@Override
		public void onClick(View view) {
			if (view instanceof ToggleButton) {
				ToggleButton toggleButton = (ToggleButton) view;
				int position = (Integer) toggleButton.getTag();
				if (dataList != null && mOnItemClickListener != null
						&& position < dataList.size()) {
					mOnItemClickListener.onItemClick(toggleButton, position, toggleButton.isChecked(),chooseBt);
				}
			}
		}
	}
	

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	public interface OnItemClickListener {
		public void onItemClick(ToggleButton view, int position,
				boolean isChecked,Button chooseBt);
	}

}
