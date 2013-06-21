package com.wewin.flowmobilesys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.wewin.flowmobilesys.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ���˵�Activity
 * 
 * @author HCOU
 * @time 2013.05.28 17:37:00
 */
public class MainActivity extends Activity {
	private GridView mGridView;// �˵�grid
	private ButtomMenu buttomMenu;// �ײ��˵�
	private TextView title;// ������
	private int[] imageRes = { R.drawable.tasklist, R.drawable.watch,
			R.drawable.settings, R.drawable.datachart };
	private String[] itemName = { "�ҵ�����", "�ҵĹ�ע", "�ɼ�����", "�����ܸ�" };
	private long exitTime = 0;// �˳�����ʱ
	private Button loginother_btn, exit_btn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();// ��ʼ������
	}

	private void initView() {
		mGridView = (GridView) findViewById(R.id.MenuGridView);
		title = (TextView) findViewById(R.id.main_tilte);
		title.setText("�����ƶ����粿��Ŀ����ƽ̨");

		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		int length = imageRes.length;
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImageView", imageRes[i]);
			map.put("ItemTextView", itemName[i]);
			data.add(map);
		}
		SimpleAdapter mSimpleAdapter = new SimpleAdapter(MainActivity.this,
				data, R.layout.menuitem, new String[] { "ItemImageView",
						"ItemTextView" }, new int[] { R.id.ItemImageView,
						R.id.ItemTextView });
		mGridView.setAdapter(mSimpleAdapter);
		mGridView.setOnItemClickListener(new GridViewItemOnClick());

		buttomMenu = new ButtomMenu();// ������ز˵�

		loginother_btn = (Button) findViewById(R.id.loginother_btn);// �л��ʺŰ�ť
		loginother_btn.setOnClickListener(new LoginExitBtnOnclickListener());
		exit_btn = (Button) findViewById(R.id.exit_btn);// �˳���ť
		exit_btn.setOnClickListener(new LoginExitBtnOnclickListener());
	}

	/*
	 * �ײ����ذ�ť����¼�
	 */
	public class LoginExitBtnOnclickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.loginother_btn:// �л��ʺ�
				goToLoginActivity();
				break;
			case R.id.exit_btn:// �˳�
				finish();
				System.exit(0);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * ��ת����¼ҳ��
	 * 
	 * @date 2013-6-20
	 */
	public void goToLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	/*
	 * �˵�����¼�
	 */
	public class GridViewItemOnClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			buttomMenu.showBottom(true);// ����ײ��˵�
			switch (position) {
			case 0:
				goToMyTaskListActivity();
				break;
			case 1:
				goToWatchTaskListActivity();
				break;
			case 2:
				goToCanSeeActivity();
				break;
			case 3:
				goToDataChartActivity();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * ��ת��chartActivity
	 * 
	 * @date 2013-6-7
	 */
	public void goToDataChartActivity() {
		Intent intent = new Intent();
		intent.setClass(this, DataChartActivity.class);
		startActivity(intent);
	}

	/**
	 * ��ת�ɼ�����˵�ҳ��
	 */
	public void goToCanSeeActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("taskFlag", 3);
		intent.setClass(this, TaskListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * ��ת�ҵĹ�ע��ʾҳ��
	 */
	private void goToWatchTaskListActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("taskFlag", 1);
		intent.setClass(this, TaskListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * ��ת�ҵ�������ʾҳ��
	 */
	private void goToMyTaskListActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("taskFlag", 2);
		intent.setClass(this, TaskListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	/**
	 * ���ز˵��ڲ���
	 * 
	 * @author HCOU
	 * @date 2013-5-29
	 */
	public class ButtomMenu {
		private View moreHideBottomView, input2;
		private ImageView more_imageView;
		private boolean mShowBottom = false;// �ײ���ʾ���

		public ButtomMenu() {
			/*
			 * ����ѡ�����ز˵�
			 */
			moreHideBottomView = findViewById(R.id.morehidebottom);
			more_imageView = (ImageView) findViewById(R.id.more_image);

			input2 = findViewById(R.id.input2);
			input2.setOnClickListener(new inputOnClick());

			showBottom(true);// ��ʼ��ʱ������
		}

		public class inputOnClick implements OnClickListener {
			@Override
			public void onClick(View v) {
				showBottom(!mShowBottom);
			}
		}

		public void showBottom(boolean bShow) {
			if (bShow) {
				moreHideBottomView.setVisibility(View.GONE);
				more_imageView.setImageResource(R.drawable.login_more_up);
				mShowBottom = true;
			} else {
				moreHideBottomView.setVisibility(View.VISIBLE);
				more_imageView.setImageResource(R.drawable.login_more);
				mShowBottom = false;
			}
		}
	}

	/**
	 * ��ӷ��ز˵����˳���ť
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����", 0).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}