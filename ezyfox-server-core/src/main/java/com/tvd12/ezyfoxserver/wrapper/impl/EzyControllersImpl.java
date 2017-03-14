package com.tvd12.ezyfoxserver.wrapper.impl;

import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfoxserver.constant.EzyCommand;
import com.tvd12.ezyfoxserver.constant.EzyConstant;
import com.tvd12.ezyfoxserver.controller.EzyController;
import com.tvd12.ezyfoxserver.controller.EzyHandShakeController;
import com.tvd12.ezyfoxserver.controller.EzyLoginController;
import com.tvd12.ezyfoxserver.wrapper.EzyControllers;

public class EzyControllersImpl implements EzyControllers {

	@SuppressWarnings("rawtypes")
	protected Map<EzyConstant, EzyController> controllers;
	
	protected EzyControllersImpl(Builder builder) {
		this.controllers = builder.newControllers();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public EzyController getController(EzyConstant cmd) {
		return controllers.get(cmd);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		@SuppressWarnings("rawtypes")
		protected Map<EzyConstant, EzyController> newControllers() {
			Map<EzyConstant, EzyController> answer = new HashMap<>();
			answer.put(EzyCommand.HAND_SHAKE, new EzyHandShakeController());
			answer.put(EzyCommand.LOGIN, new EzyLoginController());
			return answer;
		}
		
		public EzyControllers build() {
			return new EzyControllersImpl(this);
		}
	}

}
