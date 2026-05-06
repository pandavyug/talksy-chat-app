import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.sql.*;

@WebServlet("/setAvatar")
public class AvatarServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        String avatar = request.getParameter("avatar");

        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("username") == null){
            response.sendRedirect("index.html");
            return;
        }

        String username = session.getAttribute("username").toString();

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/chatapp",
                "root",
                "2108"
            );

            PreparedStatement ps = con.prepareStatement(
                "UPDATE users SET avatar=? WHERE username=?"
            );

            ps.setString(1, avatar);
            ps.setString(2, username);

            ps.executeUpdate();

            con.close();

            response.sendRedirect("chat.html");

        } 
        catch(Exception e){
            e.printStackTrace();
        }
    }
}