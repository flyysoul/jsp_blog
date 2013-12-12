package com.xiuson.dao;

import java.sql.*;

public class DB {

	// ���ݿ�URL
	private String url = "jdbc:mysql://localhost:3306/db_blog";//db_blogΪ���Լ���MySQL�������沩�����ݱ�����ݿ�
	
	
	private String userName ="root";//���ݿ��˺�

	
	private String password ="������������";//��Ϊ���ݿ�����

	
	// ���ݿ�������·��
	private String className = "com.mysql.jdbc.Driver";
	private Connection con = null;
	private Statement stm = null;

	/**
	 * ͨ�����췽���������ݿ�����
	 */
	public DB() {
		try {
			Class.forName(className).newInstance();
		} catch (Exception e) {
		//	e.printStackTrace();
			System.out.println("�������ݿ�����ʧ��");
		}
	}

	/**
	 * �������ݿ�����
	 */
	public void createCon() {
		try {
			// �������ӣ����ӵ�������urlָ�������ݿ�URL����ָ����½���ݿ���˺����롣
			con = DriverManager.getConnection(url, userName, password);
		} catch (SQLException e) {
		//	e.printStackTrace();
			System.out.println("��ȡ���ݿ�����ʧ��");
		}
	}

	/**
	 * ִ��StatMent����
	 */
	public void getStm() {
		createCon();
		try {
			// ����Connection��ʵ����createStatement()������ȡһ��StateMent�����
			stm = con.createStatement();
		} catch (Exception e) {
		//	e.printStackTrace();
			System.out.println("����Statement����ʧ��");
		}
	}

	/**
	 * ���ܣ������ݿ�����ӡ��޸ġ���ɾ���Ĳ��� ������sqlΪҪִ�е�SQL��� ����booleanֵ
	 */
	public boolean executeUpdate(String sql) {
		boolean mark = false;
		try {
			getStm();
			int iCount = stm.executeUpdate(sql);
			if (iCount > 0)
				mark = true;
			else
				mark = false;
		} catch (Exception e) {
		//	e.printStackTrace();
			System.out.println("����ʧ��");
			mark = false;
		}
		return mark;
	}

	/**
	 * ��ѯ���ݿ�
	 */
	public ResultSet executeQuery(String sql) {
		ResultSet rs = null;
		try {
			getStm();
			try {
				rs = stm.executeQuery(sql);
			} catch (Exception e) {
			//	e.printStackTrace();
			//	System.out.println();
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return rs;
	}
	
	public void close() {
		/*
		try {
			if(con != null )// || !con.isClosed()
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		*/
	}

}
