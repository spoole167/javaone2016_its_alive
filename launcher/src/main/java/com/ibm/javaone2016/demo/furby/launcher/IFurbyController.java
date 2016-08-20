package com.ibm.javaone2016.demo.furby.launcher;

public interface IFurbyController {

	void sleep() throws Exception;

	void wake() throws Exception;

	void say(String asString) throws Exception;

}
