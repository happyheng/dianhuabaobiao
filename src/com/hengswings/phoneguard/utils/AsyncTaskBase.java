package com.hengswings.phoneguard.utils;

import com.hengswings.phoneguard.R;
import com.hengswings.phoneguard.wight.LoadingView;

import android.os.AsyncTask;
import android.view.View;

/**
 * 基础的异步加载的方法，带有一个LoadingView
 * @author liuheng
 *
 */
public class AsyncTaskBase extends AsyncTask<Integer, Integer, Integer> {
	private LoadingView mLoadingView;
	public AsyncTaskBase(LoadingView loadingView){
		this.mLoadingView=loadingView;
	}
	@Override
	protected Integer doInBackground(Integer... params) {

		return null;
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		if(result==1){
			mLoadingView.setVisibility(View.GONE);
		}else{
			mLoadingView.setText(R.string.no_data);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mLoadingView.setVisibility(View.VISIBLE);
	}

}
