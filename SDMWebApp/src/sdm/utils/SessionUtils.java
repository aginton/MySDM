package sdm.utils;

import sdm.constants.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {


    public static String getUsername (HttpServletRequest request) {
        //getSession(false) : Returns the current HttpSession associated with this request, if there is no current session, returns null.
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.USERNAME) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static String getRole (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(Constants.ROLE) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }


    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }
}