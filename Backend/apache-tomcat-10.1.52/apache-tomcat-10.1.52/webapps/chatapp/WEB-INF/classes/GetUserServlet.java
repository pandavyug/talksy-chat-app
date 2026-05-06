//this is only for get one user info (username and avatar) 
// when user is logged in to show on the chat page header

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/getUser")
public class GetUserServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if(session != null && session.getAttribute("username") != null){

            String username = session.getAttribute("username").toString();

            try{

                Class.forName("com.mysql.cj.jdbc.Driver");

                Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/chatapp",
                    "root",
                    "2108"
                );

                PreparedStatement ps = con.prepareStatement(
                    "SELECT avatar FROM users WHERE username=?"
                );

                ps.setString(1, username);

                ResultSet rs = ps.executeQuery();

                if(rs.next()){

                    String avatar = rs.getString("avatar");

                    response.getWriter().write(username + "|" + avatar);

                }

                con.close();

            }catch(Exception e){
                e.printStackTrace();
            }

        } 
        else {
            response.getWriter().write("not_logged_in");
        }
    }
}

