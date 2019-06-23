#include "X11Wrapper.h"
#include <Xlib.h>
#include <stdio.h>
#include <stdint.h>
#include <memory.h>

#define _NET_WM_STATE_ADD 1

JNIEXPORT void JNICALL Java_com_phoenixpen_desktop_application_X11Wrapper_enableDesktopMode
  (JNIEnv* e, jobject o, jlong xid)
{
	// Create window XID
	Window w = (Window)xid;
	
	// Retrieve display
	Display* d = XOpenDisplay(NULL);
	
	// We want to set window states
	Atom wmNetWmState = XInternAtom(d, "_NET_WM_STATE", 1);
	
	// Retrieve atoms for states we want to set
	Atom wmStateSkipPager = XInternAtom(d, "_NET_WM_STATE_SKIP_PAGER", 1);
	Atom wmStateSkipTaskbar = XInternAtom(d, "_NET_WM_STATE_SKIP_TASKBAR", 1);
	Atom wmStateSticky = XInternAtom(d, "_NET_WM_STATE_STICKY", 1);
	Atom wmStateBelow = XInternAtom(d, "_NET_WM_STATE_BELOW", 1);
	
	// Create client message
	XClientMessageEvent xclient;
	memset(&xclient, 0, sizeof(xclient));
	
	// Fill with data
	xclient.type = ClientMessage;
	xclient.window = w;
	xclient.message_type = wmNetWmState;
	xclient.format = 32;
	xclient.data.l[0] = _NET_WM_STATE_ADD;
	xclient.data.l[1] = wmStateBelow;
	
	// Send message
	XSendEvent(d, DefaultRootWindow(d), False, SubstructureRedirectMask | SubstructureNotifyMask, (XEvent *)&xclient);
	
	// Fill with data
	xclient.type = ClientMessage;
	xclient.window = w;
	xclient.message_type = wmNetWmState;
	xclient.format = 32;
	xclient.data.l[0] = _NET_WM_STATE_ADD;
	xclient.data.l[1] = wmStateSticky;
	
	// Send message
	XSendEvent(d, DefaultRootWindow(d), False, SubstructureRedirectMask | SubstructureNotifyMask, (XEvent *)&xclient);
	
	// Fill with data
	xclient.type = ClientMessage;
	xclient.window = w;
	xclient.message_type = wmNetWmState;
	xclient.format = 32;
	xclient.data.l[0] = _NET_WM_STATE_ADD;
	xclient.data.l[1] = wmStateSkipPager;
	
	// Send message
	XSendEvent(d, DefaultRootWindow(d), False, SubstructureRedirectMask | SubstructureNotifyMask, (XEvent *)&xclient);
	
	
	// Fill with data
	xclient.type = ClientMessage;
	xclient.window = w;
	xclient.message_type = wmNetWmState;
	xclient.format = 32;
	xclient.data.l[0] = _NET_WM_STATE_ADD;
	xclient.data.l[1] = wmStateSkipTaskbar;
	
	// Send message
	XSendEvent(d, DefaultRootWindow(d), False, SubstructureRedirectMask | SubstructureNotifyMask, (XEvent *)&xclient);
	
	
	// Flush message queue
	XFlush(d);
	
	// Close connection
	XCloseDisplay(d);
	
	
	
	return;
}

