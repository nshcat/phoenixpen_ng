package com.phoenixpen.desktop.application;

import java.io.File;

public class X11Wrapper
{
    static
    {
        try
        {
            File pwd = new File(".");
            System.load(pwd.getCanonicalPath() + "/"  + System.mapLibraryName("xlibwrapper"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    native static void enableDesktopMode(long window);
}
