import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        String username = request.getParameter("username");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/chatapp",
                "root",
                "2108"
            );

            // check if phone already exists
            PreparedStatement check = con.prepareStatement(
                "SELECT * FROM users WHERE phone=?"
            );

            check.setString(1, phone);

            ResultSet rs = check.executeQuery();

            if(rs.next()){
                // phone already registered
                response.sendRedirect(request.getContextPath() + "/registration.html?regerror=phone");
                return;
            }

            // insert user
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO users(username, phone, password) VALUES (?, ?, ?)"
            );

            ps.setString(1, username);
            ps.setString(2, phone);
            ps.setString(3, password);

            int i = ps.executeUpdate();

            if(i > 0){

                // registration success

                 HttpSession session = request.getSession();
                session.setAttribute("username", username);
                

                response.sendRedirect(request.getContextPath() + "/avtar.html");
            }
            else{
                response.sendRedirect(request.getContextPath() + "/registration.html?regerror=register");
            }

            con.close();

        } 
        catch(Exception e){
            e.printStackTrace();
        }
    }
}