package com.example.myweiboapp.support.lib;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.ContactsContract.Data;

public abstract class MyAsyncTask<Params, Progress, Result> {

	private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;
	private final WorkerRunnable<Params, Result> mWorker;
	private final AtomicBoolean mTaskInvoked = new AtomicBoolean();
	private static final InternalHandler sHandler = new InternalHandler();
	private final FutureTask<Result> mFuture;
	private volatile Status mStatus = Status.PENDING;
	
	public MyAsyncTask(){
		mWorker = new WorkerRunnable<Params, Result>(){

			public Result call() throws Exception {
				mTaskInvoked.set(true);				
				android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);//设置线程的优先级为低优先级
				return postResult(doInBackground(mParams));
			}
		};
		
		mFuture = new FutureTask<Result>(mWorker){

			@Override
			protected void done() {
			
				try {
					final Result result = get();
					postResultIfNotInvoked(result);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				} catch (ExecutionException e) {
					
					e.printStackTrace();
				}
				
			}
			
		};

	}

	private void postResultIfNotInvoked(Result result) {
		final boolean wasTaskInvoked = mTaskInvoked.get();
		if (!wasTaskInvoked) {
			postResult(result);
		}
	}

	private static abstract class WorkerRunnable<Params, Result> implements Callable<Result>{
		Params[] mParams;
	}//class end:WorkerRunnable
	
	private static class InternalHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			AsyncTaskResult result = (AsyncTaskResult) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
		}
		
	}
	private Result postResult(Result result) {
		 Message message = sHandler.obtainMessage(MESSAGE_POST_RESULT,
	                new AsyncTaskResult<Result>(this, result));
	     message.sendToTarget();
	     return result;
	}
	
	

	public void finish(Result result) {
		if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        setmStatus(Status.FINISHED);
		
	}

	protected void onPostExecute(Result result) {
	}

	protected void onCancelled(Result result) {
		onCancelled();
	}
	protected void onCancelled() {
		
	}
	protected void onProgressUpdate(Progress...params) {

	}

	private boolean isCancelled() {
		return mFuture.isCancelled();
	}

	@SuppressWarnings("hiding")
	private static class AsyncTaskResult<Data>{
		final MyAsyncTask mTask;
		final Data[] mData;
		AsyncTaskResult(MyAsyncTask mTask, Data...data){
			this.mTask = mTask;
			this.mData = data;
		}
	}
	
	protected abstract Result doInBackground(Params... params);

	public Status getmStatus() {
		return mStatus;
	}

	private void setmStatus(Status mStatus) {
		this.mStatus = mStatus;
	}

	public enum Status {
		/**
		 * Indicates that the task has not been executed yet.
		 */
		PENDING,
		/**
		 * Indicates that the task is running.
		 */
		RUNNING,
		/**
		 * Indicates that {@link AsyncTask#onPostExecute} has finished.
		 */
		FINISHED,
	}//class end: Status
}
