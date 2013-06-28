package com.wewin.flowmobilesys.car;

import java.util.HashMap;
import java.util.List;
import com.wewin.flowmobilesys.DialogFactory;
import com.wewin.flowmobilesys.GlobalApplication;
import com.wewin.flowmobilesys.R;
import com.wewin.flowmobilesys.menu.TabMenu;
import com.wewin.flowmobilesys.util.WebServiceUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * ������ʾActivity
 * 
 * @author HCOU
 * @date 2013-6-18
 */
public class CarListActivity extends Activity {
	private ListView listView;
	private TextView listTitle;
	private WebServiceUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;
	private SimpleAdapter adapter;
	private String userId = "";
	private List<HashMap<String, String>> list;
	private String app_id;// ����ID
	private Button addmenu_btn;
	TabMenu.MenuBodyAdapter bodyAdapter;
	TabMenu tabMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.carapplist);
		initView();
	}

	private void initView() {
		listView = (ListView) findViewById(R.id.carlistview);
		listView.setOnItemClickListener(new MyItemClickListhener());// ע�����¼�

		listTitle = (TextView) findViewById(R.id.carlistTitle);
		dbUtil = new WebServiceUtil();
		handler = new Handler();

		/**
		 * ��Ӱ�ť
		 */
		addmenu_btn = (Button) findViewById(R.id.addmenu_btn);
		addmenu_btn.setOnClickListener(new AddMenuOnClicklistener());

		// �õ�ȫ���û�ID
		userId = ((GlobalApplication) getApplication()).getUserId();

		/**
		 * ���õ����˵�ͼ��
		 */
		bodyAdapter = new TabMenu.MenuBodyAdapter(this, new int[] {
				R.drawable.menu2, R.drawable.recycle }, new String[] { "������ϸ",
				"ɾ������" });

		tabMenu = new TabMenu(this, new BodyClickEvent(), R.drawable.login_bg);// ��������ʧ�Ķ���
		tabMenu.update();
		tabMenu.SetBodyAdapter(bodyAdapter);
		setView();
	}

	class AddMenuOnClicklistener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.addmenu_btn:
				gotoDetailedActivity(1);// ��Ӳ��༭
				break;
			default:
				break;
			}
		}
	}

	/**
	 * ����listView
	 */
	private void setView() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(this, "���ڼ�������...");
		mDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// �õ��ҵ�����
				list = dbUtil.selectMyCarAppInfo(userId);
				// ���½���
				updateDialog();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * listView item�����Ӧ�¼�
	 * 
	 * @author HCOU
	 * @date 2013-5-30
	 */
	public class MyItemClickListhener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// �õ���ǰ����ID
			app_id = ((TextView) view.findViewById(R.id.txt_id)).getText()
					.toString();

			int[] positions = new int[2];
			view.getLocationInWindow(positions);

			tabMenu.showAtLocation(view, Gravity.TOP, positions[0],
					positions[1]);
		}
	}

	/**
	 * ���½����߳�
	 */
	public void updateDialog() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				adapter = new SimpleAdapter(getApplicationContext(), list,
						R.layout.adapter_item_car, new String[] { "id",
								"carid", "username", "addtime", "carnum",
								"destination", "status" }, new int[] {
								R.id.txt_id, R.id.txt_carid, R.id.txt_username,
								R.id.txt_addtime, R.id.txt_carnum,
								R.id.txt_destination, R.id.txt_carstatus });
				listView.setAdapter(adapter);

				listTitle.setText("�ҵ�����");
			}
		});
	}

	/**
	 * �����˵�����¼�
	 * 
	 * @author HCOU
	 * @date 2013-6-5
	 */
	class BodyClickEvent implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
			tabMenu.SetBodySelect(position, Color.GRAY);// ����ѡ��״̬
			switch (position) {
			case 0:
				// ��ת������ϸ
				gotoDetailedActivity(2);// �ɱ༭
				break;
			case 1:
				// ɾ������
				doDeleteReqAndShowDialog();
				break;
			default:
				break;
			}
			tabMenu.dismiss();// ���ٵ����˵�
		}
	}

	/**
	 * ��ת������ϸActivity
	 * 
	 * @date 2013-6-6
	 */
	public void gotoDetailedActivity(int editflag) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("app_id", app_id);// ����app_id
		bundle.putInt("editflag", editflag);// �Ƿ�༭���
		intent.setClass(this, AppDetailedActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * ��ת��ҳ��
	 */
	public void goMainActivity() {
		Intent intent = new Intent();
		intent.setClass(this, CarMainActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * �ҵ�����Activity�е�ɾ��
	 * 
	 * @date 2013-6-5
	 */
	public void doDeleteReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("��ʾ")
				.setMessage("��ȷ��ɾ������������").setIcon(R.drawable.warning)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doDeleteReq();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).create();
		alertDialog.show();
	}

	/**
	 * ����ɾ������webservice
	 * 
	 * @date 2013-6-18
	 */
	public void doDeleteReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(CarListActivity.this,
				"�������¶�ȡ�ҵ�����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doDeleteCarAppReq(app_id);// ɾ������webservice
				list = dbUtil.selectMyCarAppInfo(userId);// ���¶�ȡ�ҵ�����
				// ���½���
				updateDialog();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ��ӷ��ز˵����˳���ť
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			goMainActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}