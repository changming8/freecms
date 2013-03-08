package cn.freeteam.cms.action.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


import cn.freeteam.base.BaseAction;
import cn.freeteam.cms.model.Site;
import cn.freeteam.cms.service.ChannelService;
import cn.freeteam.cms.service.InfoService;
import cn.freeteam.cms.service.SiteService;
import cn.freeteam.cms.service.TempletService;
import cn.freeteam.cms.util.FreeMarkerUtil;
import cn.freeteam.util.OperLogUtil;


import freemarker.template.TemplateModelException;

/** 
 * <p>Title: TempletAction.java</p>
 * 
 * <p>Description: TempletAction</p>
 * 
 * <p>Date: May 24, 2012</p>
 * 
 * <p>Time: 12:16:44 PM</p>
 * 
 * <p>Copyright: 2011</p>
 * 
 * <p>Company: freeteam</p>
 * 
 * @author freeteam
 * @version 1.0
 * 
 * <p>============================================</p>
 * <p>Modification History
 * <p>Mender: </p>
 * <p>Date: </p>
 * <p>Reason: </p>
 * <p>============================================</p>
 */
public class TempletAction extends BaseAction{

	private SiteService siteService;
	private TempletService templetService;
	private ChannelService channelService;
	private InfoService infoService;
	
	private String siteid;
	private String templetPath;
	
	public TempletAction(){
		init("siteService","templetService");
	}
	
	/**
	 * 获取数据处理模板并装处理结果以页面形式显示为用户
	 * @return
	 * @throws IOException 
	 * @throws TemplateModelException 
	 */
	public String pro() throws TemplateModelException, IOException{
		if (siteid!=null && siteid.trim().length()>0
				&& templetPath!=null && templetPath.trim().length()>0) {
			//查询站点
			Site site=siteService.findById(siteid);
			if (site!=null && site.getIndextemplet()!=null 
					&& site.getIndextemplet().trim().length()>0) {
				//生成静态页面
				Map<String,Object> data=new HashMap<String,Object>();
				//传递site参数
				data.put("site", site);
				data.put("contextPath", getContextPath());
				data.put("request_remoteAddr", getHttpRequest().getRemoteAddr());
				//获取参数并放入data
				Enumeration<String> paramNames=getHttpRequest().getParameterNames();
				if (paramNames!=null && paramNames.hasMoreElements()) {
					String name;
					while (paramNames.hasMoreElements()) {
						name=paramNames.nextElement();
						if (name!=null &&
								!name.equals("site") &&
								!name.equals("contextPath")&&
								!name.equals("currChannelid")&&
								!name.equals("currInfoid")) {
							data.put(name, getHttpRequest().getParameter(name));
						}
					}
				}
				//如果有currChannelid参数则传递currChannel对象
				if (getHttpRequest().getParameter("currChannelid")!=null && getHttpRequest().getParameter("currChannelid").trim().length()>0) {
					init("channelService");
					data.put("currChannel",channelService.findById(getHttpRequest().getParameter("currChannelid")));
				}
				//如果有currInfoid参数则传递currInfo对象
				if (getHttpRequest().getParameter("currInfoid")!=null && getHttpRequest().getParameter("currInfoid").trim().length()>0) {
					init("infoService");
					data.put("currInfo",infoService.findById(getHttpRequest().getParameter("currInfoid")));
				}
				//获取seesion中存放的变量
				Enumeration<String> sessionNames=getHttpSession().getAttributeNames();
				if (sessionNames!=null && sessionNames.hasMoreElements()) {
					String name;
					while (sessionNames.hasMoreElements()) {
						name=sessionNames.nextElement();
						if (name!=null) {
							//session变量名称改为session_变量名，避免重名
							data.put("session_"+name, getHttpSession().getAttribute(name));
						}
					}
				}
				templetPath="templet/"+site.getIndextemplet().trim()+"/"+templetPath;
				getHttpResponse().setCharacterEncoding("UTF-8");
				FreeMarkerUtil.createWriter(getServletContext(), data, templetPath, getHttpResponse().getWriter());
			}
		}
		return null;
	}

	public String getSiteid() {
		return siteid;
	}

	public void setSiteid(String siteid) {
		this.siteid = siteid;
	}

	public String getTempletPath() {
		return templetPath;
	}

	public void setTempletPath(String templetPath) {
		this.templetPath = templetPath;
	}

	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public TempletService getTempletService() {
		return templetService;
	}

	public void setTempletService(TempletService templetService) {
		this.templetService = templetService;
	}

	public ChannelService getChannelService() {
		return channelService;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	public InfoService getInfoService() {
		return infoService;
	}

	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
	}
}
