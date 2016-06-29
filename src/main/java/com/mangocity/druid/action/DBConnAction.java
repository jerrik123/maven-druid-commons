package com.mangocity.druid.action;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mangocity.druid.test.DBManager;

public class DBConnAction extends HttpServlet {
	static ExecutorService executors = Executors.newCachedThreadPool();

	static final long MAX_THRESHOLD = 50000;
	
	static ApplicationContext cxt = null;
	
	static{
		cxt = new ClassPathXmlApplicationContext("beans.xml");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			boot();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void boot() throws InterruptedException, SQLException {
		final DataSource dataSource = cxt.getBean("dataSource", DataSource.class);
		if(null == dataSource){
			System.out.println("获取不到数据源,停止运行...");
		}
		long start = System.currentTimeMillis();
		for (long count = 0; count < MAX_THRESHOLD; count++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection conn = dataSource.getConnection();
						PreparedStatement pstmt = DBManager
								.getPreparedStatement(conn,
										"select * from t_user");
						DBManager.printResultSet(pstmt);
						DBManager.close(conn);
					} catch (SQLException e) {
						e.printStackTrace();
						System.out.println("超过最大连接数,系统停止执行");
						System.exit(-1);
						return;
					}
				}
			});
			TimeUnit.SECONDS.sleep(3);
		}
		System.out.println("===================CostTimes===================: "
				+ (System.currentTimeMillis() - start) / 1000.0);
		executors.shutdown();
	}

}
