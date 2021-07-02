#version 450

layout(location = 0) in vec2 position;
layout(location = 1) in vec2 textCoords;

out vec2 textureCoords;

void main() {
    gl_Position=vec4(position,0,1);
    textureCoords=textCoords;
}
