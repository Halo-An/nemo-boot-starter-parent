package com.jimistore.boot.nemo.high.concurrency.helper;

public class AsyncExecuter {
	
	public static int DEFAULT_CAPACITY = 5;
	
	int capacity = DEFAULT_CAPACITY;
	
	String name;

	public AsyncExecuter(int capacity, String name) {
		super();
		this.capacity = capacity;
		this.name = name;
	}

	public AsyncExecuter setCapacity(int capacity) {
		this.capacity = capacity;
		return this;
	}
	
	public AsyncExecuter setName(String name) {
		this.name = name;
		return this;
	}

	public void execute(final IExecuter executer){
		try{
			this.consume();
			new Thread(String.format("%s-%s", name, capacity)){

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
