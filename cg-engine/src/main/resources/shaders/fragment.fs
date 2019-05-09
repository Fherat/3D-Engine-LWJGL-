#version 450

in vec2 out_texture;
out vec4 fragColor;

uniform sampler2D texture_sampler;

vec4 greyScale(vec4 texture){
	
	float greyScale = dot(texture,vec4(0.299,0.587,0.114,0.0));
	
	return texture*greyScale;
}

vec4 colorMask(vec4 current, vec4 color){
	return current+color;
}

void main()
{
    vec4 texture = texture(texture_sampler, out_texture);
    
    if(texture.a < 0.5){
    	discard;
    }
    
    vec4 color = colorMask(texture,vec4(0.3,0.0,0.0,0.0));
    vec4 grey = greyScale(color);
    
    fragColor = grey;
}