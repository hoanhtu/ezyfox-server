package com.tvd12.ezyfoxserver.builder.impl;

import java.net.InetSocketAddress;

import com.tvd12.ezyfoxserver.EzyBootstrap;
import com.tvd12.ezyfoxserver.EzyServer;
import com.tvd12.ezyfoxserver.EzyServerBootstrap;
import com.tvd12.ezyfoxserver.builder.EzyServerBootstrapBuilder;
import com.tvd12.ezyfoxserver.codec.EzyCodecCreator;
import com.tvd12.ezyfoxserver.creator.EzyDataHandlerCreator;
import com.tvd12.ezyfoxserver.creator.impl.EzyDataHandlerCreatorImpl;
import com.tvd12.ezyfoxserver.wrapper.EzyControllers;
import com.tvd12.ezyfoxserver.wrapper.EzySessionManager;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EzyServerBootstrapBuilderImpl implements EzyServerBootstrapBuilder {

	private int port;
	private EzyServer boss;
	private EventLoopGroup childGroup;
	private EventLoopGroup parentGroup;
	private EzyBootstrap localBootstrap;
	private EzyCodecCreator codecCreator;
	
	/*
	 * (non-Javadoc)
	 * @see com.tvd12.ezyfoxserver.builder.EzyServerBootstrapBuilder#port(int)
	 */
	@Override
	public EzyServerBootstrapBuilder port(int port) {
		this.port = port;
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.tvd12.ezyfoxserver.builder.EzyServerBootstrapBuilder#boss(com.tvd12.ezyfoxserver.EzyServer)
	 */
	@Override
	public EzyServerBootstrapBuilder boss(EzyServer boss) {
		this.boss = boss;
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.tvd12.ezyfoxserver.builder.EzyServerBootstrapBuilder#childGroup(io.netty.channel.EventLoopGroup)
	 */
	@Override
	public EzyServerBootstrapBuilder childGroup(EventLoopGroup childGroup) {
		this.childGroup = childGroup;
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.tvd12.ezyfoxserver.builder.EzyServerBootstrapBuilder#parentGroup(io.netty.channel.EventLoopGroup)
	 */
	@Override
	public EzyServerBootstrapBuilder parentGroup(EventLoopGroup parentGroup) {
		this.parentGroup = parentGroup;
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.tvd12.ezyfoxserver.builder.EzyServerBootstrapBuilder#localBootstrap(com.tvd12.ezyfoxserver.EzyBootstrap)
	 */
	@Override
	public EzyServerBootstrapBuilder localBootstrap(EzyBootstrap localBootstrap) {
		this.localBootstrap = localBootstrap;
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.tvd12.ezyfoxserver.builder.EzyServerBootstrapBuilder#codecCreator(com.tvd12.ezyfoxserver.codec.EzyCodecCreator)
	 */
	@Override
	public EzyServerBootstrapBuilder codecCreator(EzyCodecCreator codecCreator) {
		this.codecCreator = codecCreator;
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.tvd12.ezyfoxserver.builder.EzyBuilder#build()
	 */
	@Override
	public EzyServerBootstrap build() {
		EzyServerBootstrap answer = new EzyServerBootstrap();
		answer.setChildGroup(childGroup);
		answer.setParentGroup(parentGroup);
		answer.setLocalBootstrap(localBootstrap);
		answer.setServerBootstrap(createServerBootstrap());
		return answer;
	}
	
	protected ServerBootstrap createServerBootstrap() {
		return newServerBootstrap()
				.group(parentGroup, childGroup)
				.channel(NioServerSocketChannel.class)
				.localAddress(new InetSocketAddress(port))
				.childHandler(newChannelInitializer());
	}
	
	private ChannelInitializer<Channel> newChannelInitializer() {
		return EzyChannelInitializer.builder()
				.codecCreator(codecCreator)
				.dataHandlerCreator(newDataHandlerCreator())
				.build();
	}
	
	private EzyDataHandlerCreator newDataHandlerCreator() {
		return EzyDataHandlerCreatorImpl.builder()
				.controllers(getControllers())
				.sessionManager(getSessionManager())
				.build();
	}
	
	private EzyControllers getControllers() {
		return boss.getControllers();
	}
	
	private EzySessionManager getSessionManager() {
		return boss.getManagers().getManager(EzySessionManager.class);
	}
	
	private ServerBootstrap newServerBootstrap() {
		return new ServerBootstrap() {
			@Override
			public ServerBootstrap group(EventLoopGroup parentGroup, EventLoopGroup childGroup) {
				return childGroup != null 
						? super.group(parentGroup, childGroup) 
						: super.group(parentGroup);
			}
		};
	}
}

class EzyChannelInitializer extends ChannelInitializer<Channel> {
	
	private EzyCodecCreator codecCreator;
	private EzyDataHandlerCreator dataHandlerCreator;
	
	protected EzyChannelInitializer(Builder builder) {
		this.codecCreator = builder.codecCreator;
		this.dataHandlerCreator = builder.dataHandlerCreator;
	}
	
	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(codecCreator.newDecoder());
		pipeline.addLast(dataHandlerCreator.newHandler());
		pipeline.addLast(codecCreator.newEncoder());
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private EzyCodecCreator codecCreator;
		private EzyDataHandlerCreator dataHandlerCreator;
		
		public Builder codecCreator(EzyCodecCreator codecCreator) {
			this.codecCreator = codecCreator;
			return this;
		}
		
		public Builder dataHandlerCreator(EzyDataHandlerCreator dataHandlerCreator) {
			this.dataHandlerCreator = dataHandlerCreator;
			return this;
		}
		
		public EzyChannelInitializer build() {
			return new EzyChannelInitializer(this);
		}
		
	}
}


