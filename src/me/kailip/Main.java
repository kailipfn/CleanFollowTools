package me.kailip;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.api.DirectMessagesResources;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static Twitter twitter = TwitterFactory.getSingleton();
    public static int count = 0;
    public static int ba = 0;
    public static List<Long> idList = new ArrayList<>();
    public static void main(String args[]) throws Exception{
        // このファクトリインスタンスは再利用可能でスレッドセーフです
        twitter.setOAuthConsumer("3rJOl1ODzm9yZy63FACdg", "5jPoQ5kQvMJFDYRNE8bQ4rHuds4xJqhvgNJM4awaE8");
        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("上のリンクを開いてPINを入力してね！");
            String pin = br.readLine();
            try {
                if(pin.length() > 0){
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if(401 == te.getStatusCode()){
                    System.out.println("Unable to get the access token.");
                }else{
                    te.printStackTrace();
                }
            }
        }

        IDs id = twitter.getFriendsIDs(twitter.verifyCredentials().getScreenName(),-1);

        for(long l : id.getIDs()) {
            idList.add(l);
        }

        System.out.println("===========================");

        System.out.println("@" + twitter.getScreenName());

        System.out.println("フォロワー: " + twitter.verifyCredentials().getFollowersCount());
        
        System.out.println("フォロー: " + twitter.verifyCredentials().getFriendsCount());

        System.out.println("===========================");

        Thread.sleep(5000);

        new Timer().start();

        for(int a = 0;a < 10;a++) {
            new Task(twitter).start();
        }
    }
}
class Task extends Thread {
    public Twitter twitter;
    public Task(Twitter twitter) {
        this.twitter = twitter;
    }
    @Override
    public void run() {
        while (true) {
            try {
                if(Main.idList.size() > 0) {
                    long id = Main.idList.get(0);
                    Main.idList.remove(id);
                    twitter.destroyFriendship(id);
                    if(Main.ba == 0) {
                        System.out.println(id + " をリムーブしました！");
                    }
                    Main.count++;
                }
                else {
                    if(Main.ba == 0) {
                        System.out.println("リムーブ作業終了したよ！");
                        System.out.println("===========================");
                        System.out.println("リムーブした人数: " + Main.count);
                        System.out.println("掛かった時間: " + Timer.getTime() + "秒");
                        System.out.println("===========================");
                        Main.ba++;
                    }
                    break;
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }
}
class Timer extends Thread {
    private static int time = 0;
    @Override
    public void run() {
        try {
            time++;
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static int getTime() {
        return time;
    }
}
