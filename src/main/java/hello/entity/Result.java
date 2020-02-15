package hello.entity;

public class Result {
    String status;
    String msg;
    boolean login;
    Object data;

    public static Result failure(String message) {
        return new Result("fail", message, false);
    }

    public Result(String status, String msg, boolean isLogin) {
        this(status, msg, isLogin, null);
    }

    public Result(String status, String msg, boolean isLogin, Object data) {
        this.status = status;
        this.msg = msg;
        this.login = isLogin;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isLogin() {
        return login;
    }
}
