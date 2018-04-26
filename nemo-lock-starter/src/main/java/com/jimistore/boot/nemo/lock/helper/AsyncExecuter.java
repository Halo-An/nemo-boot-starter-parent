package com.jimistore.boot.nemo.lock.helper;

public class AsyncExecuter {
	
	public static int DEFAULT_CAPACITY = 5;
	
	private Integer capacity = DEFAULT_CAPACITY;
	
	String name;

	public AsyncExecuter(Integer capacity, String name) {
		super();
		this.capacity = capacity;
		this.name = name;
	}

	public AsyncExecuter setCapacity(Integer capacity) {
		this.capacity = capacity;
		return this;
	}
	
	public AsyncExecuter setName(String name) {
		this.name = name;
		return this;
	}

	public void execute(final IExecuter executer){
			this.consume();
			new Thread(String.format("%s-%s", name, capacity)){

				@Override
				public void run() {
					try{
						executer.execute();
					}finally{
						produce();
					}
				}
				
			}.start();
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
