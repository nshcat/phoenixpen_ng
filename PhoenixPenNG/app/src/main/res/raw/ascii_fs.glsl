#version 310 es

precision mediump float;

in vec2 tex_coords;
flat in vec4 front_color;
flat in vec4 back_color;
in vec2 shadow_coords;
flat in float fog_factor;
flat in uint shadows[8];
flat in uint is_transparent;


uniform vec4 fog_color;
uniform sampler2D tex;
uniform sampler2D shadow_tex;

layout (location = 0) out vec4 fragmentColor;

vec4 calc_pixel()
{
	vec4 t_texColor = texture(tex, tex_coords);

	return mix(	back_color,
				front_color * t_texColor,
				t_texColor.a );
}

void apply_shadows(inout vec4 p_pixel)
{
	for(uint i = 0U; i < 8U; ++i)
	{
		if(shadows[i] > 0U)
		{
			// We only need to modify the U-component of the shadow texture
			// coordinates, since all shadow textures are stored in one
			// sequential line
			float t_u_offset = float(i) * (1.f/8.f);

			// Calculate new shadow texture coordinates
			vec2 t_shadowCoords = vec2(
				shadow_coords.x + t_u_offset,
				shadow_coords.y
			);

			// Fetch texel
			vec4 t_color = texture(shadow_tex, t_shadowCoords);

			// Blend it using alpha blending
			p_pixel = mix(p_pixel, t_color, t_color.a);
			p_pixel.a = 1.f;
		}
	}
}

void apply_depth(inout vec4 p_pixel)
{
    p_pixel = mix(fog_color, p_pixel, fog_factor);
    p_pixel.a = 1.f;
}


void main()
{
	// Discard fragment if the glyph was transparent
	if(is_transparent > 0U)
		discard;

    //fragmentColor = calc_pixel();
    vec4 clr = calc_pixel();

    apply_depth(clr);
    apply_shadows(clr);


    fragmentColor = clr;
}
