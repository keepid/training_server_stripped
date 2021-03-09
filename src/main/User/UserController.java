package User;

import Config.Message;
import Database.UserDao;
import Logger.LogFactory;
import User.Services.GetUserInfoService;
import User.Services.LoginService;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import io.javalin.http.Handler;
import org.json.JSONObject;
import org.slf4j.Logger;

public class UserController {
  Logger logger;
  MongoDatabase db;
  UserDao userDao;

  public UserController(UserDao userDao) {
    this.userDao = userDao;
    LogFactory l = new LogFactory();
    logger = l.createLogger("UserController");
    logger = (new LogFactory()).createLogger("UserController");
  }

  public Handler loginUser =
      ctx -> {
        ctx.req.getSession().invalidate();
        JSONObject req = new JSONObject(ctx.body());
        String username = req.getString("username");
        String password = req.getString("password");
        LoginService loginService = new LoginService(userDao, logger, username, password);
        Message loginRes = loginService.executeAndGetResponse();
        if (loginRes == UserMessage.AUTH_SUCCESS) {
            ctx.sessionAttribute("username", username);
        }
        ctx.result(loginRes.toResponseString());
      };

  public Handler logout =
      ctx -> {
        ctx.req.getSession().invalidate();
        logger.info("Signed out");
        ctx.result(UserMessage.SUCCESS.toJSON().toString());
      };

  public Handler getUserInfo =
      ctx -> {
        logger.info("Started getUserInfo handler");
        String cookieUsername = ctx.sessionAttribute("username");
        JSONObject req = new JSONObject(ctx.body());
        String username = req.getString("username");
        if (!cookieUsername.equals(username))  {
            ctx.result(UserMessage.USER_NOT_FOUND.toJSON().toString());
        }
        else {
            GetUserInfoService infoService = new GetUserInfoService(userDao, logger, username);
            Message message = infoService.executeAndGetResponse();
            JSONObject jsonMessage = message.toJSON();
            if (UserMessage.SUCCESS != message) {
                ctx.result(jsonMessage.toString());
            } else {
                JSONObject info = infoService.getUserFields();
                jsonMessage = mergeJSON(jsonMessage, info);
                ctx.result(jsonMessage.toString());
            }
        }
      };

  // helper function to merge 2 json objects
  public static JSONObject mergeJSON(JSONObject object1, JSONObject object2) {
    JSONObject merged = new JSONObject(object1, JSONObject.getNames(object1));
    for (String key : JSONObject.getNames(object2)) {
      merged.put(key, object2.get(key));
    }
    return merged;
  }
}
