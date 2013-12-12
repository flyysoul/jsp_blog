package com.xiuson.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.SQLException;
import java.util.*;

import com.xiuson.dao.ArticleDao;
import com.xiuson.dao.FriendDao;
import com.xiuson.dao.IPDao;
import com.xiuson.dao.WordDao;
import com.xiuson.toolsbean.MyTools;
import com.xiuson.valuebean.IPBean;
import com.xiuson.valuebean.MasterBean;

/**
 * 
 * @author Administrator ��Servlet���ղ����������ҳ�������
 */
public class IndexServlet extends HttpServlet {

	private static MasterBean masterBean;

	/**
	 * Constructor of the object.
	 */
	public IndexServlet() {
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
		IPDao ipDao = new IPDao();	
		IPBean ipBean = new IPBean();

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
		/*********************��ȡ����ҳ���������ʾ���е�����****************/
		ArticleDao articleDao = new ArticleDao(); 

		int [] percent = new int[3];
		double sum = 0;
		String myArticlePercent = "",myFriendPercent = "", myWordPercent = "";
		
			try {
				percent = ipDao.queryIPPercent();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		for(int i = percent.length-1 ;i>=0;i--) {
			System.out.println("test---------------------->IndexServlet.percent[]"+percent[i]);
			sum+=percent[i];
		}
		myArticlePercent = String.format("%.2f",percent[0]*100.0/sum ) ;
		myFriendPercent = String.format("%.2f",percent[1]*100.0/sum ) ;
		myWordPercent = String.format("%.2f",percent[2]*100.0/sum ) ;
			
		session.setAttribute("myArticlePercent", myArticlePercent);
		session.setAttribute("myFriendPercent", myFriendPercent);
		session.setAttribute("myWordPercent", myWordPercent);
		
			
		//��tb_article���ݱ��л�ȡǰ3ƪ����
		List articleList = articleDao.queryArticle(-1, null);//����������𣬲�ѯǰ3ƪ����
		request.setAttribute("articleList", articleList);

		WordDao wordDao = new WordDao();	
		int begin= 1 ;
		int count= 3;
		
		List iWordList = wordDao.queryWord(begin,count);//��ȡ��������
		int wordSum = wordDao.queryWordSum();

		session.setAttribute("wordSum", wordSum);
		session.setAttribute("iWordList", iWordList);
		
		FriendDao friendDao = new FriendDao();
		List iFriendList = friendDao.queryFriend(begin,count);
		session.setAttribute("iFriendList", iFriendList);
		
		//ipDao.getDB().close();
		wordDao.getDB().close();//�ر����ݿ�����
		friendDao.getDB().close();
		articleDao.getDB().close();
		
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
