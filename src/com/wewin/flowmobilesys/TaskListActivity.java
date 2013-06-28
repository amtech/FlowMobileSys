package com.wewin.flowmobilesys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.wewin.flowmobilesys.GlobalApplication;
import com.wewin.flowmobilesys.adapter.ListAdapter;
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
	private WebServiceUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;
	private ListAdapter adapter;
	private int taskFlag = 0;
	private String userId = "", userName = "", rolename = "",
			department_name = "";
	private String missionId = "";
	private String intent_missionId;
	private String canSee = "";
	private String datachart_index = "";
	private String task_status = "", zrrName = "", ysrName = "";
	private List<HashMap<String, String>> list;
	TabMenu.MenuBodyAdapter bodyAdapter;
	TabMenu tabMenu;
	private String auditorcompleteFlag = "";

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
		dbUtil = new WebServiceUtil();
		handler = new Handler();

		// �õ�ȫ���û�ID
		userId = ((GlobalApplication) getApplication()).getUserId();
		// �û���
		userName = ((GlobalApplication) getApplication()).getUserName();
		// �õ�ȫ���û���ɫ����
		rolename = ((GlobalApplication) getApplication()).getRolename();
		// �õ�ȫ���û���������
		department_name = ((GlobalApplication) getApplication())
				.getDepartment_name();

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
		case 4:// �������б�
			intent_missionId = intent.getStringExtra("intent_missionId");
			break;
		case 5:// DataChart�б�
			datachart_index = intent.getStringExtra("index");
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
					list = dbUtil.selectCanSeeMissionInfo(userId, rolename,
							department_name);
					break;
				case 4:// ������
					list = dbUtil.selectChildMissionInfo(intent_missionId);
					break;
				case 5:// DataChart�б�
					list = dbUtil.selectChartMissionInfo(userId,
							datachart_index, rolename, department_name);
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
			// ����״̬
			task_status = ((TextView) view.findViewById(R.id.txt_status))
					.getText().toString();
			// ������
			zrrName = ((TextView) view.findViewById(R.id.txt_zrrName))
					.getText().toString();
			// ������
			ysrName = ((TextView) view.findViewById(R.id.txt_ysrName))
					.getText().toString();

			reInitTabMenu();// ����missionType���¹��쵯���˵�

			int[] positions = new int[2];
			view.getLocationInWindow(positions);

			if (taskFlag != 4 && taskFlag != 5) {// �������DataChart������
				tabMenu.showAtLocation(view, Gravity.TOP, positions[0],
						positions[1]);
			}
		}
	}

	/**
	 * ���¹��쵯���˵�
	 * 
	 * @date 2013-6-26
	 */
	public void reInitTabMenu() {
		if (zrrName.equals(userName) && taskFlag == 2
				&& (task_status.equals("������") || task_status.equals("������ʱ"))) {
			bodyAdapter = new TabMenu.MenuBodyAdapter(TaskListActivity.this,
					new int[] { R.drawable.menu2, R.drawable.write,
							R.drawable.complete_mini }, new String[] { "������ϸ",
							"�������", "���" });
			auditorcompleteFlag = "complete";
		} else if (ysrName.equals(userName) && taskFlag == 2
				&& task_status.equals("�ύ����")) {
			bodyAdapter = new TabMenu.MenuBodyAdapter(TaskListActivity.this,
					new int[] { R.drawable.menu2, R.drawable.write,
							R.drawable.audit_mini }, new String[] { "������ϸ",
							"�������", "���" });
			auditorcompleteFlag = "audit";
		} else if (ysrName.equals(userName)
				&& taskFlag == 2
				&& (task_status.equals("�����") || task_status.equals("��ʱ���") || task_status
						.equals("��ʱ"))) {
			bodyAdapter = new TabMenu.MenuBodyAdapter(TaskListActivity.this,
					new int[] { R.drawable.menu2, R.drawable.write,
							R.drawable.reaudit_mini }, new String[] { "������ϸ",
							"�������", "����" });
			auditorcompleteFlag = "reaudit";
		} else {
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
						R.drawable.menu2, R.drawable.watch_mini },
						new String[] { "������ϸ", "��ע����" });
				break;
			}
		}
		tabMenu = new TabMenu(TaskListActivity.this, new BodyClickEvent(),
				R.drawable.login_bg);// ��������ʧ�Ķ���
		tabMenu.update();
		tabMenu.SetBodyAdapter(bodyAdapter);
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
					if (rolename.equals("��ͨԱ��") || rolename.equals("����")) {
						taskTitle.setText("�ɼ�����");
					} else if (rolename.equals("���ž���")) {
						taskTitle.setText(department_name + "����");
					} else if (rolename.equals("���ܾ���")
							|| rolename.equals("�ܾ���")) {
						taskTitle.setText("��������");
					} else {
						taskTitle.setText("�ɼ�����");
					}
					break;
				case 4:
					taskTitle.setText("������");
					break;
				case 5:
					taskTitle.setText("�����ܸ�������ʾ");
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
			case 2:// ��˻�����ɲ���
				goToAuditOrCompleteWindow();
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
	 * ��˻�����ɲ���
	 * 
	 * @date 2013-6-26
	 */
	public void goToAuditOrCompleteWindow() {
		if (auditorcompleteFlag.equals("complete")) {// ��ɲ���
			doCompleteShowDialog();
		} else if (auditorcompleteFlag.equals("audit")) {// ��������
			doAuditShowDialog("���");
		} else if (auditorcompleteFlag.equals("reaudit")) {// �������
			doAuditShowDialog("�������");
		}
	}

	/**
	 * ���Dialog
	 * 
	 * @date 2013-6-5
	 */
	public void doAuditShowDialog(String str) {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("��ʾ")
				.setMessage("��ȷ��" + str + "��������").setIcon(R.drawable.warning)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doAuditReq();
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
	 * �����������webservice
	 * 
	 * @date 2013-6-26
	 */
	public void doAuditReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(TaskListActivity.this,
				"�������¶�ȡ����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAuditTaskReq(missionId, userId);// ��������webservice

				list = dbUtil.selectMyMissionInfo(userId);// ���¶�ȡ�ҵ�����
				// ���½���
				updateDialog();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ���Dialog
	 * 
	 * @date 2013-6-5
	 */
	public void doCompleteShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("��ʾ")
				.setMessage("��ȷ���ύ����������").setIcon(R.drawable.warning)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doCompleteReq();
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
	 * �����������webservice
	 * 
	 * @date 2013-6-26
	 */
	public void doCompleteReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(TaskListActivity.this,
				"�������¶�ȡ����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doCompleteTaskReq(missionId, userId);// �������webservice
				list = dbUtil.selectMyMissionInfo(userId);// ���¶�ȡ�ҵ�����
				// ���½���
				updateDialog();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
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
		case 4:// ������ҳ��
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
		bundle.putString("backFlag", "list");// ����backFlag
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
				list = dbUtil.selectCanSeeMissionInfo(userId, rolename,
						department_name);// ���¶�ȡ�ɼ�����
				// ���½���
				updateDialog();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}
}
