package com.gifisan.nio.service;


public interface ServiceAcceptor {

	public abstract void accept(Request request, Response response) throws Exception;

}