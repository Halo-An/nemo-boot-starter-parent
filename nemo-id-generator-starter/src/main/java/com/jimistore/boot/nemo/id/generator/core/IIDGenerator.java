package com.jimistore.boot.nemo.id.generator.core;

public interface IIDGenerator {
	
	/**
	 * 生成id
	 * @param seq 字符序列
	 * @param length 长度
	 * @param num 序号
	 * @return
	 */
	public String generator(String seq, int length, long num);

}
