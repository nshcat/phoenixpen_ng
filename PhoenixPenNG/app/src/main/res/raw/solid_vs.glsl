#version 310 es

precision mediump float;

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec4 vertexColor;
layout(location = 2) in vec3 vertexNormal;

out vec4 fragmentColor;
out vec3 fragmentPosition;
out smooth vec3 fragmentNormal;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
uniform mat3 normalTransform;

void main()
{
    fragmentNormal = normalTransform * vertexNormal;
    fragmentColor = vertexColor;
    vec4 vPos = vec4(vertexPosition, 1.0f);
    gl_Position = projection * view * model * vPos;
}
