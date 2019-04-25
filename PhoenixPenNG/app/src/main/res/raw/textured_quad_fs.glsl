#version 310 es

precision mediump float;


uniform sampler2D srcTexture;

layout (location = 0) out vec4 fragmentColor;

in vec2 texCoords;


void main()
{
	fragmentColor = texture(srcTexture, texCoords);
}