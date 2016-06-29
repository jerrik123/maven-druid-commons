package com.mangocity.druid.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AcquireConnInConrrency {
	static ExecutorService executors = Executors.newCachedThreadPool();

	static final long MAX_THRESHOLD = 50000;

	public static void main(String[] args) throws InterruptedException {
		long start = System.currentTimeMillis();
		for (long count = 0; count < MAX_THRESHOLD; count++) {
			executors.execute(new Runnable() {
				@Override
				public void run() {
					try {
						Connection conn = DBManager.getConn();
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
