import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

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

            // check user exists
            PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM users WHERE username=?"
            );

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if(!rs.next()){
                response.sendRedirect("index.html?error=user");
                return;
            }

            String dbPhone = rs.getString("phone");
            String dbPassword = rs.getString("password");

            // phone check
            if(!dbPhone.equals(phone)){
                response.sendRedirect("index.html?error=phone");
                return;
            }

            // password check
            if(!dbPassword.equals(password)){
                response.sendRedirect("index.html?error=password");
                return;
            }

            // login success
            HttpSession session = request.getSession();
            session.setAttribute("username", username);

            response.sendRedirect("chat.html");

        } 
        catch(Exception e){
            e.printStackTrace();
        }
    }
}