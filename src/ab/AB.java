/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ab;

/**
 *
 * @author Chathuranga - Pamba
 */
/*
 import static ab.AB.Kernel32.OpenProcess;
 import static ab.AB.Kernel32.PROCESS_QUERY_INFORMATION;
 import static ab.AB.Kernel32.PROCESS_VM_READ;
 import static ab.AB.Psapi.GetModuleBaseNameW;
 import static ab.AB.User32DLL.GetForegroundWindow;
 import static ab.AB.User32DLL.GetWindowTextW;
 import static ab.AB.User32DLL.GetWindowThreadProcessId;
 import com.sun.jna.Native;
 import com.sun.jna.Pointer;
 import com.sun.jna.platform.win32.WinDef.HWND;
 import com.sun.jna.ptr.PointerByReference;
 import static java.util.Collections.enumeration;

 public class AB {
 private static final int MAX_TITLE_LENGTH = 1024;

 public static void main(String[] args) throws Exception {
 char[] buffer = new char[MAX_TITLE_LENGTH * 2];
 GetWindowTextW(GetForegroundWindow(), buffer, MAX_TITLE_LENGTH);
 System.out.println("Active window title: " + Native.toString(buffer));

 PointerByReference pointer = new PointerByReference();
 GetWindowThreadProcessId(GetForegroundWindow(), pointer);
 Pointer process = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, pointer.getValue());
 GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
 System.out.println("Active window process: " + Native.toString(buffer));
 }

 static class Psapi {
 static { Native.register("psapi"); }
 public static native int GetModuleBaseNameW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);
 }

 static class Kernel32 {
 static { Native.register("kernel32"); }
 public static int PROCESS_QUERY_INFORMATION = 0x0400;
 public static int PROCESS_VM_READ = 0x0010;
 public static native int GetLastError();
 public static native Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
 }

 static class User32DLL {
 static { Native.register("user32"); }
 public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);
 public static native HWND GetForegroundWindow();
 public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
 }
 }
 */
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

// see http://twall.github.io/jna/4.0/javadoc/
public class AB {

    private static final int MAX_TITLE_LENGTH = 1024;
    static String winTitle;
    static Rectangle screen;

    public static void main(String[] args) throws Exception {

        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().startsWith("windows")) {
            // we change the temp directory because sometimes Windows is stupid and doesn't want to load jna.dll from the temp directory
            File tempDir = new File(System.getenv("USERPROFILE") + "\\AppData\\Local\\MyCompany\\temp");
            //System.out.println("Using temp dir: " + tempDir.getPath());
            tempDir.getPath();
            tempDir.mkdirs();
            System.setProperty("java.io.tmpdir", tempDir.getPath());
        }

        new Thread() {

            @Override
            public void run() {
                while (true) {
                    

                    char[] buffer = new char[MAX_TITLE_LENGTH * 2];
                    HWND hwnd = User32.INSTANCE.GetForegroundWindow();
                    User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
                    winTitle = Native.toString(buffer);
                    //System.out.println("title: " + winTitle);
                    RECT rect = new RECT();
                    User32.INSTANCE.GetWindowRect(hwnd, rect);
                    screen = rect.toRectangle();

                    String dateAndTime = getCurrentTimeAndDate();
                    
                    String fileName = dateAndTime + " "+winTitle ; 
                    System.out.println(fileName);    
                    getScreenShot(screen, fileName );

                    //System.out.println("rect = " + rect);
                    
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AB.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }.start();

        // System.out.println();
    }

    static void getScreenShot(Rectangle r, String fileName) {

        try {

            Rectangle screenRectangle = r;
            Robot robot = new Robot();
            BufferedImage image = robot.createScreenCapture(screenRectangle);
            try {
                ImageIO.write(image, "png", new File(fileName + ".png"));
            } catch (IOException ex) {
                Logger.getLogger(AB.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (AWTException ex) {
            Logger.getLogger(AB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static String getCurrentTimeAndDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}
