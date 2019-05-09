#version 450

layout (location=0) in vec3 position;
layout (location=1) in vec2 in_texture;

out vec2 out_texture;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main()
{
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
    out_texture = in_texture;
}