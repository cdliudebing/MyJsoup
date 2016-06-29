package com.jdrx.phone.util;

public class Single {
	private Single single = new Single();
	private Single(){
		synchronized(this){
			if(single == null){
				single = new Single();
			}
		}
	}
	public Single getSingle(){
		return this.single;
	}
}
