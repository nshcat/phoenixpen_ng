package com.phoenixpen.desktop.application;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.ptr.IntByReference;

import java.awt.*;

public class X11DesktopModeHelper {

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

    public static boolean setToDesktopMode(Window w)
    {
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

            X11Wrapper.enableDesktopMode(Native.getWindowID(w));

            return true;
        }
        finally {
            if(display != null) {
                // Close the display
                x.XCloseDisplay(display);
            }
        }
    }

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

}