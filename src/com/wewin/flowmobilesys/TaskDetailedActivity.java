package com.wewin.flowmobilesys;

import java.util.List;
import com.wewin.flowmobilesys.menu.ActionItem;
import com.wewin.flowmobilesys.menu.TitlePopup;
import com.wewin.flowmobilesys.util.DBUtil;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * ������ϸAcitivity
 * 
 * @author HCOU
 * @date 2013-6-6
 */
public class TaskDetailedActivity extends Activity {
	private TextView detailedTitle;
	private String missionId;
	private int taskFlag;// 1,�ҵĹ�ע��2�ҵ�����3�ɼ�����
	private DBUtil dbUtil;
	private Dialog mDialog;
	private Handler handler;
	private List<String> list;
	private Button control_btn;
	private TitlePopup titlePopup;

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
		missionId = intent.getStringExtra("missionId");
		taskFlag = intent.getIntExtra("taskFlag", 0);

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
			titlePopup.addAction(new ActionItem(this, "ɾ������",
					R.drawable.recycle_mini_2));
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
		public void onItemClick(android.widget.AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			System.out.println(arg2);

			titlePopup.dismiss();
		}
	};

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
				list = dbUtil.selectDetailedMissionInfo(missionId);
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
				txt_tasktype.setText(list.get(0));
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
			}
		});
	}
}
