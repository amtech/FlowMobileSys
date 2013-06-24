package com.wewin.flowmobilesys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.wewin.flowmobilesys.GlobalApplication;
import com.wewin.flowmobilesys.adapter.ListAdapter;
import com.wewin.flowmobilesys.menu.TabMenu;
import com.wewin.flowmobilesys.util.DBUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * ������ʾActivity
 * 
 * @author HCOU
 * @date 2013-6-5
 */
public class TaskListActivity extends Activity {
	private ListView listView;
	private TextView taskTitle;
	private DBUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;
	private ListAdapter adapter;
	private int taskFlag = 0;
	private String userId = "";
	private String missionId = "";
	private String canSee = "";
	private List<HashMap<String, String>> list;
	TabMenu.MenuBodyAdapter bodyAdapter;
	TabMenu tabMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasklist);
		initView();
	}

	private void initView() {
		listView = (ListView) findViewById(R.id.listView);
		listView.setOnItemClickListener(new MyItemClickListhener());// ע�����¼�

		taskTitle = (TextView) findViewById(R.id.taskTitle);
		dbUtil = new DBUtil();
		handler = new Handler();

		// �õ�ȫ���û�ID
		userId = ((GlobalApplication) getApplication()).getUserId();

		Intent intent = getIntent();
		taskFlag = intent.getIntExtra("taskFlag", 0);

		/**
		 * ���õ����˵�ͼ��
		 */
		switch (taskFlag) {
		case 1:
			bodyAdapter = new TabMenu.MenuBodyAdapter(this, new int[] {
					R.drawable.menu2, R.drawable.cancelwatch_mini },
					new String[] { "������ϸ", "ȡ����ע" });
			break;
		case 2:
			bodyAdapter = new TabMenu.MenuBodyAdapter(this, new int[] {
					R.drawable.menu2, R.drawable.write }, new String[] {
					"������ϸ", "�������" });
			break;
		case 3:
			bodyAdapter = new TabMenu.MenuBodyAdapter(this, new int[] {
					R.drawable.menu2, R.drawable.watch_mini }, new String[] {
					"������ϸ", "��ע����" });
			break;
		}

		tabMenu = new TabMenu(this, new BodyClickEvent(), R.drawable.login_bg);// ��������ʧ�Ķ���
		tabMenu.update();
		tabMenu.SetBodyAdapter(bodyAdapter);
		setView();
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
				switch (taskFlag) {
				case 1:// ��ע����
					list = dbUtil.selectWatchMissionInfo(userId);
					break;
				case 2:// �ҵ�����
					list = dbUtil.selectMyMissionInfo(userId);
					break;
				case 3:// �ɼ�����
					list = dbUtil.selectCanSeeMissionInfo(userId);
					break;
				default:
					list = new ArrayList<HashMap<String, String>>();
					break;
				}
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
			missionId = ((TextView) view.findViewById(R.id.txt_missionid))
					.getText().toString();
			// �õ��Ƿ��ѹ�ע���
			canSee = ((TextView) view.findViewById(R.id.txt_counts)).getText()
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
				adapter = new ListAdapter(getApplicationContext(), list);
				listView.setAdapter(adapter);

				switch (taskFlag) {
				case 1:
					taskTitle.setText("�ҵĹ�ע");
					break;
				case 2:
					taskTitle.setText("�ҵ�����");
					break;
				case 3:
					taskTitle.setText("�ɼ�����");
					break;
				}
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
				gotoDetailedActivity();
				break;
			case 1:
				// ���ڹ�עѡ���
				checkWitchWindow();
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
	public void gotoDetailedActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("missionId", missionId);// ����missionId
		bundle.putInt("taskFlag", taskFlag);// ���Ͳ˵���ǩ
		bundle.putString("canSee", canSee);// �����Ƿ��ѹ�ע���
		intent.setClass(this, TaskDetailedActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * �ж����Ǹ�activity����
	 * 
	 * @date 2013-6-5
	 */
	public void checkWitchWindow() {
		switch (taskFlag) {
		case 1:// ��עҳ��
			doCancleWatchReqAndShowDialog();
			break;
		case 2:// �ҵ�����ҳ��
			goToWriteActivity();
			break;
		case 3:// �ɼ�����
			if ("�ѹ�ע".equals(canSee)) {
				doShowWarningDialog();
				break;
			}
			doOkWatchReqAndShowDialog();
			break;
		default:
			break;
		}
	}

	/**
	 * �ѱ���ע������ʾ
	 * 
	 * @date 2013-6-5
	 */
	public void doShowWarningDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("��ʾ")
				.setMessage("�������ѱ����ע��").setIcon(R.drawable.warning)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).create();
		alertDialog.show();
	}

	/**
	 * �ҵ�����Activity�е��������
	 * 
	 * @date 2013-6-5
	 */
	public void goToWriteActivity() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("missionId", missionId);// ����missionId
		intent.setClass(this, ReportListActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);
		finish();
	}

	/**
	 * ��עActivity�еĹ�ע
	 * 
	 * @date 2013-6-5
	 */
	public void doCancleWatchReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("��ʾ")
				.setMessage("��ȷ��ȡ����������Ĺ�ע��").setIcon(R.drawable.warning)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doAcessCancelReq();
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
	 * @date 2013-6-5
	 */
	public void doDeleteReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(TaskListActivity.this,
				"�������¶�ȡ�ҵ�����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doDeleteReq(missionId);// ɾ������webservice
				list = dbUtil.selectMyMissionInfo(userId);// ���¶�ȡ�ҵ�����
				// ���½���
				updateDialog();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ����ȡ����עwebservice
	 * 
	 * @date 2013-6-5
	 */
	public void doAcessCancelReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(TaskListActivity.this,
				"�������¶�ȡ��ע����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAcessCancelReq(userId, missionId);// ȡ����עwebservice
				list = dbUtil.selectWatchMissionInfo(userId);// ���¶�ȡ��ע����
				// ���½���
				updateDialog();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * �ҵ�����Activity�еĹ�ע
	 * 
	 * @date 2013-6-5
	 */
	public void doOkWatchReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("��ʾ")
				.setMessage("��ȷ����ע����������").setIcon(R.drawable.warning)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doAcessOkReq();
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
	 * ���ʹ�עwebservice
	 * 
	 * @date 2013-6-5
	 */
	public void doAcessOkReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(TaskListActivity.this,
				"�������¶�ȡ�ɼ�����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAcessOkReq(userId, missionId);// ��עwebservice
				list = dbUtil.selectCanSeeMissionInfo(userId);// ���¶�ȡ�ɼ�����
				// ���½���
				updateDialog();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}
}
