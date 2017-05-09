package com.tvd12.ezyfoxserver.event.impl;

import com.tvd12.ezyfoxserver.builder.EzyBuilder;
import com.tvd12.ezyfoxserver.entity.EzySession;
import com.tvd12.ezyfoxserver.event.EzyEvent;
import com.tvd12.ezyfoxserver.event.EzySessionEvent;

import lombok.Getter;

@Getter
public class EzySimpleSessionEvent implements EzySessionEvent {

    protected EzySession session;
    
    public static class Builder<B extends Builder<B>> implements EzyBuilder<EzyEvent> {
    
        protected EzySession session;
        
        @SuppressWarnings("unchecked")
        public B session(EzySession session) {
            this.session = session;
            return (B)this;
        }
        
        @Override
        public EzyEvent build() {
            EzySimpleSessionEvent answer = newProduct();
            answer.session = session;
            return answer;
        }
        
        protected EzySimpleSessionEvent newProduct() {
            return new EzySimpleSessionEvent();
        }
    }
    
}
