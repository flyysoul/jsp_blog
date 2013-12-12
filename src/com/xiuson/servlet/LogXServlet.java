package com.xiuson.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.SQLException;
import java.util.*;

import com.xiuson.dao.IPDao;
import com.xiuson.dao.LogonDao;
import com.xiuson.toolsbean.MyTools;
import com.xiuson.valuebean.IPBean;
import com.xiuson.valuebean.MasterBean;

public class LogXServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public LogXServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getParameter("action");
		if (action == null)
			action = "";
		if (action.equals("islogon"))
			this.isLogon(request, response);
		if (action.equals("logon"))
			this.logon(request, response);
		if (action.equals("logout"))
			this.logout(request, response);

	}

	/**
	 * �ж��Ƿ��Ѿ���½
	 */
	public void isLogon(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		IPDao ipDao = new IPDao();	
		IPBean ipBean = new IPBean();
		
		//ipBean.setHeadersNames(request.getHeaderNames().toString());
		ipBean.setMethod(request.getMethod());
		ipBean.setProtocol(request.getProtocol());
		ipBean.setRealPath(request.getContextPath());//������Ҫ�޸� 
		ipBean.setReferer(request.getHeader("Referer"));
		if (request.getHeader("x-forwarded-for") == null) {   
			  ipBean.setRemoteAddr(request.getRemoteAddr());
			} 
			else {
				ipBean.setRemoteAddr(request.getHeader("x-forwarded-for")   );
			}
		ipBean.setRemoteHost(request.getRemoteHost());
		ipBean.setRequestUrl(request.getRequestURI());
		ipBean.setServerName(request.getServerName());
		ipBean.setServerPath(request.getServletPath());
		ipBean.setServerPort(""+request.getServerPort());
		
		HttpSession session = request.getSession();
		ipBean.setCharacterEncoding(request.getCharacterEncoding());
		ipBean.setQueryString(request.getQueryString());
		ipBean.setPathInfo(request.getPathInfo());
		ipBean.setRemoteUser(request.getRemoteUser());
		ipBean.setAcceptLanguage(request.getHeader("Accept-Language"));
		ipBean.setAcceptEncoding(request.getHeader("Accept-Encoding"));
		ipBean.setUserAgent(request.getHeader("User-Agent"));
		//ipBean.setLastAccessed(request.getHeader("Last-Accessed"));
		ipBean.setLastAccessed(session.getLastAccessedTime()+"");
		
		String sdTime = MyTools.ChangeTime(new Date());
		ipBean.setTime(sdTime);
		ipDao.addIP(ipBean);
		
		String forward = "";
	//	HttpSession session = request.getSession();
		if (session.getAttribute("logoner") != null)
			forward = "/admin/AdminIndex.jsp";
		else
			forward = "/front/logon.jsp";
		response.sendRedirect(forward);
	}

	/**
	 * ��֤������
	 */
	public boolean validateLogon(HttpServletRequest request,
			HttpServletResponse response) {
		boolean mark = true;
		String messages = "";
		String name = request.getParameter("userName");
		String password = request.getParameter("userPass");
		if (name == null || name.equals("")) {
			mark = false;
			messages += "<li>������<b>�û���!</b></li>";
		}
		if (password == null || password.equals("")) {
			mark = false;
			messages += "<li>������<b>����!</b></li>";
		}
		request.setAttribute("messages", messages);
		return mark;
	}

	/**
	 * ��֤���
	 */
	public void logon(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
				
		IPDao ipDao = new IPDao();	
		IPBean ipBean = new IPBean();
		
		//ipBean.setHeadersNames(request.getHeaderNames().toString());
		ipBean.setMethod(request.getMethod());
		ipBean.setProtocol(request.getProtocol());
		ipBean.setRealPath(request.getContextPath());//������Ҫ�޸� 
		ipBean.setReferer(request.getHeader("Referer"));
		if (request.getHeader("x-forwarded-for") == null) {   
			  ipBean.setRemoteAddr(request.getRemoteAddr());
			} 
			else {
				ipBean.setRemoteAddr(request.getHeader("x-forwarded-for")   );
			}
		ipBean.setRemoteHost(request.getRemoteHost());
		ipBean.setRequestUrl(request.getRequestURI());
		ipBean.setServerName(request.getServerName());
		ipBean.setServerPath(request.getServletPath());
		ipBean.setServerPort(""+request.getServerPort());
		
		HttpSession session = request.getSession();
		ipBean.setCharacterEncoding(request.getCharacterEncoding());
		ipBean.setQueryString(request.getQueryString());
		ipBean.setPathInfo(request.getPathInfo());
		ipBean.setRemoteUser(request.getRemoteUser());
		ipBean.setAcceptLanguage(request.getHeader("Accept-Language"));
		ipBean.setAcceptEncoding(request.getHeader("Accept-Encoding"));
		ipBean.setUserAgent(request.getHeader("User-Agent"));
		ipBean.setLastAccessed(session.getLastAccessedTime()+"");	
		String sdTime = MyTools.ChangeTime(new Date());
		ipBean.setTime(sdTime);
		ipDao.addIP(ipBean);	
		boolean flag = validateLogon(request, response);
		RequestDispatcher rd = null;
		if (flag) {
			LogonDao masterDao = new LogonDao();
			MasterBean logoner = new MasterBean();
			logoner.setMasterName(request.getParameter("userName"));
			logoner.setMasterPass(request.getParameter("userPass"));
			boolean mark = masterDao.logon(logoner);// ��֤�û����
			
			System.out.println("test------------------LogXServerlet.logon:mark:"+mark);
			if (!mark) { // �����ڸ��û�
				System.out.println("test------------------LogXServerlet.logon:" +
						"if�����ڸ��û�,logoner.masterName="+logoner.getMasterName()+"logoner.pass="+logoner.getMasterPass());
				request.setAttribute("messages", "<li>������û������������!</li>");
				rd = request.getRequestDispatcher("front/logon.jsp");
				rd.forward(request, response);
			} else {
				System.out.println("test------------------LogXServerlet.logon:else���ڸ��û�");
				
				int begin ;
				int count;
				String beginString = request.getParameter("ipBegin");//��ҳ��ʾʱ�ĵڼ�ҳ
				String countString = request.getParameter("ipCount");//ÿҳ��������
				if(beginString == null)
					begin = 1;
				else 
					begin= Integer.parseInt(beginString);
				if(countString == null)
					count = 10;
				else
					count = Integer.parseInt(countString);

				List ipList = null;
				int ipSum = 0;
				try {
					ipList = ipDao.queryIP(begin, count);
					ipSum = ipDao.queryIPSum();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				session.setAttribute("ipList", ipList);//��ȡȫ��ip��¼
				session.setAttribute("ipSum", ipSum);				
				session.setAttribute("logoner", logoner);// ����ǰ��½���û�ע�ᵽsession�е�logoner������
				
				ipDao.getDB().close();
				response.sendRedirect("admin/AdminIndex.jsp");
			}
		} else { // Ϊ��
			rd = request.getRequestDispatcher("/front/logon.jsp");
			rd.forward(request, response);
		}
	}

	/**
	 * �˳���½
	 */
	public void logout(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		IPDao ipDao = new IPDao();	
		IPBean ipBean = new IPBean();
		
		//ipBean.setHeadersNames(request.getHeaderNames().toString());
		ipBean.setMethod(request.getMethod());
		ipBean.setProtocol(request.getProtocol());
		ipBean.setRealPath(request.getContextPath());//������Ҫ�޸� 
		ipBean.setReferer(request.getHeader("Referer"));
		if (request.getHeader("x-forwarded-for") == null) {   
			  ipBean.setRemoteAddr(request.getRemoteAddr());
			} 
			else {
				ipBean.setRemoteAddr(request.getHeader("x-forwarded-for")   );
			}
		ipBean.setRemoteHost(request.getRemoteHost());
		ipBean.setRequestUrl(request.getRequestURI());
		ipBean.setServerName(request.getServerName());
		ipBean.setServerPath(request.getServletPath());
		ipBean.setServerPort(""+request.getServerPort());
		
		HttpSession session = request.getSession();
		ipBean.setCharacterEncoding(request.getCharacterEncoding());
		ipBean.setQueryString(request.getQueryString());
		ipBean.setPathInfo(request.getPathInfo());
		ipBean.setRemoteUser(request.getRemoteUser());
		ipBean.setAcceptLanguage(request.getHeader("Accept-Language"));
		ipBean.setAcceptEncoding(request.getHeader("Accept-Encoding"));
		ipBean.setUserAgent(request.getHeader("User-Agent"));
		ipBean.setLastAccessed(session.getLastAccessedTime()+"");
		
		String sdTime = MyTools.ChangeTime(new Date());
		ipBean.setTime(sdTime);
		ipDao.addIP(ipBean);
		
		//HttpSession session = request.getSession();
		session.removeAttribute("logoner");
		RequestDispatcher rd = request.getRequestDispatcher("/front/FrontIndex.jsp");
		rd.forward(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
