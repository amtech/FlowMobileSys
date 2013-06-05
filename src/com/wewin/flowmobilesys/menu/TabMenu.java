package com.wewin.flowmobilesys.menu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class TabMenu extends PopupWindow {
	private GridView gridView;
	private LinearLayout mLayout;

	public TabMenu(Context context, OnItemClickListener bodyClick,
			int colorBgTabMenu) {
		super(context);

		mLayout = new LinearLayout(context);
		mLayout.setOrientation(LinearLayout.VERTICAL);

		gridView = new GridView(context);
		gridView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));// ѡ�е�ʱ��Ϊ͸��ɫ
		gridView.setNumColumns(4);
		gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		gridView.setVerticalSpacing(0);
		gridView.setHorizontalSpacing(0);
		gridView.setPadding(0, 0, 0, 0);
		gridView.setGravity(Gravity.CENTER);
		gridView.setOnItemClickListener(bodyClick);
		mLayout.addView(gridView);

		// ����Ĭ����
		this.setContentView(mLayout);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setBackgroundDrawable(new ColorDrawable(colorBgTabMenu));// ����TabMenu�˵�����
		this.setFocusable(true);// menu�˵���ý��� ���û�л�ý���menu�˵��еĿؼ��¼��޷���Ӧ
	}

	public void SetBodySelect(int index, int colorSelBody) {
		int count = gridView.getChildCount();
		for (int i = 0; i < count; i++) {
			if (i != index)
				((LinearLayout) gridView.getChildAt(i))
						.setBackgroundColor(Color.TRANSPARENT);
		}
		((LinearLayout) gridView.getChildAt(index))
				.setBackgroundColor(colorSelBody);
	}

	public void SetBodyAdapter(MenuBodyAdapter bodyAdapter) {
		gridView.setAdapter(bodyAdapter);
	}

	/**
	 * �Զ���Adapter��TabMenu��ÿ����ҳ������
	 * 
	 */
	static public class MenuBodyAdapter extends BaseAdapter {
		private Context mContext;
		private int[] resID;

		/**
		 * ����TabMenu�ķ�ҳ����
		 * 
		 * @param context
		 *            ���÷���������
		 * @param resID
		 */
		public MenuBodyAdapter(Context context, int[] resID) {
			this.mContext = context;
			this.resID = resID;
		}

		@Override
		public int getCount() {
			return resID.length;
		}

		public Object getItem(int position) {
			return makeMenyBody(position);
		}

		public long getItemId(int position) {
			return position;
		}

		private LinearLayout makeMenyBody(int position) {
			LinearLayout result = new LinearLayout(this.mContext);
			result.setOrientation(LinearLayout.VERTICAL);
			result.setGravity(Gravity.CENTER_HORIZONTAL
					| Gravity.CENTER_VERTICAL);
			result.setPadding(0, 0, 0, 0);

			ImageView img = new ImageView(this.mContext);
			img.setBackgroundResource(resID[position]);
			result.addView(img, new LinearLayout.LayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)));
			return result;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			return makeMenyBody(position);
		}
	}
}