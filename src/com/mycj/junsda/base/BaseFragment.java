package com.mycj.junsda.base;




import android.graphics.Typeface;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment{
	
//	 /**
//		 * 字体:简体方正
//		 * @return
//		 */
//		public Typeface getTypefaceForJianti(){
//			 BaseApp app = (BaseApp)(getActivity().getApplication());
//			return app.getTypefaceForJianti();
//		}
//		/**
//		 * 字体:繁体方正
//		 * @return
//		 */
//		public Typeface getTypefaceForFanti(){
//			 BaseApp app = (BaseApp)(getActivity().getApplication());
//			return app.getTypefaceForFanti();
//		}
//		/**
//		 * 字体:数字字母 方正
//		 * @return
//		 */
//		public Typeface getTypefaceForNumber(){
//			 BaseApp app = (BaseApp)(getActivity().getApplication());
//			return app.getTypefaceForNumber();
//		}
	
	public boolean isConnected(){
	        BaseApp app  = (BaseApp) getActivity().getApplication();
	        return app.getXBlueService()!=null && app.getXBlueService().isAllConnected();
	}
}
