package com.jimistore.boot.nemo.id.generator.core;

public class IDGenerator implements IIDGenerator {

	@Override
	public String generator(String seq, int length, long num) {
		StringBuilder id = new StringBuilder();
		char[] chars = seq.toCharArray();
		int size = chars.length;
		long diff = 0;
		for(int i=0; i<length; i++) {
			diff = diff * size + 1;
		}
		long remain = diff * num;
		for(int i=length; i>0; i--) {
			int cur = (int)Math.pow(size, (i-1));
			int index = (int) remain / cur;
			id.append(chars[ index % size ]);
			remain = remain % cur;
		}
		
		return id.toString();
	}

	public long parseNumber(String seq, String id) {
		char[] chars = seq.toCharArray();
		int length = id.length();
		int size = chars.length;
		int diff = 0;
		for(int i=0; i<length; i++) {
			diff = diff * size + 1;
		}
		char[] ids = id.toCharArray();
		long remain = 0;
		for(int j=0;j<length;j++) {
			char ch = ids[length-j-1];
			int index = 0;
			for(int i=0;i<size;i++) {
				if(ch == chars[i]) {
					index = i;
					break;
				}
			}
			int cur = (int)Math.pow(size, j);

			if(j==length-1) {
				while((index * cur + remain) % diff!=0) {
					index = index + size;
				}
			}
			
			remain = index * cur + remain;
		}
		return remain / diff;
	}

}
