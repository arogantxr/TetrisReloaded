package htl.steyr.tetris.key;

import java.io.*;

public class SaveKey {
    public static void save(String username, KeyAssignment keys) {
        try {
            File dir = new File("users");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("users/" + username + "_keys.dat"))) {
                out.writeObject(keys);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static KeyAssignment load(String username) {
        File file = new File("users/" + username + "_keys.dat");

        if (!file.exists()) {
            return new KeyAssignment(); // default
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (KeyAssignment) in.readObject();
        } catch (Exception e) {
            return new KeyAssignment();
        }
    }
}
