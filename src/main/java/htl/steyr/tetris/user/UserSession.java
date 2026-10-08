package htl.steyr.tetris.user;

public class UserSession {

    private static UserData ud = null;

    public static void setUserData(UserData userData){
        ud = userData;
    }
    public static UserData getUserData(){
        return ud;
    }
}
