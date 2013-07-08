package com.wewin.flowmobilesys;

import java.util.HashMap;
import java.util.List;
import com.wewin.flowmobilesys.DialogFactory;
import com.wewin.flowmobilesys.GlobalApplication;
import com.wewin.flowmobilesys.R;
import com.wewin.flowmobilesys.service.WebServiceUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * ��������б�
 * 
 * @author HCOU
 * @date 2013-6-24
 */
public class ReportListActivity extends Activity {
	private ListView listView;
	private TextView listTitle;
	private WebServiceUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;
	private SimpleAdapter adapter;
	private String userId = "";
	private List<HashMap<String, String>> list;
	private String missionId;// ����ID
	private String backFlag;
	private String report_id;
	private Button addreport_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reportlist);
		initView();
	}

	private void initView() {
		Intent intent = getIntent();
		missionId = intent.getStringExtra("missionId");// �õ�����ID
		backFlag = intent.getStringExtra("backFlag");// ���ؼ����

		listView = (ListView) findViewById(R.id.reportlistView);
		listView.setOnItemClickListener(new MyItemClickListhener());// ע�����¼�
		listView.setOnItemLongClickListener(new ItemLongClickListhener());// �����¼�

		listTitle = (TextView) findViewById(R.id.reportTitle);
		dbUtil = new WebServiceUtil();
		handler = new Handler();

		/**
		 * ��Ӱ�ť
		 */
		addreport_btn = (Button) findViewById(R.id.addreport_btn);
		addreport_btn.setOnClickListener(new AddMenuOnClicklistener());

		// �õ�ȫ���û�ID
		userId = ((GlobalApplication) getApplication()).getUserId();

		setView();
	}

	class AddMenuOnClicklistener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.addreport_btn:
				gotoAddActivity();// ���
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
				// �õ�ĳ�������������
				list = dbUtil.selectReportInfo(userId, missionId);
				// ���½���
				updateDialog();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * list view ����¼�
	 * 
	 * @author HCOU
	 * @date 2013-6-24
	 */
	class MyItemClickListhener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

		}
	}

	/**
	 * list view �����¼�
	 * 
	 * @author HCOU
	 * @date 2013-6-24
	 */
	class ItemLongClickListhener implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View view,
				int arg2, long arg3) {
			report_id = ((TextView) view.findViewById(R.id.txt_reportId))
					.getText().toString();// �õ���ǰ����ID
			doDeleteReqAndShowDialog();
			return true;
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
						R.layout.repadapter_item, new String[] { "id", "des",
								"addtime" }, new int[] { R.id.txt_reportId,
								R.id.txt_repdes, R.id.txt_repaddtime });
				listView.setAdapter(adapter);

				listTitle.setText("����������");
			}
		});
	}

	/**
	 * ��ת���������Activity
	 * 
	 * @date 2013-6-24
	 */
	public void gotoAddActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("missionId", missionId);// ����missionId
		bundle.putString("backFlag", backFlag);// ����backFlag
		intent.setClass(this, ReportAddActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * ��ת��ҳ��
	 */
	public void goToTaskListActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putInt("taskFlag", 2);
		intent.setClass(this, TaskListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * ��ת������ϸActivity
	 * 
	 * @date 2013-6-6
	 */
	public void gotoTaskDetailedActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("missionId", missionId);// ����missionId
		bundle.putInt("taskFlag", 2);// ���Ͳ˵���ǩ
		bundle.putString("canSee", "");// �����Ƿ��ѹ�ע���
		intent.setClass(this, TaskDetailedActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * �������е�ɾ��
	 * 
	 * @date 2013-6-24
	 */
	public void doDeleteReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("��ʾ")
				.setMessage("��ȷ��ɾ��������������").setIcon(R.drawable.warning)
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
		mDialog = DialogFactory.creatRequestDialog(ReportListActivity.this,
				"�������¶�ȡ������...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doDeleteReportReq(report_id);// ɾ��������webservice
				list = dbUtil.selectReportInfo(userId, missionId);
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
			if (backFlag.equals("detailed"))
				gotoTaskDetailedActivity();
			else if (backFlag.equals("list"))
				goToTaskListActivity();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}