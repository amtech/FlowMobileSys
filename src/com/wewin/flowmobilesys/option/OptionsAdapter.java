package com.wewin.flowmobilesys.option;

import java.util.ArrayList;
import com.wewin.flowmobilesys.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * �Զ���������Adapter
 * 
 * @author HCOU
 * @date 2013-6-19
 */
public class OptionsAdapter extends BaseAdapter {

	private ArrayList<String> list = new ArrayList<String>();
	private Activity activity = null;
	private Handler handler;

	/**
	 * �Զ��幹�췽��
	 * 
	 * @param activity
	 * @param handler
	 * @param list
	 */
	public OptionsAdapter(Activity activity, Handler handler,
			ArrayList<String> list) {
		this.activity = activity;
		this.handler = handler;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			// �������
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.option_item, null);
			holder.textView = (TextView) convertView
					.findViewById(R.id.item_text);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String[] strs = list.get(position).split("\\$");
		holder.textView.setText(new String(strs[0]));

		// Ϊ������ѡ�����ֲ��������¼�������Ч���ǵ������������䵽�ı���
		holder.textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				Bundle data = new Bundle();
				// ����ѡ������
				data.putInt("selIndex", position);
				msg.setData(data);
				msg.what = 1;
				// ������Ϣ
				handler.sendMessage(msg);
			}
		});
		return convertView;
	}
}

class ViewHolder {
	TextView IdView;
	TextView textView;
}
