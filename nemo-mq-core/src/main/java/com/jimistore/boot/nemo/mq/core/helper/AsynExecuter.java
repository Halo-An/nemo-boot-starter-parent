package com.jimistore.boot.nemo.mq.core.helper;

public class AsynExecuter {
	
	public static int DEFAULT_CAPACITY = 5;
	
	int capacity = DEFAULT_CAPACITY;

	public AsynExecuter setCapacity(int capacity) {
		this.capacity = capacity;
		return this;
	}
	
	public void execute(final IExecuter executer){
		try{
			this.consume();
			new Thread(String.format("AsynExecuter-%s", capacity)){

				@Override
				public void run() {
					executer.execute();
				}
				
			}.start();
		}finally{
			this.produce();
		}
	}
	
	public synchronized void consume(){
		if(capacity<=0){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		capacity--;
	}
	
	public synchronized void produce(){
		capacity++;
		this.notifyAll();
	}
	
	

}
