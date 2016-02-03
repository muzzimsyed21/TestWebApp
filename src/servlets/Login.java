package servlets;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import containers.Page;
import containers.Query;
import containers.Servlet;
import models.Movie;
import models.User;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by ka9mse on 1/28/2016.
 */

@WebServlet("/Login")
public class Login extends HttpServlet{
    private static final long serialVersionUID = 1L;

    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
     public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        HttpSession session = req.getSession();
        User currentUser = (User)session.getAttribute("User");

        if (currentUser.notExists()){
            //load login page
            RequestDispatcher view = req.getRequestDispatcher("/login.html");
            view.forward(req, resp);
        }

        else{
            //go to main page
            resp.sendRedirect("Main");
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        Query queryGenerator = new Query();
        System.out.println("doPost called");
        try {

            //Create a JNDI Initial context to be able to lookup the DataSource
            //consider caching this as a static or instance variable since its expensive to create JNDI context
            Context init = new InitialContext();
            if (init == null) out.println ("initialContext is NULL");

            //can also be cached as static/instance variable since JNDI lookups expensive.
            Context env = (Context) init.lookup("java:comp/env");
            if (env == null ) out.println("Env is NULL");

            DataSource source = (DataSource)env.lookup("jdbc/moviedb");

            //get connection from pool
            Connection connection = source.getConnection();

            //get login credential from login form
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            //query input from form
            PreparedStatement pStatement = connection.prepareStatement(queryGenerator.login(username, password));
            System.out.println(pStatement);
            ResultSet result = pStatement.executeQuery();

            //if correct username/password add to to the session variable
            if (result.next()){

                System.out.println("proceed to main");
                req.getSession().setAttribute("User", new User(result.getString("id"), result.getString("first_name"), result.getString("last_name"),
                        result.getString("email"), result.getString("password"), new ArrayList<String>()));
                req.getSession().setAttribute("pages", new Page());
                req.getSession().setAttribute("servlets", new Servlet());
                req.getSession().setAttribute("moviesToShow", new ArrayList<Movie>());
                req.getSession().setAttribute("showAdvancedMenu", false);

                resp.sendRedirect("Main");

            }

            // or else no query was returned (wrong credentials) alert client
            else{

                req.getRequestDispatcher("/index.jsp").forward(req, resp);

                // PRINT THIS SCRIPT ON WRONG UN/PASSWORD!
                //out.println("<SCRIPT>alert('Username and password do not match')</SCRIPT>");

                System.out.println("wrong password");
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }
}


