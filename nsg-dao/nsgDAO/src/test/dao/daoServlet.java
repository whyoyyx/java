package test.dao;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.nsg.app.AppConfig;
import org.nsg.dao.jdbc.AbstractJdbcSessionMgr;
import org.nsg.util.LogUtil;

/**
 * Servlet implementation class daoServlet
 */
@WebServlet("/daoServlet")
public class daoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public daoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CoursesDao dao = new CoursesDao();
		dao.getCourses();
		/*AbstractJdbcSessionMgr jdbcSessionMgr = 
		System.out.println("daoServlet.doGet()");
		System.out.println(jdbcSessionMgr);*/
		LogUtil.exception(new RuntimeException(), dao, Level.ERROR, false);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
