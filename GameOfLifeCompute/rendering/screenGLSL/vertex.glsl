#version 450

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 textCoords;
layout(location = 2) in vec3 colour;

out vec2 textureCoords;
out vec3 outColour;

void main() {
    gl_Position=vec4(position,0,1);
    textureCoords=textCoords;
    outColour=colour;
}
