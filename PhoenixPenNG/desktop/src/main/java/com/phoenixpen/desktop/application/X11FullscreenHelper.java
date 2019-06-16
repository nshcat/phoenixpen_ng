package com.phoenixpen.desktop.application;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.ptr.IntByReference;

import java.awt.*;

/**
 * Author: Vladimir Kravets
 * E-Mail: vova.kravets@gmail.com
 * Date: 4/10/13
 * Time: 12:19 AM
 * based on code from VLCJ
 * http://code.google.com/p/vlcj/source/browse/trunk/vlcj/src/main/java/uk/co/caprica/vlcj/runtime/x/LibXUtil.java
 */

public class X11FullscreenHelper {

    private static boolean isFullScreenMode = false;

    public static void changeAtom(
            X11.Display d,
            X11.Window w,
            String atomName,
            String value,
            int mode
    )
    {
        X11 x = X11.INSTANCE;
        X11.Atom wtype = x.XInternAtom(d, atomName, false);
        X11.Atom data = x.XInternAtom(d, value, false);

        // TODO that might not work
        int atomValue = data.intValue();
        IntByReference patom = new IntByReference(atomValue);

        x.XChangeProperty(d, w, wtype, X11.XA_ATOM, 32, mode, patom.getPointer(), 1);
    }


    public static void setWindowTypeToDesktop(
            X11.Display d,
            X11.Window w
    )
    {
        changeAtom(d, w, "_NET_WM_WINDOW_TYPE", "_NET_WM_WINDOW_TYPE_DESKTOP", X11.PropModeReplace);
    }


    /**
     * Ask the window manager to make a window full-screen.
     * <p>
     * This method sends a low-level event to an X window to request that the
     * window be made 'real' full-screen - i.e. the window will be sized to fill
     * the entire screen bounds, and will appear <em>above</em> any window
     * manager screen furniture such as panels and menus.
     * <p>
     * This method should only be called on platforms where X is supported.
     * <p>
     * The implementation makes use of the JNA X11 platform binding.
     *
     * @param w window to make full-screen
     * @param fullScreen <code>true</code> to make the window full-screen; <code>false</code> to restore the window to it's original size and position
     * @return <code>true</code> if the message was successfully sent to the window; <code>false</code> otherwise
     */
    public static boolean setFullScreenWindow(Window w, boolean fullScreen) {
        // Use the JNA platform X11 binding
        X11 x = X11.INSTANCE;
        X11.Display display = null;
        X11.Window window = new X11.Window(Native.getWindowID(w));

        try {
            // Open the display
            display = x.XOpenDisplay(null);

            X11.XSetWindowAttributes attr = new X11.XSetWindowAttributes();
            attr.override_redirect = true;
            x.XChangeWindowAttributes(display, window, new NativeLong(X11.CWOverrideRedirect), attr);
            


            //setWindowTypeToDesktop(display, window);

            // Send the message

            /*int result = sendClientMessage(
                    display,
                    Native.getWindowID(w),
                    "_NET_WM_STATE",
                    new NativeLong[]{
                            new NativeLong(fullScreen ? _NET_WM_STATE_ADD : _NET_WM_STATE_REMOVE),
                            x.XInternAtom(display, "_NET_WM_STATE_HIDDEN", false),
                            x.XInternAtom(display, "_NET_WM_STATE_ABOVE", false),
                            new NativeLong(0L),
                            new NativeLong(0L)
                    }
            );
            isFullScreenMode = (result != 0) && fullScreen;
            return (result != 0);*/
            return true;
        }
        finally {
            if(display != null) {
                // Close the display
                x.XCloseDisplay(display);
            }
        }
    }

    /**
     * Helper method to send a client message to an X window.
     *
     * @param display display
     * @param wid native window identifier
     * @param msg type of message to send
     * @param data0 message data
     * @param data1 message data
     * @return <code>1</code> if the message was successfully sent to the window; <code>0</code> otherwise
     */
    private static int sendClientMessage(X11.Display display, long wid, String msg, NativeLong[] data) {
        // Use the JNA platform X11 binding
        assert (data.length == 5);
        X11 x = X11.INSTANCE;
        // Create and populate a client-event structure
        X11.XEvent event = new X11.XEvent();
        event.type = X11.ClientMessage;
        // Select the proper union structure for the event type and populate it
        event.setType(X11.XClientMessageEvent.class);
        event.xclient.type = X11.ClientMessage;
        event.xclient.serial = new NativeLong(0L);
        event.xclient.send_event = 1;
        event.xclient.message_type = x.XInternAtom(display, msg, false);
        event.xclient.window = new X11.Window(wid);
        event.xclient.format = 32;
        // Select the proper union structure for the event data and populate it
        event.xclient.data.setType(NativeLong[].class);
        System.arraycopy(data, 0, event.xclient.data.l, 0, 5);

        // Send the event
        NativeLong mask = new NativeLong(X11.SubstructureRedirectMask | X11.SubstructureNotifyMask);
        int result = x.XSendEvent(display, x.XDefaultRootWindow(display), 0, mask, event);
        // Flush, since we're not processing an X event loop
        x.XFlush(display);
        // Finally, return the result of sending the event
        return result;
    }

    public static boolean isInFullscreen() {
        // TODO: to get window attribute and check if _NET_WM_STATE_FULLSCREEN set on window
        return isFullScreenMode;
    }

    // X window message definitions
    private static final int _NET_WM_STATE_REMOVE = 0;
    private static final int _NET_WM_STATE_ADD    = 1;

}