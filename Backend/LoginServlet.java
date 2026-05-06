import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/chatapp", "root", "YOUR_PASSWORD"
            );

            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM users WHERE username=? AND password=?"
            );

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if(rs.next()){
                response.sendRedirect("chat.html");   // ✅ GO TO CHAT PAGE
            } else {
                // Redirect back to login page with a query param so client-side JS can show the error
                response.sendRedirect("index.html?error=invalid");
            }

        } catch(Exception e){
            e.printStackTrace();
        }
    }
}