package com.wewin.flowmobilesys.car;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.wewin.flowmobilesys.DialogFactory;
import com.wewin.flowmobilesys.GlobalApplication;
import com.wewin.flowmobilesys.R;
import com.wewin.flowmobilesys.adapter.OptionsAdapter;
import com.wewin.flowmobilesys.menu.ActionItem;
import com.wewin.flowmobilesys.menu.TitlePopup;
import com.wewin.flowmobilesys.util.DBUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ������ϸActivity
 * 
 * @author HCOU
 * @date 2013-6-18
 */
public class AppDetailedActivity extends Activity implements Callback {
	private TextView detailedTitle;
	private String app_id, car_id, userId;
	private DBUtil dbUtil;
	private Dialog mDialog;
	private Handler handler, showBoxHandler;
	private List<String> list;
	private Button control_btn;// ͷ���˵���ť
	private TitlePopup titlePopup;
	private int flag;
	private int editflag = 0;// �༭����ӱ��
	private int pwidth;
	// ������ѡ������Դ
	private ArrayList<String> datas = new ArrayList<String>();
	// չʾ��������ѡ���ListView
	private ListView listView = null;
	// �Զ���Adapter
	private OptionsAdapter optionsAdapter = null;
	// PopupWindow����
	private PopupWindow selectPopupWindow = null;

	private LinearLayout layout, addBtnlayout, carlist_layout;
	private Button showbox_btn;
	private Button datapicker_btn1, datapicker_btn2;// ��ʾʱ�䰴ť
	private Button complete_btn, cancle_btn, add_btn;// ��ɺ�ȡ����ť
	private int mYear, mMonth, mDay;
	private static final int DATE_DIALOG_ID = 1;
	private static final int SHOW_DATAPICK = 0;

	private EditText txt_addcarname, txt_begintime, txt_endtime, txt_personnum,
			txt_reason, txt_destination, txt_remark;

	// �Ƿ��ʼ����ɱ�־
	private boolean initflag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.appdetailed);
		initView();
	}

	/**
	 * ��ʼ������
	 * 
	 * @date 2013-6-6
	 */
	private void initView() {
		Intent intent = getIntent();

		app_id = intent.getStringExtra("app_id");
		editflag = intent.getIntExtra("editflag", 0);

		// �õ�ȫ���û�ID
		userId = ((GlobalApplication) getApplication()).getUserId();

		dbUtil = new DBUtil();
		handler = new Handler();

		detailedTitle = (TextView) findViewById(R.id.detailedTitle);
		detailedTitle.setText("������ϸ");// ���ñ�����
		control_btn = (Button) findViewById(R.id.carcontrol_btn);// �������˵���ť
		control_btn.setOnClickListener(new TitleButtnOnclickLisenter());

		datapicker_btn1 = (Button) findViewById(R.id.datapicker_btn1);// ʱ�䰴ť
		datapicker_btn1
				.setOnClickListener(new DatePickerButtnOnclickLisenter());
		datapicker_btn2 = (Button) findViewById(R.id.datapicker_btn2);// ʱ�䰴ť
		datapicker_btn2
				.setOnClickListener(new DatePickerButtnOnclickLisenter());

		carlist_layout = (LinearLayout) findViewById(R.id.carlist_layout);// ��һ��combobox����

		layout = (LinearLayout) findViewById(R.id.btn_layout);
		complete_btn = (Button) findViewById(R.id.complete_btn);
		complete_btn.setOnClickListener(new CompleteCancleAddOnclickLisenter());
		cancle_btn = (Button) findViewById(R.id.cancle_btn);
		cancle_btn.setOnClickListener(new CompleteCancleAddOnclickLisenter());

		addBtnlayout = (LinearLayout) findViewById(R.id.btn_layout_add);
		add_btn = (Button) findViewById(R.id.add_btn);
		add_btn.setOnClickListener(new CompleteCancleAddOnclickLisenter());

		txt_addcarname = (EditText) findViewById(R.id.txt_addcarname);
		txt_begintime = (EditText) findViewById(R.id.txt_begintime);
		txt_endtime = (EditText) findViewById(R.id.txt_endtime);
		txt_personnum = (EditText) findViewById(R.id.txt_personnum);
		txt_reason = (EditText) findViewById(R.id.txt_reason);
		txt_destination = (EditText) findViewById(R.id.txt_destination);
		txt_remark = (EditText) findViewById(R.id.txt_remark);

		initTitleMenu();// ��ʼ�������˵�
		setDateTime();// ����ʱ���ĳ�ʼֵ
		/**
		 * ���ñ������˵��Ƿ�ɼ�,�͵ײ���Ӱ�ť
		 */
		switch (editflag) {
		case 1:// Add
			control_btn.setVisibility(View.GONE);
			addBtnlayout.setVisibility(View.VISIBLE);
			carlist_layout.setVisibility(View.GONE);// ���س���ѡ��� //TODO

			datapicker_btn1.setVisibility(View.VISIBLE);
			datapicker_btn2.setVisibility(View.VISIBLE);
			txt_personnum.setFocusableInTouchMode(true);
			txt_reason.setFocusableInTouchMode(true);
			txt_destination.setFocusableInTouchMode(true);
			txt_remark.setFocusableInTouchMode(true);
			break;
		case 2:// Edit
			carlist_layout.setVisibility(View.GONE);
			control_btn.setVisibility(View.VISIBLE);
			addBtnlayout.setVisibility(View.GONE);
			setViewData();// ��ѯҳ��Ҫ��ʾ������
			break;
		default:
			break;
		}
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
		titlePopup.addAction(new ActionItem(this, "�༭", R.drawable.edit_mini));
		titlePopup.addAction(new ActionItem(this, "ɾ��",
				R.drawable.recycle_mini_2));
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
			case 0:// ��һ����ť,�༭
				activeEditing();// ���������
				break;
			case 1:// ɾ��
				doDeleteReqAndShowDialog();
				break;
			default:
				break;
			}
			titlePopup.dismiss();
		}
	}

	/**
	 * ���ȡ����ť����¼�
	 * 
	 * @author HCOU
	 * @date 2013-6-19
	 */
	class CompleteCancleAddOnclickLisenter implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.complete_btn:
				if (checkNull()) {
					doupdateReqAndShowDialog();// ��ɱ༭
				} else {
					Toast.makeText(AppDetailedActivity.this, "ǰ3��Ϊ��������", 0)
							.show();
				}
				break;
			case R.id.cancle_btn:
				cancleEditing();// ȡ���༭
				break;
			case R.id.add_btn:
				if (checkNull()) {
					doaddReqAndShowDialog();// ���
				} else {
					Toast.makeText(AppDetailedActivity.this, "ǰ3��Ϊ��������", 0)
							.show();
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * ��������ʱ��Ϊ��
	 * 
	 * @date 2013-6-20
	 * @return
	 */
	public boolean checkNull() {
		if (txt_begintime.getText().toString().equals("")) {
			return false;
		} else if (txt_endtime.getText().toString().equals("")) {
			return false;
		} else if (txt_personnum.getText().toString().equals("")) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * ���������
	 * 
	 * @date 2013-6-19
	 */
	public void activeEditing() {
		datapicker_btn1.setVisibility(View.VISIBLE);
		datapicker_btn2.setVisibility(View.VISIBLE);
		layout.setVisibility(View.VISIBLE);

		txt_personnum.setFocusableInTouchMode(true);
		txt_reason.setFocusableInTouchMode(true);
		txt_destination.setFocusableInTouchMode(true);
		txt_remark.setFocusableInTouchMode(true);
	}

	/**
	 * ȡ������
	 * 
	 * @date 2013-6-19
	 */
	public void cancleEditing() {
		datapicker_btn1.setVisibility(View.GONE);
		datapicker_btn2.setVisibility(View.GONE);
		layout.setVisibility(View.GONE);

		txt_personnum.setFocusable(false);
		txt_reason.setFocusable(false);
		txt_destination.setFocusable(false);
		txt_remark.setFocusable(false);
	}

	/**
	 * ���ʱ༭����webservice
	 * 
	 * @date 2013-6-18
	 */
	public void doUpdateReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(AppDetailedActivity.this,
				"�������¶�ȡ�ҵ�����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doUpdateCarAppReq(app_id, txt_begintime.getText()
						.toString(), txt_endtime.getText().toString(),
						txt_personnum.getText().toString(), txt_reason
								.getText().toString(), txt_destination
								.getText().toString(), txt_remark.getText()
								.toString());// �޸�����webservice
				// ��ת������ҳ��
				goToCarAppListActivity();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ��������Activity�еı༭�޸�
	 * 
	 * @date 2013-6-19
	 */
	public void doupdateReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("��ʾ")
				.setMessage("��ȷ���޸ĸ���������").setIcon(R.drawable.warning)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doUpdateReq();
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
	 * @date 2013-6-18
	 */
	public void doAddReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(AppDetailedActivity.this,
				"�������¶�ȡ�ҵ�����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doAddCarAppReq(userId, "  ", "0", txt_begintime
						.getText()// TODO
						.toString(), txt_endtime.getText().toString(),
						txt_personnum.getText().toString(), txt_reason
								.getText().toString(), txt_destination
								.getText().toString(), txt_remark.getText()
								.toString());// �������webservice
				// ��ת������ҳ��
				goToCarAppListActivity();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ��������Activity�е����
	 * 
	 * @date 2013-6-20
	 */
	public void doaddReqAndShowDialog() {
		Dialog alertDialog = new AlertDialog.Builder(this).setTitle("��ʾ")
				.setMessage("��ȷ����Ӹ���������").setIcon(R.drawable.warning)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doAddReq();
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
	 * @date 2013-6-19
	 */
	public void doDeleteReq() {
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		mDialog = DialogFactory.creatRequestDialog(AppDetailedActivity.this,
				"�������¶�ȡ�ҵ�����...");
		mDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				dbUtil.doDeleteCarAppReq(app_id);// ɾ������webservice
				// ��ת������ҳ��
				goToCarAppListActivity();
				// ���ٴ���
				mDialog.dismiss();
			}
		}).start();
	}

	/**
	 * ��������Activity�е�ɾ��
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
	 * ��ת�ҵ�������ʾҳ��
	 */
	private void goToCarAppListActivity() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), CarListActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	/**
	 * ��ת��CarListActivity
	 * 
	 * @date 2013-6-7
	 */
	public void goToMyCarAppActivity() {
		Intent intent = new Intent();
		intent.setClass(this, CarListActivity.class);
		startActivity(intent);
		finish();
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
				list = dbUtil.selectCarAppDetailedInfo(app_id);
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
				txt_begintime.setText(list.get(0).toString());
				txt_endtime.setText(list.get(1).toString());
				txt_personnum.setText(list.get(2).toString());
				txt_reason.setText(list.get(3).toString());
				txt_destination.setText(list.get(4).toString());
				txt_remark.setText(list.get(5).toString());
			}
		});
	}

	/**
	 * ʱ�䰴ť��Ӧ�¼�
	 * 
	 * @author HCOu
	 * @date 2013-6-18
	 */
	class DatePickerButtnOnclickLisenter implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.datapicker_btn1:
				flag = 1;

				Message msg = new Message();
				msg.what = AppDetailedActivity.SHOW_DATAPICK;
				AppDetailedActivity.this.saleHandler.sendMessage(msg);
				break;
			case R.id.datapicker_btn2:
				flag = 2;

				Message msg2 = new Message();
				msg2.what = AppDetailedActivity.SHOW_DATAPICK;
				AppDetailedActivity.this.saleHandler.sendMessage(msg2);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * �������ڿؼ���Handler
	 */
	Handler saleHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppDetailedActivity.SHOW_DATAPICK:
				showDialog(DATE_DIALOG_ID);
				break;
			}
		}
	};

	/**
	 * 
	 * ���ڿؼ����¼�
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			// ���������
			updateDisplay(flag);
		}
	};

	/**
	 * ��������
	 */
	private void updateDisplay(int disflag) {
		switch (disflag) {
		case 1:
			txt_begintime.setText(new StringBuilder()
					.append(mYear)
					.append(".")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append(".")
					.append((mDay < 10) ? "0" + mDay : mDay));
			break;
		case 2:
			txt_endtime.setText(new StringBuilder()
					.append(mYear)
					.append(".")
					.append((mMonth + 1) < 10 ? "0" + (mMonth + 1)
							: (mMonth + 1)).append(".")
					.append((mDay < 10) ? "0" + mDay : mDay));
			break;
		default:
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	/**
	 * ��ʼ��ʱ����ʾ��
	 * 
	 * @date 2013-6-18
	 */
	private void setDateTime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * ��ӷ��ز˵����˳���ť
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			goToMyCarAppActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * û����onCreate�����е���initBoxWedget()��������onWindowFocusChanged�����е��ã�
	 * ����ΪinitWedget()����Ҫ��ȡPopupWindow���������������������ȣ���onCreate���������޷���ȡ���ÿ�ȵ�
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		while (!initflag) {
			initBoxWedget();
			initflag = true;
		}
	}

	/**
	 * ��ʼ������ؼ�
	 * 
	 * @date 2013-6-19
	 */
	private void initBoxWedget() {
		// ��ʼ��Handler,����������Ϣ
		showBoxHandler = new Handler(AppDetailedActivity.this);
		// ��ʼ���������
		showbox_btn = (Button) findViewById(R.id.showbox_btn);
		// ��ȡ������������������
		int width = txt_addcarname.getWidth();
		pwidth = width;
		// ���õ��������ͷͼƬ�¼����������PopupWindow����������
		showbox_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (initflag) {
					// ��ʾPopupWindow����
					popupWindwShowing();
				}
			}
		});
		// ��ʼ��PopupWindow
		initDatasAndWindow();
	}

	/**
	 * ��ѯ���ݺ͸��´���
	 * 
	 * @date 2013-6-19
	 */
	private void initDatasAndWindow() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				datas = dbUtil.selectAllCars();// ��ѯ��������
				handler.post(new Runnable() {// ���½���
					@Override
					public void run() {
						initPopuWindow();
					}
				});
			}
		}).start();
	}

	/**
	 * ��ʼ��PopupWindow
	 * 
	 * @date 2013-6-19
	 */
	private void initPopuWindow() {
		// PopupWindow���������򲼾�
		View loginwindow = (View) this.getLayoutInflater().inflate(
				R.layout.options, null);
		listView = (ListView) loginwindow.findViewById(R.id.listoptions);

		// �����Զ���Adapter
		optionsAdapter = new OptionsAdapter(this, showBoxHandler, datas);
		listView.setAdapter(optionsAdapter);
		selectPopupWindow = new PopupWindow(loginwindow, pwidth,
				LayoutParams.WRAP_CONTENT, true);
		selectPopupWindow.setOutsideTouchable(true);
		// ��һ����Ϊ��ʵ�ֵ���PopupWindow�󣬵������Ļ�������ּ�Back��ʱPopupWindow����ʧ��
		// û����һ����Ч�����ܳ�������������Ӱ�챳��
		// ���������������ޣ���������ԭ�򣬻������֡�֪����ָ��һ��
		selectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	/**
	 * ��ʾPopupWindow����
	 * 
	 * @date 2013-6-19
	 */
	public void popupWindwShowing() {
		// ��selectPopupWindow��Ϊparent����������ʾ����ָ��selectPopupWindow��Y����������ƫ��3pix��
		// ����Ϊ�˷�ֹ���������ı���֮�������϶��Ӱ���������
		// ���Ƿ�������϶����������϶�Ĵ�С�����ܻ���ݻ��͡�Androidϵͳ�汾��ͬ����ɣ���̫�����
		selectPopupWindow.showAsDropDown(txt_addcarname, 0, 0);
	}

	/**
	 * PopupWindow��ʧ
	 * 
	 * @date 2013-6-19
	 */
	public void dismiss() {
		selectPopupWindow.dismiss();
	}

	/**
	 * ����Hander��Ϣ
	 */
	@Override
	public boolean handleMessage(Message message) {
		Bundle data = message.getData();
		switch (message.what) {
		case 1:
			// ѡ���������������ʧ
			int selIndex = data.getInt("selIndex");
			txt_addcarname.setText(datas.get(selIndex).split("\\$")[0]);
			car_id = datas.get(selIndex).split("\\$")[1];

			dismiss();
			break;
		}
		return false;
	}
}
