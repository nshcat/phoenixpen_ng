#! /bin/bash

gcc -lX11 -shared -o libxlibwrapper.so -I/usr/lib/jvm/java-8-openjdk/include -I/usr/lib/jvm/java-8-openjdk/include/linux -I/usr/include/X11 X11Wrapper.c

