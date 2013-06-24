package com.wewin.flowmobilesys;

import java.util.ArrayList;
import java.util.List;

import com.wewin.flowmobilesys.adapter.OnGestureAndTouchAdapter;
import com.wewin.flowmobilesys.menu.ActionItem;
import com.wewin.flowmobilesys.menu.TitlePopup;
import com.wewin.flowmobilesys.util.DBUtil;
import com.wewin.flowmobilesys.util.FileUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ������ϸAcitivity
 * 
 * @author HCOU
 * @date 2013-6-6
 */
public class TaskDetailedActivity extends Activity {
	private TextView detailedTitle;
	private String missionId;
	private String canSee = "";
	int updownFlag = 0;// �����һ��������һ�����0,�ϣ�1��
	private String userid;
	private String next_id;// ��һ��
	private String for_id;// ��һ��
	private int taskFlag;// 1�ҵĹ�ע��2�ҵ�����3�ɼ�����
	private DBUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;
	private List<String> list;
	private Button control_btn;
	private TitlePopup titlePopup;
	private GestureDetector mGestureDetector;
	private LinearLayout touchLayout;

	private TextView txt_tasktype, txt_response_person, txt_taskid,
			txt_task_creator, txt_task_yanshou, txt_task_name,
			txt_task_startdate, txt_task_enddate, txt_task_mj, txt_task_status,
			txt_task_fenguan, txt_task_writedate, txt_task_des,
			txt_task_counts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taskdetailed);
		initView();
	}

	/**
	 * ��ʼ������
	 * 
	 * @date 2013-6-6
	 */
	private void initView() {
		Intent intent = getIntent();
		// �õ�ȫ���û�ID
		userid = ((GlobalApplication) getApplication()).getUserId();
		missionId = intent.getStringExtra("missionId");
		taskFlag = intent.getIntExtra("taskFlag", 0);
		canSee = intent.getStringExtra("canSee");
		dbUtil = new DBUtil();
		handler = new Handler();

		detailedTitle = (TextView) findViewById(R.id.detailedTitle);
		detailedTitle.setText("������ϸ");// ���ñ�����
		control_btn = (Button) findViewById(R.id.control_btn);// �������˵���ť
		control_btn.setOnClickListener(new TitleButtnOnclickLisenter());

		txt_tasktype = (TextView) findViewById(R.id.txt_tasktype);
		txt_response_person = (TextView) findViewById(R.id.txt_response_person);
		txt_taskid = (TextView) findViewById(R.id.txt_taskid);
		txt_task_creator = (TextView) findViewById(R.id.txt_task_creator);
		txt_task_yanshou = (TextView) findViewById(R.id.txt_task_yanshou);
		txt_task_name = (TextView) findViewById(R.id.txt_task_name);
		txt_task_startdate = (TextView) findViewById(R.id.txt_task_startdate);
		txt_task_enddate = (TextView) findViewById(R.id.txt_task_enddate);
		txt_task_mj = (TextView) findViewById(R.id.txt_task_mj);
		txt_task_status = (TextView) findViewById(R.id.txt_task_status);
		txt_task_fenguan = (TextView) findViewById(R.id.txt_task_fenguan);
		txt_task_writedate = (TextView) findViewById(R.id.txt_task_writedate);
		txt_task_des = (TextView) findViewById(R.id.txt_task_des);
		txt_task_counts = (TextView) findViewById(R.id.txt_task_counts);

		initTouchListener();// �󶨴��������¼�
		initTitleMenu();// ��ʼ�������˵�
		setViewData();// ��ѯҳ��Ҫ��ʾ������
	}

	/**
	 * ��ʼ���ײ��˵�
	 * 
	 * @date 2013-6-8
	 */
	private void initTitleMenu() {
		titlePopup = new TitlePopup(this, new TitleItemOnclickLisenter(),
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titlePopup.update();
		switch (taskFlag) {
		case 1:// �ҵĹ�ע
			titlePopup.addAction(new ActionItem(this, "ȡ����ע",
					R.drawable.cancelwatch_mini_2));
			break;
		case 2:// �ҵ�����
			break;
		case 3:// �ɼ�����
			titlePopup.addAction(new ActionItem(this, "��ע",
					R.drawable.watch_mini_2));
			break;
		default:
			break;
		}
		titlePopup.addAction(new ActionItem(this, "����",
				R.drawable.export_mini_2));
		titlePopup
				.addAction(new ActionItem(this, "��һ��", R.drawable.next_mini_2));
		titlePopup.addAction(new ActionItem(this, "��һ��",
				R.drawable.return_mini_2));
	}

	/**
	 * �����˵�����¼���
	 * 
	 * @author HCOU
	 * @date 2013-6-8
	 */
	class TitleButtnOnclickLisenter implements OnClickListener {
		@Override
		public void onClick(View v) {
			titlePopup.show(v);
		}
	};

	/**
	 * �����˵������¼�
	 * 
	 * @author HCOU
	 * @date 2013-6-9
	 */
	class TitleItemOnclickLisenter implements OnItemClickListener {
		public void onItemClick(android.widget.AdapterView<?> adapterView,
				View view, int position, long id) {
			switch (position) {
			case 0:// ��һ����ť
				switch (taskFlag) {
				case 1:// �ҵĹ�ע��ȡ����ע����
					doCancleWatchReqAndShowDialog();
					break;
				case 2:// �ҵ������е�ɾ������
					doDeleteReqAndShowDialog();
					break;
				case 3:// �ɼ������еĹ�ע����
					if ("�ѹ�ע".equals(canSee)) {
						doShowWarningDialog();
						break;
					}
					doOkWatchReqAndShowDialog();
					break;
				default:
					break;
				}
				break;
			case 1:// ����
				doExportFileShowDialog();
				break;
			case 2:// ��һ��
				doGotoNextMissionDetailed();
				break;
			case 3:// ��һ��
				doGotoForMissionDetailed();
				break;
			default:
				break;
			}
			titlePopup.dismiss();
		}
	};

	/**
	 * �����ļ�ȷ��
	 * 
	 * @date 2013-6-5
	 */
	public void doExportFileShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("��ʾ")
				.setMessage("��ȷ����������������Ϣ��SD����").setIcon(R.drawable.warning)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doExportFileToSdCard();
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
	 * �����ļ���SdCard
	 * 
	 * @date 2013-6-13
	 */
	public void doExportFileToSdCard() {
		FileUtil util = new FileUtil();
		String data = "                    ������ϸ\n\n" + "    �������ͣ�"
				+ txt_tasktype.getText().toString() + "\n" + "      �����ˣ�"
				+ txt_response_person.getText().toString() + "\n" + "    �����ţ�"
				+ txt_taskid.getText().toString() + "\n" + "  �����ƶ��ˣ�"
				+ txt_task_creator.getText().toString() + "\n" + "      �����ˣ�"
				+ txt_task_yanshou.getText().toString() + "\n" + "    �������ƣ�"
				+ txt_task_name.getText().toString() + "\n" + "����ʼ���ڣ�"
				+ txt_task_startdate.getText().toString() + "\n" + "����������ڣ�"
				+ txt_task_enddate.getText().toString() + "\n" + "    �����ܼ���"
				+ txt_task_mj.getText().toString() + "\n" + "    ����״̬��"
				+ txt_task_status.getText().toString() + "\n" + "    �ֹ��쵼��"
				+ txt_task_fenguan.getText().toString() + "\n" + "    ����ڣ�"
				+ txt_task_writedate.getText().toString() + "\n" + "��������������"
				+ txt_task_des.getText().toString() + "\n" + "    ����������"
				+ txt_task_counts.getText().toString() + "\n";
		util.WriteFile(missionId + userid + ".txt", data);
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
		mDialog = DialogFactory.creatRequestDialog(TaskDetailedActivity.this,
				"�������¶�ȡ�ɼ�����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAcessOkReq(userid, missionId);// ��עwebservice
				// ��ת���ɼ�����ҳ��
				goToTaskListActivity(3);
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ��עActivity�е�ȡ����ע
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
		mDialog = DialogFactory.creatRequestDialog(TaskDetailedActivity.this,
				"�������¶�ȡ��ע����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAcessCancelReq(userid, missionId);// ȡ����עwebservice
				// ��ת���ҵĹ�עҳ��
				goToTaskListActivity(1);
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ������һ������
	 * 
	 * @date 2013-6-13
	 */
	public void doGotoNextMissionDetailed() {
		updownFlag = 1;
		missionId = next_id;
		setViewData();// ���¼�������
	}

	/**
	 * ������һ������
	 * 
	 * @date 2013-6-13
	 */
	public void doGotoForMissionDetailed() {
		updownFlag = 0;
		missionId = for_id;
		setViewData();// ���¼�������
	}

	/**
	 * ������ϸActivity�е�ɾ��
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
	 * @date 2013-6-5
	 */
	public void doDeleteReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(TaskDetailedActivity.this,
				"�������¶�ȡ�ҵ�����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doDeleteReq(missionId);// ɾ������webservice
				// ��ת���ҵ�����ҳ��
				goToTaskListActivity(2);
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ��ת�ҵ�������ʾҳ��
	 */
	private void goToTaskListActivity(final int flag) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("taskFlag", flag);
				intent.setClass(getApplicationContext(), TaskListActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		});
	}

	/**
	 * ����listView
	 */
	private void setViewData() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(this, "���ڼ�������...");
		mDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// ����webservice��ȡ����
				switch (taskFlag) {
				case 1:// �ҵĹ�ע
					list = dbUtil.selectWatchMissionDetailedInfo(missionId,
							userid);
					break;
				case 2:// �ҵ�����
					list = dbUtil
							.selectMyMissionDetailedInfo(missionId, userid);
					break;
				case 3:// �ɼ�����
					list = dbUtil.selectCanSeeMissionDetailedInfo(missionId,
							userid);
					break;
				default:
					list = new ArrayList<String>();
					break;
				}
				// ���½���
				updateView();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ������ͼ
	 * 
	 * @date 2013-6-6
	 */
	public void updateView() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (list.size() > 0) {
					if (list.get(0).equals("0")) {
						txt_tasktype.setText("����ʽ");
					} else {
						txt_tasktype.setText("�ɷ�ʽ");
					}
					txt_response_person.setText(list.get(1).toString());
					txt_taskid.setText(list.get(2).toString());
					txt_task_creator.setText(list.get(3).toString());
					txt_task_yanshou.setText(list.get(4).toString());
					txt_task_name.setText(list.get(5).toString());
					txt_task_startdate.setText(list.get(6).toString());
					txt_task_enddate.setText(list.get(7).toString());
					txt_task_mj.setText(list.get(8).toString());
					txt_task_status.setText(list.get(9).toString());
					txt_task_fenguan.setText(list.get(10).toString());
					txt_task_writedate.setText(list.get(11).toString());
					txt_task_des.setText(list.get(12).toString());
					txt_task_counts.setText(list.get(13).toString());

					missionId = list.get(14).toString();
					next_id = list.get(15).toString();
					for_id = list.get(16).toString();
				} else {
					switch (updownFlag) {
					case 0:
						Toast.makeText(getApplicationContext(), "�Ѿ�������", 0)
								.show();
						break;
					case 1:
						Toast.makeText(getApplicationContext(), "�Ѿ�������", 0)
								.show();
						break;
					}
				}
			}
		});
	}

	/**
	 * ���ô����¼�
	 * 
	 * @date 2013-6-14
	 */
	public void initTouchListener() {
		/**
		 * ���ô��������¼�
		 */
		mGestureDetector = new GestureDetector(
				(OnGestureListener) new OnGestureAndTouchAdapter() {
					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if (e1.getX() - e2.getX() > 20
								&& Math.abs(velocityX) > 0) {// ��������
							doGotoForMissionDetailed();// ��һҳ
						} else if (e2.getX() - e1.getX() > 20
								&& Math.abs(velocityX) > 0) {// ��������
							doGotoNextMissionDetailed();// ��һҳ
						}
						return false;
					}
				});

		touchLayout = (LinearLayout) findViewById(R.id.touchlayout);
		touchLayout.setOnTouchListener(new OnGestureAndTouchAdapter() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});
		touchLayout.setLongClickable(true);
	}

	/**
	 * ��ӷ��ز˵����˳���ť
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			goToTaskListActivity(taskFlag);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
