#include "X11Wrapper.h"
#include <Xlib.h>
#include <stdio.h>
#include <stdint.h>


JNIEXPORT void JNICALL Java_com_phoenixpen_desktop_application_X11Wrapper_raiseWindow
  (JNIEnv* e, jobject o, jlong xid)
{
	// Create window XID
	Window w = (Window)xid;
	
	// Retrieve display
	Display* d = XOpenDisplay(NULL);
	
	Window hwnd;
	int revert_to;
	
	XGetInputFocus(d, &hwnd, &revert_to);
	
	char* retval;
	
	if(XFetchName(d, w, &retval) != 0)
	{
		printf("Window name: %s", retval);
		XFree(retval);
	}
	XSetWindowAttributes attr = { };
	attr.override_redirect = 1;
	XChangeWindowAttributes(d, w, CWOverrideRedirect, &attr);
	
	// Raise window
	XLowerWindow(d, w);
	
	// Close connection
	XCloseDisplay(d);
	
	return;
}

