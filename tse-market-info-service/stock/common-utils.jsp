<%@ page import="java.util.Enumeration"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.concurrent.ConcurrentHashMap"%>
<%@ page import="javax.servlet.http.HttpServletRequest"%>
<%@ page import="javax.servlet.http.HttpSession"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@ page import="org.owasp.esapi.User"%>
<%@ page import="org.owasp.esapi.errors.AuthenticationException"%>

<%!
public String purifyString(String inputStr, String defaultStr, int maxLength){
    if (inputStr == null || ! inputStr.matches("[_a-zA-Z0-9-]*") || inputStr.contains("0x")) {
        return defaultStr;
    }

    if (inputStr.length() < maxLength) {
        maxLength = inputStr.length();
    }

    return inputStr.substring(0,maxLength).replaceAll("(?i)script", "").replaceAll("(?i)alert", "").replaceAll("(?i)prompt", "");
}

public HttpSession changeSessionIdentifier(HttpServletRequest request) throws AuthenticationException {

    // 1.get the current session
    HttpSession oldSession = request.getSession();

    // 2.make a copy of the session content
    Map<String, Object> temp = new ConcurrentHashMap<String, Object>();
    Enumeration<String> e = oldSession.getAttributeNames();
    while (e != null && e.hasMoreElements()) {
        String name = (String) e.nextElement();
        Object value = oldSession.getAttribute(name);
        temp.put(name, value);
    }

    // 3.kill the old session and create a new one
    oldSession.invalidate();
    HttpSession newSession = request.getSession();
    User user = ESAPI.authenticator().getCurrentUser();
    user.addSession(newSession);
    user.removeSession(oldSession);

    // 4.copy back the session content to new session
    for (Map.Entry<String, Object> stringObjectEntry : temp.entrySet()) {
        newSession.setAttribute(stringObjectEntry.getKey(), stringObjectEntry.getValue());
    }

    return newSession;
}
%>
