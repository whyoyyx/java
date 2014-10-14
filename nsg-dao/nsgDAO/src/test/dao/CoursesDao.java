package test.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CoursesDao extends JdbcBaseDao
{
	Logger logger = LogManager.getLogger(CoursesDao.class);
	
	public void getCourses()
	{
		StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM course ");
		
		String sql			= sqlBuilder.toString();
		List<Object[]> list	= query(sql);
		
		System.out.println(list.get(0)[1]);
		
		logger.info(this.getClass().getName()+" ---> "+"CoursesDao.getCourses()");
	}
}
