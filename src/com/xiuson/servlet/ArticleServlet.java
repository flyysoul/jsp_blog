package com.xiuson.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

import com.xiuson.dao.*;
import com.xiuson.toolsbean.MyTools;
import com.xiuson.valuebean.*;

public class ArticleServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public ArticleServlet() {
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
		if (action == null) {
			action = "";
		}

		// System.out.println("test-------------------------------------------_ArticleServlet.doPost.read");
		if (action.equals("read"))// ��x����
			this.readArticle(request, response);

		if (action.equals("followAdd"))// �l�����»ظ�
			this.validateFollow(request, response);

		if (action.equals("all")) { // ��ҳ��ʾ��������
			this.Articles(request, response);
		}
		if (action.equals("addArticle")) { // ת�����������ҳ��
			this.addArticles(request, response);
		}
		if (action.equals("addArticleConfig")) { // ȷ���������
			this.addArticlesConfig(request, response);
		}

	}

	// �l�����»ظ�
	public void validateFollow(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		IPDao ipDao = new IPDao();
		IPBean ipBean = new IPBean();
		// ipBean.setHeadersNames(request.getHeaderNames().toString());
		ipBean.setMethod(request.getMethod());
		ipBean.setProtocol(request.getProtocol());
		ipBean.setRealPath(request.getContextPath());// ������Ҫ�޸�
		ipBean.setReferer(request.getHeader("Referer"));
		if (request.getHeader("x-forwarded-for") == null) {
			ipBean.setRemoteAddr(request.getRemoteAddr());
		} else {
			ipBean.setRemoteAddr(request.getHeader("x-forwarded-for"));
		}
		ipBean.setRemoteHost(request.getRemoteHost());
		ipBean.setRequestUrl(request.getRequestURI());
		ipBean.setServerName(request.getServerName());
		ipBean.setServerPath(request.getServletPath());
		ipBean.setServerPort("" + request.getServerPort());
		String sdTime = MyTools.ChangeTime(new Date());
		ipBean.setTime(sdTime);
		ipDao.addIP(ipBean);
		ipDao.getDB().close();// �ر����ݿ�����

		String strId = request.getParameter("articleId");
		int articleId = MyTools.strToint(strId);
		String content = request.getParameter("content");
		String author = request.getParameter("author");

		if (content != null)
			content = new String(content.getBytes("ISO-8859-1"), "UTF8");// ������ת��
		if (author != null)
			author = new String(author.getBytes("ISO-8859-1"), "UTF8");// ������ת��

		ReviewBean reviewBean = new ReviewBean();
		reviewBean.setArticleId(articleId);
		reviewBean.setAuthor(author);
		reviewBean.setContent(content);
		reviewBean.setSdTime(sdTime);
		ReviewDao reviewDao = new ReviewDao();
		reviewDao.followAdd(reviewBean);
		HttpSession session = request.getSession();
		ArticleBean articleBean = new ArticleBean();
		articleBean.setId(articleId);

		ArticleDao articleDao = new ArticleDao();
		articleDao.operationArticle("readTimes", articleBean);// �ۼ���x�Δ�
		articleBean = articleDao.queryArticleSingle(articleId);// �@ȡָ�����µă���
		session.setAttribute("readSingle", articleBean);// ����article��request��

		int begin;
		int count;
		String beginString = request.getParameter("reviewBegin");// ��ҳ��ʾʱ�ĵڼ�ҳ
		String countString = request.getParameter("reviewCount");// ÿҳ��������
		if (beginString == null)
			begin = 1;
		else
			begin = Integer.parseInt(beginString);
		if (countString == null)
			count = 10;
		else
			count = Integer.parseInt(countString);

		List reviewList = reviewDao.queryReview(articleId, begin, count); // �@ȡָ�����µ��uՓ
		session.setAttribute("reviewList", reviewList);
		System.out
				.println("test-------------------------------------------_ArticleServlet.readArticle");
		RequestDispatcher rd = request
				.getRequestDispatcher("/front/article/ArticleSingle.jsp");
		rd.forward(request, response);
	}

	public void Articles(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		IPDao ipDao = new IPDao();
		IPBean ipBean = new IPBean();
		ipBean.setMethod(request.getMethod());
		ipBean.setProtocol(request.getProtocol());
		ipBean.setRealPath(request.getContextPath());// ������Ҫ�޸�
		ipBean.setReferer(request.getHeader("Referer"));
		if (request.getHeader("x-forwarded-for") == null) {
			ipBean.setRemoteAddr(request.getRemoteAddr());
		} else {
			ipBean.setRemoteAddr(request.getHeader("x-forwarded-for"));
		}
		ipBean.setRemoteHost(request.getRemoteHost());
		ipBean.setRequestUrl(request.getRequestURI());
		ipBean.setServerName(request.getServerName());
		ipBean.setServerPath(request.getServletPath());
		ipBean.setServerPort("" + request.getServerPort());

		String sdTime = MyTools.ChangeTime(new Date());
		ipBean.setTime(sdTime);
		ipDao.addIP(ipBean);
		ipDao.getDB().close();// �ر����ݿ�����

		int begin;
		int count;
		String beginString = request.getParameter("articleBegin");// ��ҳ��ʾʱ�ĵڼ�ҳ
		String countString = request.getParameter("articleCount");// ÿҳ��������
		if (beginString == null)
			begin = 1;
		else
			begin = Integer.parseInt(beginString);
		if (countString == null)
			count = 10;
		else
			count = Integer.parseInt(countString);

		HttpSession session = request.getSession();
		ArticleDao articleDao = new ArticleDao();
		List articleList = articleDao.queryArticleFromTo(begin, count);// ��ȡ��������
		System.out.println("test---------------articleList.size=:"
				+ articleList.size());
		int articleSum = articleDao.queryArticleSum();
		articleDao.getDB().close();// �ر����ݿ�����
		session.setAttribute("articleSum", articleSum);
		session.setAttribute("articleList", articleList);

		RequestDispatcher rd = request.getRequestDispatcher("/front/article/AllArticles.jsp");
		rd.forward(request, response);
	}

	/**
	 * �鿴һƪ���µľ�������
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void readArticle(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		IPDao ipDao = new IPDao();
		IPBean ipBean = new IPBean();

		ipBean.setMethod(request.getMethod());
		ipBean.setProtocol(request.getProtocol());
		ipBean.setRealPath(request.getContextPath());// ������Ҫ�޸�
		ipBean.setReferer(request.getHeader("Referer"));
		if (request.getHeader("x-forwarded-for") == null) {
			ipBean.setRemoteAddr(request.getRemoteAddr());
		} else {
			ipBean.setRemoteAddr(request.getHeader("x-forwarded-for"));
		}
		ipBean.setRemoteHost(request.getRemoteHost());
		ipBean.setRequestUrl(request.getRequestURI());
		ipBean.setServerName(request.getServerName());
		ipBean.setServerPath(request.getServletPath());
		ipBean.setServerPort("" + request.getServerPort());

		HttpSession session = request.getSession();
		ipBean.setCharacterEncoding(request.getCharacterEncoding());
		ipBean.setQueryString(request.getQueryString());
		ipBean.setPathInfo(request.getPathInfo());
		ipBean.setRemoteUser(request.getRemoteUser());
		ipBean.setAcceptLanguage(request.getHeader("Accept-Language"));
		ipBean.setAcceptEncoding(request.getHeader("Accept-Encoding"));
		ipBean.setUserAgent(request.getHeader("User-Agent"));
		ipBean.setLastAccessed(session.getLastAccessedTime() + "");

		String sdTime = MyTools.ChangeTime(new Date());
		ipBean.setTime(sdTime);
		ipDao.addIP(ipBean);

		// HttpSession session = request.getSession();
		ArticleBean articleBean = new ArticleBean();
		ArticleDao articleDao = new ArticleDao();
		ReviewDao reviewDao = new ReviewDao(); // ����һ���������uՓ�M�в����Č���

		String strId = request.getParameter("id");
		int id = MyTools.strToint(strId);
		articleBean.setId(id);

		boolean flag = articleDao.operationArticle("readTimes", articleBean);// �ۼ���x�Δ�
		if (flag)
			System.out.println("test:�����Ķ������ɹ�");
		else
			System.out.println("test:�����Ķ�����ʧ��");

		articleBean = articleDao.queryArticleSingle(id);// �@ȡָ�����µă���
		session.setAttribute("readSingle", articleBean);// ����article��request��

		int begin;
		int count;
		String beginString = request.getParameter("reviewBegin");// ��ҳ��ʾʱ�ĵڼ�ҳ
		String countString = request.getParameter("reviewCount");// ÿҳ��������
		if (beginString == null)
			begin = 1;
		else
			begin = Integer.parseInt(beginString);
		if (countString == null)
			count = 10;
		else
			count = Integer.parseInt(countString);

		List reviewList = reviewDao.queryReview(id, begin, count); // �@ȡָ�����µ��uՓ
		int reviewSum = reviewDao.queryReviewSum(id);
		session.setAttribute("reviewSum", reviewSum);
		session.setAttribute("reviewList", reviewList);
		RequestDispatcher rd = request
				.getRequestDispatcher("/front/article/ArticleSingle.jsp");
		rd.forward(request, response);
	}

	// ת������������ҳ��
	public void addArticles(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		IPDao ipDao = new IPDao();
		IPBean ipBean = new IPBean();
		ipBean.setMethod(request.getMethod());
		ipBean.setProtocol(request.getProtocol());
		ipBean.setRealPath(request.getContextPath());// ������Ҫ�޸�
		ipBean.setReferer(request.getHeader("Referer"));
		if (request.getHeader("x-forwarded-for") == null) {
			ipBean.setRemoteAddr(request.getRemoteAddr());
		} else {
			ipBean.setRemoteAddr(request.getHeader("x-forwarded-for"));
		}
		ipBean.setRemoteHost(request.getRemoteHost());
		ipBean.setRequestUrl(request.getRequestURI());
		ipBean.setServerName(request.getServerName());
		ipBean.setServerPath(request.getServletPath());
		ipBean.setServerPort("" + request.getServerPort());
		String sdTime = MyTools.ChangeTime(new Date());
		ipBean.setTime(sdTime);
		ipDao.addIP(ipBean);
		ipDao.getDB().close();// �ر����ݿ�����
		
		ArticleDao ad = new ArticleDao();
		HashMap<Integer,String> hm = new HashMap<Integer,String>();
		hm = ad.queryArticleStyle();
		HttpSession session = request.getSession();
		session.setAttribute("hm", hm);
		ad.getDB().close();

		RequestDispatcher rd = request.getRequestDispatcher("/admin/AddArticle.jsp");
		rd.forward(request, response);
	}
	/*
	 * ȷ�Ϸ�������
	 */
	public void addArticlesConfig(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		IPDao ipDao = new IPDao();
		IPBean ipBean = new IPBean();
		ipBean.setMethod(request.getMethod());
		ipBean.setProtocol(request.getProtocol());
		ipBean.setRealPath(request.getContextPath());// ������Ҫ�޸�
		ipBean.setReferer(request.getHeader("Referer"));
		if (request.getHeader("x-forwarded-for") == null) {
			ipBean.setRemoteAddr(request.getRemoteAddr());
		} else {
			ipBean.setRemoteAddr(request.getHeader("x-forwarded-for"));
		}
		ipBean.setRemoteHost(request.getRemoteHost());
		ipBean.setRequestUrl(request.getRequestURI());
		ipBean.setServerName(request.getServerName());
		ipBean.setServerPath(request.getServletPath());
		ipBean.setServerPort("" + request.getServerPort());
		String sdTime = MyTools.ChangeTime(new Date());
		ipBean.setTime(sdTime);
		ipDao.addIP(ipBean);
		ipDao.getDB().close();// �ر����ݿ�����
		
		
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		String create = request.getParameter("create");
		String info = request.getParameter("info");
		//String sdTime = MyTools.ChangeTime(new Date();
		
		if(title != null)
			title = new String(title.getBytes("ISO-8859-1"),"UTF8");//������ת��
		if(content != null)
			content = new String(content.getBytes("ISO-8859-1"),"UTF8");
		if(info != null)
			info = new String(info.getBytes("ISO-8859-1"),"UTF8");//������ת��
		if(create != null)
			create = new String(create.getBytes("ISO-8859-1"),"UTF8");
			
		ArticleBean ab = new ArticleBean();
		ab.setContent(content);
		ab.setCreate(create);
		ab.setInfo(info);
		ab.setSdTime(sdTime);
		ab.setTitle(title);
		ab.setTypeId(Integer.valueOf(request.getParameter("typeId")));
		
		ArticleDao ad = new ArticleDao();
		Boolean flag = ad.operationArticle("add", ab);
		System.out.println("test----------------------->addAtricleConfig.flag="+flag);
		ad.getDB().close();
		
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
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
