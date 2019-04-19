#version 310 es

precision mediump float;

in vec4 fragmentColor;
in vec3 fragmentPosition;
in vec3 fragmentNormal;
out vec4 color;

void main()
{
    color = fragmentColor;
}
