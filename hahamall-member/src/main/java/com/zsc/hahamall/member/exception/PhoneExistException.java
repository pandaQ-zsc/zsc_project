package com.zsc.hahamall.member.exception;

/**
 * <p>Title: PhoneExistException</p>
 * Description：
 * date：2021/6/25 19:17
 */
public class PhoneExistException extends RuntimeException {
	public PhoneExistException() {
		super("手机号存在");
	}
}
