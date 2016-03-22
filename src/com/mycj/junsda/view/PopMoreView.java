package com.mycj.junsda.view;

import com.mycj.junsda.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

public class PopMoreView {
	private PopupWindow pop;
	private LinearLayout llShare;
	private LinearLayout llDelete;

	public PopMoreView build(Context context ,final OnPopClickListener mOnPopClickListener) {
		View popview = LayoutInflater.from(context).inflate(R.layout.view_pop, null);
		pop = new PopupWindow(popview, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		pop.setTouchable(true);
		pop.setFocusable(true);
		pop.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_pop_trans));
		pop.setOutsideTouchable(true);
		llShare = (LinearLayout) popview.findViewById(R.id.ll_share);
		llDelete = (LinearLayout) popview.findViewById(R.id.ll_delete);
		llShare.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mOnPopClickListener!=null) {
					mOnPopClickListener.onShareClick(v);
				}
				if (pop!=null && pop.isShowing()) {
					pop.dismiss();
				}
				
			}
		});
		llDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mOnPopClickListener!=null) {
					mOnPopClickListener.onDeleteClick(v);
				}
				if (pop!=null && pop.isShowing()) {
					pop.dismiss();
				}
			}
		});
	
		return this;
	}

	
	public interface OnPopClickListener{
		public void onShareClick(View v);
		public void onDeleteClick(View v);
	}
	
	
	public void  setOnDismissListener(OnDismissListener l) {
		pop.setOnDismissListener(l);
	}

	public void showAsDropDown(View v, int dx, int dy) {
		pop.showAsDropDown(v, dx, dy);
	}

}
