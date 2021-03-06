package com.tmall.asshole.engine;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

import com.tmall.asshole.common.Event;
import com.tmall.asshole.common.EventContext;
import com.tmall.asshole.common.LoggerInitUtil;


/**
 *
 * @author tangjinou (jiuxian.tjo)
 *
 */
public class EventEngine implements IEngine<Event,EventContext> {
	private final static Log logger = LoggerInitUtil.LOGGER;

	@Autowired
	private IHandlerLocator<Event, EventContext> handlerLocator;

	public void setHandlerLocator(IHandlerLocator<Event, EventContext> handlerLocator) {
		this.handlerLocator = handlerLocator;
	}

	public void init(){

	}


	public boolean fire(Event event, EventContext context) throws Exception {
		boolean flg = false;
		if (event != null) {
			String eventName = event.getClass().getName();
			IHandler handler = handlerLocator.lookup(eventName);

			try {
			  // PowerEventEngine 不是一般的 EventEngine ， 需要用AbstractHandler
			  if(!(handler instanceof AbstractHandler)){
				  context.setProcessLogs("高级的PowerEventEngine 使用不了哦~"+handler.getClass()+"不是继承AbstractHandler");
				  return false;
			  }
			    AbstractHandler abstractHandler = (AbstractHandler) handler;
			    boolean before = abstractHandler.beforeHandle(event, context);
			    boolean now = abstractHandler.handle(event, context);
			    boolean after = abstractHandler.afterHandle(event, context);
				flg = before && now && after;
			} catch (Exception e) {
				logger.debug("Event process error name:" + eventName + " content:" + event.toString(), e);
				throw e;
			}
		}
		return flg;
	}

}
